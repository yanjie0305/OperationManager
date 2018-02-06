package com.example.edaibu.operationmanager.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.example.edaibu.operationmanager.R;
import com.example.edaibu.operationmanager.bean.InfoBean;
import com.example.edaibu.operationmanager.bean.MessageBean;
import com.example.edaibu.operationmanager.util.LogUtils;
import com.example.edaibu.operationmanager.util.OverlayManager;
import com.example.edaibu.operationmanager.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${gyj} on 2017/9/20.
 */
public class MapFragment extends Fragment {
    private View view;
    public MapView mapView;
    public BaiduMap mBaiduMap;
    private UiSettings mUiSetting;
    private List<MessageBean> messageBeans;
    private String userLatitude, userLongitude;
    private Map<String, List<LatLng>> operationStaffMap = new HashMap<>();
    private Bitmap bitmap;
    private int[] colors = new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10};
    private List<List<Double>> coordinates;
    private PolylineOptions options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setMapCustomFile(getActivity(), "custom_config_1009.json");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.map_layout, container, false);
        mapView = (MapView) view.findViewById(R.id.bmapView);
        MapView.setMapCustomEnable(true);
        initMap();
        return view;

    }

    private void initMap() {
        // 地图设置
        mBaiduMap = mapView.getMap();
        //去掉放大缩小按钮
        mapView.showZoomControls(false);

        // 去掉logo
        int count = mapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mapView.getChildAt(i);
            if (child instanceof ZoomControls || child instanceof ImageView) {
                child.setVisibility(View.GONE);
            }
        }
        mUiSetting = mBaiduMap.getUiSettings();
        // 设置不允许旋转地图
        mUiSetting.setRotateGesturesEnabled(false);
        mUiSetting.setCompassEnabled(false);
        mUiSetting.setOverlookingGesturesEnabled(false);
        mUiSetting.setScrollGesturesEnabled(true);


    }

    public void getSuccessData(List<MessageBean> messageBeans, List<List<Double>> coordinates) {
        this.messageBeans = messageBeans;
        this.coordinates = coordinates;
        saveData(messageBeans);
        drawOperationStaffOnMap();


    }


    private void saveData(List<MessageBean> messageBeans) {
        operationStaffMap.clear();
        try {
            int size = messageBeans.size();
            for (int i = 0; i < size; i++) {
                MessageBean messageBean = messageBeans.get(i);
                String uid = messageBean.getUid();
//                 LogUtils.e("uid=" + uid);
                List<LatLng> latLngList = operationStaffMap.get(uid);
                if (latLngList == null) {
                    latLngList = new ArrayList<>();
                    operationStaffMap.put(uid, latLngList);
                }

                List<MessageBean.CoordsBean> coords = messageBean.getCoords();
                for (int j = 0; j < coords.size(); j++) {
                    try {
                        userLongitude = coords.get(j).getUserLatitude();
                        userLatitude = coords.get(j).getUserLongitude();
                    } catch (Exception e) {

                    }
//                     LogUtils.e("userLongitude=" + userLatitude + "*****userLatitude=" + userLongitude);
                    if (userLongitude.equals("0") && userLatitude.equals("0")) {
                        continue;
                    }
                    double lat = Double.parseDouble(userLongitude);
                    double lng = Double.parseDouble(userLatitude);
                    LatLng latLng = new LatLng(lat, lng);
                    if (!outOfChina(latLng)) {
                        latLngList.add(latLng);
                    }

                }


            }

            LogUtils.e("staffMap = " + operationStaffMap.toString());
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    // 判断坐标是否在中国
    public static boolean outOfChina(LatLng latLng) {
        double lat = latLng.latitude;
        double lon = latLng.longitude;
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        if ((119.962 < lon && lon < 121.750) && (21.586 < lat && lat < 25.463))
            return true;

        return false;
    }

    public void clearMap() {

        if (operationStaffMap != null) {
            operationStaffMap.clear();
        }

    }

    /**
     * 刷新地图上车辆
     */
    public void drawOperationStaffOnMap() {
        LogUtils.e("drawStaffOnMap");
        if (null == mBaiduMap) {
            return;

        }
        mBaiduMap.clear();

        final List<OverlayOptions> overlayOptions = new ArrayList<>();
        // 管理多个覆盖物
        final OverlayManager overlayManager = new OverlayManager(mBaiduMap) {
            @Override
            public List<OverlayOptions> getOverlayOptions() {
                return overlayOptions;
            }

            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }

            @Override
            public boolean onPolylineClick(Polyline polyline) {
                return true;
            }
        };

        drawOperationStaffs(overlayOptions, messageBeans);
        overlayManager.addToMap();

        overlayManager.zoomToSpan(); //仅对mark起作用
    }


    /**
     * 画所有人的轨迹
     *
     * @param overlayOptions
     */
    private void drawOperationStaffs(List<OverlayOptions> overlayOptions, List<MessageBean> messageBeans) {
        LogUtils.e("drawStaffs");
        if (null == mBaiduMap) {
            return;
        }
        mBaiduMap.clear();
        LogUtils.e("messageBeans=" + messageBeans.size());
        if (messageBeans.size() == 0) {
            Double lat = coordinates.get(0).get(1);
            Double lng = coordinates.get(0).get(0);
            LatLng latLng = new LatLng(lat, lng);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.setMapStatus(u);
            Toast.makeText(getActivity(), getString(R.string.no_data), Toast.LENGTH_SHORT).show();
        }

        for (Map.Entry<String, List<LatLng>> entry : operationStaffMap.entrySet()) {

            List<LatLng> latLngList = entry.getValue();
            for (int i = 0, len = messageBeans.size(); i < len; i++) {
                MessageBean bean = messageBeans.get(i);
                InfoBean infoBean = new InfoBean(bean.getName(), bean.getRoleId());
                int color = colors[i % colors.length];
                if (entry.getKey() == bean.getUid()) {
                    drawOneOperationStaff(overlayOptions, latLngList, infoBean, color);
                }

            }
        }


    }


    /**
     * 画一个人的轨迹
     *
     * @param overlayOptions
     */
    private void drawOneOperationStaff(List<OverlayOptions> overlayOptions, final List<LatLng> latLngList, InfoBean infoBean, int color) {
        LogUtils.e(latLngList.size() + "");
        if (null == latLngList || latLngList.size() < 2) {
            return;
        }

        if (latLngList.isEmpty()) {
            return;
        }
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(getMarkerInfoView(infoBean));
        // draw line
        options = new PolylineOptions();
        options.points(latLngList);
        options.width(10);
        options.zIndex(1000);
        options.color(getResources().getColor(color));
        // options.dottedLine(true);
        mBaiduMap.addOverlay(options);
        // draw bike icon
        LatLng latLng = latLngList.get(latLngList.size() - 1);
        MarkerOptions mo = new MarkerOptions().position(latLng).icon(bitmapDescriptor);
        overlayOptions.add(mo);

    }

    public void drawOneStaffLine(List<LatLng> latLngList, InfoBean infoBean) {
        mBaiduMap.clear();
        LogUtils.e("latLngList.size() = " + latLngList.size());
        if (null == latLngList || latLngList.size() < 2) {
            Toast.makeText(getActivity(), getString(R.string.is_0_or_only_one_data), Toast.LENGTH_SHORT).show();
            return;
        }
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(getMarkerInfoView(infoBean));
        // draw line
//        PolylineOptions options = new PolylineOptions();
        options.points(latLngList);
        options.width(10);
        options.zIndex(1000);
        options.color(getResources().getColor(R.color.bike_line));
        mBaiduMap.addOverlay(options);
        // draw staff icon
        LatLng latLng = latLngList.get(latLngList.size() - 1);
        MarkerOptions mo = new MarkerOptions().position(latLng).icon(bitmapDescriptor);
        mBaiduMap.addOverlay(mo);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().build()));
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(u);


    }


    /**
     * 自定义marker图标
     *
     * @param infoBean
     * @return
     */
    @NonNull
    private View getMarkerInfoView(InfoBean infoBean) {
        //创建InfoWindow展示的view
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.popup, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        tv_name.setText(infoBean.getName());
        if (infoBean.getRoleId().equals("1")) {
            bitmap = Util.readBitMap(getActivity(), R.mipmap.inspection);
        } else if (infoBean.getRoleId().equals("2")) {
            bitmap = Util.readBitMap(getActivity(), R.mipmap.repair);
        } else if (infoBean.getRoleId().equals("3")) {
            bitmap = Util.readBitMap(getActivity(), R.mipmap.allocation);
        }
        img.setImageBitmap(bitmap);
        return view;
    }

    @Override
    public void onDestroy() {
        LogUtils.e("onDestroy");
        super.onDestroy();
        mapView.onDestroy();
        mapView = null;
    }

    @Override
    public void onResume() {
        LogUtils.e("onResume");
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        LogUtils.e("onPause");
        super.onPause();
        mapView.onPause();
    }


    /**
     * 设置个性化地图config文件路径
     */
    public static void setMapCustomFile(Context context, String PATH) {
        FileOutputStream out = null;
        InputStream inputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets().open("customConfigdir/" + PATH);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            moduleName = context.getFilesDir().getAbsolutePath();
            File f = new File(moduleName + "/" + PATH);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e("moduleName----" + moduleName + "/" + PATH);
        MapView.setCustomMapStylePath(moduleName + "/" + PATH);

    }

}
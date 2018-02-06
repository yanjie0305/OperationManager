package com.example.edaibu.operationmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.example.edaibu.operationmanager.adapter.MyAdapter;
import com.example.edaibu.operationmanager.bean.AreaBean;
import com.example.edaibu.operationmanager.bean.CompanyNameBean;
import com.example.edaibu.operationmanager.bean.InfoBean;
import com.example.edaibu.operationmanager.bean.MessageBean;
import com.example.edaibu.operationmanager.bean.NewMessageBean;
import com.example.edaibu.operationmanager.bean.SecondMessageBean;
import com.example.edaibu.operationmanager.bean.SecondNewMessageBean;
import com.example.edaibu.operationmanager.constant.Constant;
import com.example.edaibu.operationmanager.fragment.MapFragment;
import com.example.edaibu.operationmanager.http.AreaHttp;
import com.example.edaibu.operationmanager.http.NameHttp;
import com.example.edaibu.operationmanager.service.MqttService;
import com.example.edaibu.operationmanager.util.DataHolder;
import com.example.edaibu.operationmanager.util.GsonUtils;
import com.example.edaibu.operationmanager.util.LogUtils;
import com.example.edaibu.operationmanager.view.MySpinner;

import java.util.ArrayList;
import java.util.List;

import static com.example.edaibu.operationmanager.service.MqttService.MY_TOPIC;
import static com.example.edaibu.operationmanager.service.MqttService.NEW_TOPIC;
import static com.example.edaibu.operationmanager.service.MqttService.SECOND_NEW_MSG;
import static com.example.edaibu.operationmanager.service.MqttService.SECOND_NEW_TOPIC;
import static com.example.edaibu.operationmanager.service.MqttService.SECOND_TOPIC;
import static com.example.edaibu.operationmanager.service.MqttService.STAFF_INFO;
import static com.example.edaibu.operationmanager.service.MqttService.USER_FLG;
import static com.example.edaibu.operationmanager.service.MqttService.actionStop;


public class MainActivity extends AppCompatActivity {

    public static String ZHIYING = "02";
    public static String LIANYING = "03";
    public static String JINGQU = "04";
    private String type, companyId, device, newTopic, myTopic, areaId,mobile;
    private DrawerLayout drawerLayout;
    private MapFragment mapFragment;
    private MySpinner spinner1, spinner2, spinner3, spinner4;
    private MyAdapter myAdapter,adapter,areaAdapter,staffNameAdapter;
    private List<String> companyNameList,staffNameList;
    private List<CompanyNameBean.Data> dataLists;
    boolean isInitSp = true;
    private Intent intent;
    private List<MessageBean> messageBeanList;
    private List<AreaBean.DataBean> areaDataList;
    public List<List<Double>> coordinates;
    private List<LatLng> latLngLists;
    private InfoBean infoBean;
    private Handler mHandler = new Handler() {
        //刷新界面的Handler
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.GET_NAME_SUCCESS:
                    CompanyNameBean companyNameBean = (CompanyNameBean) msg.obj;
                    if (companyNameBean.getData() == null) {
                        return;
                    }
                    if (companyNameBean.getStatus() == 200) {
                        dataLists = companyNameBean.getData();
                        initSpinner2Data();
                    }
                    break;
                case Constant.GET_AREA_SUCCESS:
                   AreaBean areaBean = (AreaBean) msg.obj;
                    if (areaBean.getData() == null) {
                        return;
                    }
                    if (areaBean.getCode() == 200 && areaBean.getData() != null) {
                        areaDataList = areaBean.getData();
                        initSpinner3Data();
                    }
                    break;
                case Constant.REQUST_ERROR:
                    Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT);
                    break;
            }
            super.handleMessage(msg);
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.e("onCreate");
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设备标识
        device = Build.DEVICE;
        register();//注册广播
        initView();//初始化方法
        initMap();//开启地图
        initData();//初始化数据


    }

    /**
     * 注册广播
     */
    private void register() {
       IntentFilter filter = new IntentFilter();
        filter.addAction(MqttService.EXTRA_DATA);
        filter.addAction(MqttService.NEW_EXTRA_DATA);
        filter.addAction(MqttService.SECOND_EXTRA_DATA);
        filter.addAction(MqttService.SECOND_NEW_EXTRA_DATA);
        //注册广播监听
        registerReceiver(pushReceiver, filter);
        intent = new Intent();
    }

    public BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收并处理第一次订阅返回的数据
            if (MqttService.EXTRA_DATA.equals(intent.getAction())) {
                //  msg = intent.getStringExtra(MqttService.MSG);
                staffNameList = new ArrayList<>();
               String msg = DataHolder.getInstance().getData();
                LogUtils.e("msg=" + msg);
                if (msg == null) {
                    return;
                }
                List<MessageBean> messageBeans = GsonUtils.jsonToList(msg, MessageBean[].class);
                messageBeanList = new ArrayList<>();
                LogUtils.e("messageBeans=" + messageBeans.size());
                for (int i = 0; i < messageBeans.size(); i++) {
                   MessageBean messageBean = messageBeans.get(i);
                    messageBeanList.add(messageBean);
                    staffNameList.add(messageBean.getName());
                }
                initSpinner4Data();
                mapFragment.getSuccessData(messageBeanList, coordinates);
                //接收并处理所有运维人员实时的数据
            } else if (MqttService.NEW_EXTRA_DATA.equals(intent.getAction())) {
               String newMsg = intent.getStringExtra(MqttService.NEW_MSG);
                LogUtils.e("newMsg=" + newMsg);
                if (newMsg == null) {
                    return;
                }
                List<MessageBean.CoordsBean> coordsList = new ArrayList<>();
                NewMessageBean newMessageBean = GsonUtils.parseJsonWithGson(newMsg, NewMessageBean.class);
                MessageBean.CoordsBean coordsBean = new MessageBean.CoordsBean(newMessageBean.getUserLongitude(), newMessageBean.getUserLatitude(), newMessageBean.getLogTime());
                coordsList.add(coordsBean);
               MessageBean messageBean = new MessageBean(newMessageBean.getRoleId(), newMessageBean.getName(), newMessageBean.getUid(), newMessageBean.getMobile(), coordsList);
                if (!staffNameList.contains(newMessageBean.getName())) {
                    staffNameList.add(newMessageBean.getName());
                }
                staffNameAdapter.notifyDataSetChanged();
                // initSpinner4Data();
                messageBeanList.add(messageBean);
                LogUtils.e("messageBeanList = " + messageBeanList.toString());
                mapFragment.getSuccessData(messageBeanList, coordinates);
                //接收并处理一个运维人员的行驶数据
            } else if (MqttService.SECOND_EXTRA_DATA.equals(intent.getAction())) {
               String secondMsg = DataHolder.getInstance().getData();
                LogUtils.e("secondMsg = " + secondMsg);
                if (secondMsg == null) {
                    return;
                }

                SecondMessageBean secondMessageBean = GsonUtils.parseJsonWithGson(secondMsg, SecondMessageBean.class);
                List<SecondMessageBean.LocationBean> location = secondMessageBean.getLocaltion();
                latLngLists = new ArrayList<>();
                for (int i = 0; i < location.size(); i++) {

                    if (location.get(i).getLatitude() == 0 && location.get(i).getLongitude() == 0) {
                        continue;
                    }

                    LogUtils.e("lat = " + location.get(i).getLatitude() + "*******lng = " + location.get(i).getLongitude());
                    latLngLists.add(new LatLng(location.get(i).getLatitude(), location.get(i).getLongitude()));
                }

                infoBean = new InfoBean(secondMessageBean.getName(),secondMessageBean.getRoleId());
                 mapFragment.drawOneStaffLine(latLngLists, infoBean);
                //接收并处理实时数据
            } else if (MqttService.SECOND_NEW_EXTRA_DATA.equals(intent.getAction())) {
                String secondNewMsg = intent.getStringExtra(SECOND_NEW_MSG);
                LogUtils.e("secondNewMsg = " + secondNewMsg);
                if (secondNewMsg == null) {
                    return;
                }
                SecondNewMessageBean secondNewMessageBean = GsonUtils.parseJsonWithGson(secondNewMsg, SecondNewMessageBean.class);
                if (secondNewMessageBean.getLatitude()==0&&secondNewMessageBean.getLongitude()==0){
                    return;
                }
                latLngLists.add(new LatLng(secondNewMessageBean.getLatitude(), secondNewMessageBean.getLongitude()));
                 mapFragment.drawOneStaffLine(latLngLists,infoBean);

            }

        }


    };

    /**
     * Spinner2
     */

    private void initSpinner2Data() {
        LogUtils.e("initSpinner2Data");
        spinner2.setSelection(0);
        companyId = dataLists.get(0).getId();
        AreaHttp.getArea(companyId, mHandler);

        companyNameList.clear();
        if (dataLists == null || dataLists.size() == 0) {
            adapter.notifyDataSetChanged();
            return;
        }
        for (int i = 0; i < dataLists.size(); i++) {
            companyNameList.add(dataLists.get(i).getName());
        }
        isInitSp = true;
        adapter.notifyDataSetChanged();
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LogUtils.e("onItemSelected=" + companyId);
                if (isInitSp) {
                    isInitSp = false;
                    return;
                }

                mapFragment.clearMap();
                companyId = dataLists.get(i).getId();
                LogUtils.e("companyId=" + companyId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        AreaHttp.getArea(companyId, mHandler);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                LogUtils.e("onNothingSelected" + companyId);

            }
        });
    }

    /**
     * Spinner3
     */

    private void initSpinner3Data() {

        LogUtils.e("initSpinner3Data");
        ArrayList<String> areaList = new ArrayList<>();
        if (areaDataList == null || areaDataList.size() == 0) {
            isInitSp = false;
            return;
        }
        for (int i = 0; i < areaDataList.size(); i++) {
            areaList.add(areaDataList.get(i).getName());

        }
        spinner3.setSelection(0);
        areaId = areaDataList.get(0).getId();
        coordinates = areaDataList.get(0).getCoordinates();
        LogUtils.e("coordinates = " + coordinates);
        LogUtils.e("areaId = " + areaId);
        // myTopic = "/areaId/coords/first/" + device + "/" + "599b9f05ee5c397e98702b53";
        myTopic = "/areaId/coords/first/" + device + "/" + areaId;
        newTopic = "/areaId/newest/" + areaId;
        //发广播  订阅的topic
        intent.setAction(USER_FLG);
        intent.putExtra(MY_TOPIC, myTopic);
        intent.putExtra(NEW_TOPIC, newTopic);
        LogUtils.e("myTopic=" + myTopic);
        LogUtils.e("newTopic=" + newTopic);
        sendBroadcast(intent);

        LogUtils.e(areaList.toString());
        isInitSp = true;
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mapFragment.clearMap();
                if (isInitSp) {
                    isInitSp = false;
                    return;
                }
                coordinates = areaDataList.get(position).getCoordinates();
                LogUtils.e("coordinates = " + coordinates);
                areaId = areaDataList.get(position).getId();
                LogUtils.e("areaId = " + areaId);
                myTopic = "/areaId/coords/first/" + device + "/" + areaId;
                newTopic = "/areaId/newest/" + areaId;
                //发广播  订阅的topic
                intent.setAction(USER_FLG);
                intent.putExtra(MY_TOPIC, myTopic);
                intent.putExtra(NEW_TOPIC, newTopic);
                sendBroadcast(intent);
                initSpinner4Data();
                 // drawerLayout.closeDrawer(Gravity.LEFT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LogUtils.e("onNothingSelected");

            }
        });
        areaAdapter = new MyAdapter(getApplicationContext(), areaList);
        spinner3.setAdapter(areaAdapter);

    }
    /**
     * Spinner4
     */

    private void initSpinner4Data() {
        LogUtils.e("staffNameList = " + staffNameList.size());
        if (staffNameList.size() == 0) {
            spinner4.setVisibility(View.GONE);
            return;
        } else {
            spinner4.setVisibility(View.VISIBLE);
        }
        staffNameAdapter = new MyAdapter(MainActivity.this, staffNameList);
        spinner4.setAdapter(staffNameAdapter);
        isInitSp = true;
        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isInitSp) {
                     isInitSp = false;
                    return;
                }
                String staffName = staffNameList.get(position);
                for (int i = 0; i < messageBeanList.size(); i++) {
                    if (messageBeanList.get(i).getName().equals(staffName)) {
                        mobile = messageBeanList.get(i).getMobile();
                    }
                }
                LogUtils.e("mobile = " + mobile);
                String secondTopic = "/areaId/coords/second/" + areaId + "/" + mobile;
                String secondNewTopic = "/areaId/personal/" + areaId + "/" + mobile;
                // Intent intent = new Intent();
                intent.setAction(STAFF_INFO);
                intent.putExtra(SECOND_TOPIC, secondTopic);
                intent.putExtra(SECOND_NEW_TOPIC, secondNewTopic);
                sendBroadcast(intent);
                LogUtils.e("secondTopic = " + secondTopic);
                 drawerLayout.closeDrawer(Gravity.LEFT);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * 开启地图
     */
    private void initMap() {
        LogUtils.e("initMap");
        mapFragment = new MapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_map, mapFragment);
        fragmentTransaction.commitAllowingStateLoss();

    }


    /**
     * 初始化view方法
     */
    private void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        spinner1 = (MySpinner) findViewById(R.id.spinner1);
        spinner2 = (MySpinner) findViewById(R.id.spinner2);
        spinner3 = (MySpinner) findViewById(R.id.spinner3);
        spinner4 = (MySpinner) findViewById(R.id.spinner4);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        LogUtils.e("initData");
        List<String> list = new ArrayList<>();
        companyNameList = new ArrayList<>();
        list.add(getString(R.string.directly_manufacturer));
        list.add(getString(R.string.pool));
        list.add(getString(R.string.scenic));
        myAdapter = new MyAdapter(MainActivity.this, list);
        spinner1.setAdapter(myAdapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mapFragment.clearMap();

                String itemName = spinner1.getItemAtPosition(i).toString();
                if (getString(R.string.directly_manufacturer).equals(itemName)) {
                    type = ZHIYING;
                    NameHttp.getName(type, mHandler);
                } else if (getString(R.string.pool).equals(itemName)) {
                    type = LIANYING;
                    NameHttp.getName(type, mHandler);
                } else if (getString(R.string.scenic).equals(itemName)) {
                    type = JINGQU;
                    NameHttp.getName(type, mHandler);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        adapter = new MyAdapter(MainActivity.this, companyNameList);
        spinner2.setAdapter(adapter);


    }

    /**
     * 遥控左右控制键
     *
     * @param keyCode
     * @param event
     * @return
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            //点击右键
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                LogUtils.e("KEYCODE_DPAD_RIGHT");
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;
            //点击左键
            case KeyEvent.KEYCODE_DPAD_LEFT:
                LogUtils.e("KEYCODE_DPAD_LEFT");
                drawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            //点击"+"键 地图放大
            case KeyEvent.KEYCODE_VOLUME_UP:
                LogUtils.e("KEYCODE_VOLUME_UP");
                mapFragment.mapView.setFocusable(true);
                MapStatusUpdate statusUpdate = MapStatusUpdateFactory.zoomIn();
                mapFragment.mBaiduMap.animateMapStatus(statusUpdate);
                return true;
            //点击"-"键 地图减小
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                LogUtils.e("KEYCODE_VOLUME_DOWN");
                mapFragment.mapView.setFocusable(true);
                MapStatusUpdate statusUpdates = MapStatusUpdateFactory.zoomOut();
                mapFragment.mBaiduMap.animateMapStatus(statusUpdates);
                return true;
            default:
                break;
        }


        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("onResume");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("onDestroy");
        unregisterReceiver(pushReceiver);
        actionStop(this);

    }

}

package com.example.edaibu.operationmanager.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;


import com.example.edaibu.operationmanager.R;
import com.example.edaibu.operationmanager.constant.HttpConstant;
import com.example.edaibu.operationmanager.util.DataHolder;
import com.example.edaibu.operationmanager.util.LogUtils;
import com.example.edaibu.operationmanager.util.Util;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


/**
 * MQTT接收推送的service
 **/
public class MqttService extends Service {
    /**
     * 接收到广播
     */
    public static final String USER_FLG = "my_topic";
    public static final String EXTRA_DATA = "extra_data";
    public static final String NEW_EXTRA_DATA = "new_extra_data";
    public static final String SECOND_NEW_EXTRA_DATA = "second_new_extra_data";
    public static final String SECOND_EXTRA_DATA = "second_extra_data";
    public static final String STAFF_INFO = "staff_info";
    /**
     * 消息中的数据
     */
    public static final String NEW_MSG = "newMsg";
    public static final String SECOND_NEW_MSG = "secondNewMsg";

    /**
     * 第一次订阅消息的topic
     */
    public static final String MY_TOPIC = "myTopic";
    /**
     * 第二次订阅消息的topic
     */
    public static final String NEW_TOPIC = "newTopic";

    public static final String SECOND_TOPIC = "secondTopic";
    public static final String SECOND_NEW_TOPIC = "secondNewTopic";
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private MqttAndroidClient client;
    private static MqttConnectOptions conOpt;
    private String host = HttpConstant.HOST;
    private String userName = HttpConstant.UESR_NAME;
    private String passWord = "";
    /**
     * 推送消息的标识
     **/
    private static String topics, myTopic, newTopics,secondTopic,secondNewTopic;
    private static String clientId = HttpConstant.CLIENT_ID;
    private static final String ACTION_START = clientId + ".START";
    private static final String ACTION_STOP = clientId + ".STOP";
    //超时时间（秒）
    private int CONNECTION_TIMEOUT = 10;
    //心跳包间隔（秒）
    private int KEEP_ALIVE_INTERVAL = 30;
    private static boolean mStarted = false;
    private MyReceiver myReceiver;
    private IntentFilter filter;
    private Intent intent;
    private String device;



    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("onCreate");
        //注册广播
        register();
        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取设备唯一标识
        device = Build.DEVICE;
        clientId = clientId + device;
        LogUtils.e("clientId=" + clientId);

    }
    //注册广播
    private void register() {
        myReceiver = new MyReceiver();
        filter = new IntentFilter();
        filter.addAction(MqttService.USER_FLG);
        filter.addAction(MqttService.STAFF_INFO);
        registerReceiver(myReceiver, filter);
//         intent = new Intent();
        intent = new Intent(Intent.ACTION_MAIN,null);
        // intent.addCategory(Intent.CATEGORY_LAUNCHER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("onStartCommand");
        topics = device;
        if (intent == null) {
            return START_STICKY;
        }
        if (ACTION_START.equals(intent.getAction())) {
            LogUtils.e("topics=" + topics);
            if (TextUtils.isEmpty(topics)) {
                return START_STICKY;
            }
            init();

        } else if (ACTION_STOP.equals(intent.getAction())) {
            LogUtils.e("stopSelf");
            disconnect();
            topics = "";
            mStarted = false;
            return START_NOT_STICKY;
        }
        return super.onStartCommand(intent, flags, startId);
    }


    // Static method to start the service
    public static void actionStart(Context ctx) {
        Intent i = new Intent(ctx, MqttService.class);
        i.setAction(ACTION_START);
        ctx.startService(i);
    }

    // Static method to stop the service
    public static void actionStop(Context ctx) {
        Intent i = new Intent(ctx, MqttService.class);
        i.setAction(ACTION_STOP);
        ctx.startService(i);
    }

    private synchronized void init() {
        if (mStarted) {
            return;
        }
        LogUtils.e("init");
        mStarted = true;
        initConnection();
    }

    private void initConnection() {
        // 服务器地址（协议+地址+端口号）
        client = new MqttAndroidClient(this, host, clientId);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);
        conOpt = new MqttConnectOptions();
        //设置自动重连
        //conOpt.setAutomaticReconnect(true);
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(CONNECTION_TIMEOUT);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);
        // 用户名
        conOpt.setUserName(userName);
        // 密码
        conOpt.setPassword(passWord.toCharArray());
        LogUtils.e("topics= " + topics);
        conOpt.setWill(topics, clientId.getBytes(), 1, false);
        doClientConnection(conOpt);
    }


    private void reconnectIfNecessary() {
        LogUtils.e("重连 ");
        if (isNetWork()) {
            doClientConnection(conOpt);
            LogUtils.e("网络链接正常：");
        } else {
            LogUtils.e("没有网络：");
            //disconnect();
        }
    }

    @Override
    public void onDestroy() {
        LogUtils.e("onDestroy");
        disconnect();
        super.onDestroy();
    }

    private void disconnect() {
        try {
            if (client != null) {
                client.disconnect();
                client = null;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接MQTT服务器
     */
    private synchronized void doClientConnection(MqttConnectOptions mqttConnectOptions) {

        if (client != null && !client.isConnected() && isNetWork() && !TextUtils.isEmpty(topics)) {

            try {
                client.connect(mqttConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            if (client == null) {
                return;
            }
            LogUtils.e("连接成功 ");
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            // 连接失败，重连
            LogUtils.e("连接失败 " + arg1.toString());
            reconnectIfNecessary();
        }
    };
    private String secondNewMsg;
    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            LogUtils.e("myTopic=" + myTopic);
            LogUtils.e("topic=" + topic);
            if (myTopic.equals(topic)) {
                //解析第一次消息
               String msg = new String(message.getPayload());
                LogUtils.e("messageArrived:" + msg);
                if (msg == null) {
                    return;
                }
                DataHolder.getInstance().setData(msg);
                intent.setAction(EXTRA_DATA);
                sendBroadcast(intent);
                //取消第一次
                client.unsubscribe(myTopic);
                //订阅第二次
                client.subscribe(newTopics, 1);


            } else if (newTopics.equals(topic)) {
                //解析第二次消息
                LogUtils.e("newTopics=" + newTopics);
               String newMsg = new String(message.getPayload());
                LogUtils.e("newMsg = " + newMsg);
                if (newMsg ==null){
                    return;
                }

                intent.setAction(NEW_EXTRA_DATA);
                intent.putExtra(NEW_MSG, newMsg);
                sendBroadcast(intent);

            } else if (secondTopic.equals(topic)){
                LogUtils.e("secondTopic=" + secondTopic);
                String secondMsg = new String(message.getPayload());
                LogUtils.e("secondMsg = "+secondMsg);
                if (secondMsg==null){
                    return;
                }
                intent.setAction(SECOND_EXTRA_DATA);
                DataHolder.getInstance().setData(secondMsg);
                sendBroadcast(intent);
                //取消第一次
                client.unsubscribe(secondTopic);
                //订阅第二次
                client.subscribe(secondNewTopic, 1);

            }else if (secondNewTopic.equals(topic)){

                LogUtils.e("secondNewTopics=" + secondNewTopic);
                secondNewMsg = new String(message.getPayload());
                LogUtils.e("secondNewMsg = " + secondNewMsg);
                if (secondNewMsg ==null){
                    return;
                }

                intent.setAction(SECOND_NEW_EXTRA_DATA);
                intent.putExtra(SECOND_NEW_MSG, secondNewMsg);
                sendBroadcast(intent);
            }
            else {

                Toast.makeText(getApplicationContext(),getString(R.string.replay),Toast.LENGTH_SHORT).show();

                return;
            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            LogUtils.e("messageArrived:" + arg0.toString());

        }

        @Override
        public void connectionLost(Throwable arg0) {
            // 失去连接，重连
            LogUtils.e("push链接断开:");
            reconnectIfNecessary();
        }
    };

    /**
     * 判断网络是否连接
     */
    private boolean isNetWork() {
        info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isConnected()) ? true : false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MqttService.USER_FLG.equals(intent.getAction())) {
                myTopic = intent.getStringExtra(MqttService.MY_TOPIC);
                newTopics = intent.getStringExtra(MqttService.NEW_TOPIC);
                LogUtils.e("myTopic=" + myTopic);
                LogUtils.e("newTopics = " + newTopics);
                LogUtils.e("client="+client);
                //订阅topic
                try {
                    client.subscribe(myTopic, 1);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }else if (MqttService.STAFF_INFO.equals(intent.getAction())){
               secondTopic = intent.getStringExtra(MqttService.SECOND_TOPIC);
                secondNewTopic = intent.getStringExtra(MqttService.SECOND_NEW_TOPIC);
                LogUtils.e("secondTopic = "+secondTopic);
                //订阅topic
                try {
                    client.subscribe(secondTopic, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
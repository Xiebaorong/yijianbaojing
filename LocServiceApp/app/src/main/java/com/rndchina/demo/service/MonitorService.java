package com.rndchina.demo.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.rndchina.demo.BuildConfig;
import com.rndchina.demo.base.LoadCallBack;
import com.rndchina.demo.bean.GpsData;
import com.rndchina.demo.util.APPUrl;
import com.rndchina.demo.util.Constants;
import com.rndchina.demo.util.GsonUtil;
import com.rndchina.demo.util.LocalDataUtils;
import com.rndchina.demo.util.MyDataCoding;
import com.rndchina.demo.util.OkHttpManager;
import com.rndchina.demo.util.PhoneInfoUtils;
import com.rndchina.demo.util.UdpSendHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by dell on 2017-8-28.
 */

public class MonitorService extends Service {
    private static final String TAG = "MonitorService";
    private final IBinder binder = new LocalBinder();
    PowerManager.WakeLock m_wklk;
    MyDataCoding myDataCoding;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    String devName;
    private final static String SCREEN_ON = "SCRENN ON";
    private final static String SCREEN_OFF = "SCREEN_OFF";
    private final static String SCREEN_UNLOCK= "SCREEN_UNLOCK";
    private  String screenStatus=SCREEN_ON;

    private PowerManager pm;

    private SharedPreferences.Editor editor;

    private List<Integer> list = new ArrayList<>();
    public class LocalBinder extends Binder {
        MonitorService getService(){
            return MonitorService.this;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (null == m_wklk)
        {
            m_wklk = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass()
                    .getCanonicalName());
            if (null != m_wklk)
            {
                m_wklk.acquire();
            }
        }
        editor = getSharedPreferences("data",MODE_PRIVATE).edit();

        myDataCoding = new MyDataCoding();
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(new MyLocationListener());
        String phoneno = PhoneInfoUtils.getInstance().getNativePhoneNumber();
        if(TextUtils.isEmpty(phoneno)) {
            devName = LocalDataUtils.getInstance().getDeviceName();
        }else{
            devName = phoneno;
        }


        initParams();
        startMonitor();
        registerListener();

    }


    /**
     * 启动screen状态广播接收器
     */
    private void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mBatInfoReceiver, filter);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("System","关闭服务");
        if (m_wklk != null) {
            m_wklk.release();
            m_wklk = null;
        }
        stopMonitor();
        //当界面销毁了之后，记得解除注册广播接收者
        unregisterReceiver(mBatInfoReceiver);
        UdpSendHelper.getInstance().exit();

    }

    private void initParams() {
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        mLocationOption.setInterval(30*1000);// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms）
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

    }


    public void startMonitor() {
        if (BuildConfig.DEBUG) Log.d(TAG, "start monitor location");
        if (!mLocationClient.isStarted()) {
            mLocationClient.startLocation();
        }else {
            Log.d("LocSDK3", "locClient is null or not started");
        }

    }

    public void stopMonitor() {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "stop monitor location");
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }


    class MyLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    LatLng latLng = new LatLng(aMapLocation.getLatitude(),
                            aMapLocation.getLongitude());//取出经纬度
                    StringBuffer sb = new StringBuffer(256);
                    sb.append("time : ");
                    sb.append(aMapLocation.getTime());
                    sb.append("\nerror code : ");
                    sb.append(aMapLocation.getLocationType());
                    sb.append("\nlatitude : ");
                    sb.append(aMapLocation.getLatitude());
                    sb.append("\nlontitude : ");
                    sb.append(aMapLocation.getLongitude());
                    sb.append("\ncity : ");
                    sb.append(aMapLocation.getCity());
                    Date  date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String sendtime = sdf.format(date);
                    GpsData gpsData = new GpsData();
                    Constants.TIME=System.currentTimeMillis();
                    Constants.XZB=aMapLocation.getLongitude();
                    Constants.YZB=aMapLocation.getLatitude();
//                    editor.putString("xzb", Constants.XZB+"");
//                    editor.putString("yzb", Constants.YZB+"");
//                    editor.putLong("time",System.currentTimeMillis());
                    Log.e("经纬度", "服务: "+aMapLocation.getLatitude()+"++++++++"+aMapLocation.getLongitude());
                    gpsData.setdLatitude(aMapLocation.getLatitude());
                    gpsData.setdLongitude(aMapLocation.getLongitude());
                    gpsData.setiSpeed(aMapLocation.getSpeed()+"");
                    gpsData.setiDirection(aMapLocation.getBearing()+"");
                    gpsData.setiAltitude(aMapLocation.getAltitude()+"");
                    gpsData.setiPrecision(aMapLocation.getAccuracy()+"");
                    gpsData.setTerminal(devName);
                    gpsData.setDateTime(sendtime);
                    gpsData.setScreenState(screenStatus);
                    String jsonstr =GsonUtil.beanToJSONString(gpsData);
                    Log.e("经纬度", "服务json: jsonstr"+jsonstr);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("locinfo", jsonstr);
                    OkHttpManager.getInstance().postRequest(APPUrl.SENDGPS, new LoadCallBack<String>(getApplicationContext()) {
                                @Override
                                public void onSuccess(Call call, Response response, String s) {
                                    Log.d("MonitorService", s);
                                    EventBus.getDefault().post("定位信息发送中...");
                                }

                                @Override
                                public void onEror(Call call, int statusCode, Exception e) {
                                    Log.e("lgz", "Exception = " + e.getMessage());
                                    EventBus.getDefault().post(e.getMessage());
                                }

                                @Override
                                public void onFailure(Call call, IOException e) {
                                    EventBus.getDefault().post(e.getMessage());
                                }
                            }
                            , params);
//                    byte[] buffer =myDataCoding.GpsFram(devName,aMapLocation.getLatitude(),aMapLocation.getLongitude(),(int)aMapLocation.getSpeed(),(int)aMapLocation.getBearing(),aMapLocation.getTime()+"");
//                    UdpSendHelper.getInstance().addBuffer(buffer);
                } else {
                    if(System.currentTimeMillis()-Constants.TIME>5*60000){
                        Constants.XZB=0.0;
                        Constants.YZB=0.0;
                    }
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());

                    EventBus.getDefault().post("定位失败");
//                    byte[] buffer =myDataCoding.GpsFram(devName,0,0,0,0,"");
//                    UdpSendHelper.getInstance().addBuffer(buffer);
                    //XLog.e(MSG, "AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                }
            }
            editor.commit();
        }
    }


    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            Log.e(TAG, "onReceive: "+action );
            if(Intent.ACTION_SCREEN_ON.equals(action)){
                Log.d("lyj", "-----------------screen is on...");
                screenStatus = SCREEN_ON;
            }else if(Intent.ACTION_SCREEN_OFF.equals(action)){
                Log.d("lyj", "----------------- screen is off...");
                screenStatus = SCREEN_OFF;
            }else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                Log.d("lyj", "----------------- screen is off...");
                screenStatus = SCREEN_UNLOCK;

            }

        }

    };


}

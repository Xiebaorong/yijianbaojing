package com.rndchina.demo.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;

import com.rndchina.demo.R;
import com.rndchina.demo.base.LoadCallBack;
import com.rndchina.demo.base.MyLocListener;
import com.rndchina.demo.bean.SosMsg;
import com.rndchina.demo.util.APPUrl;
import com.rndchina.demo.util.Constants;
import com.rndchina.demo.util.GsonUtil;
import com.rndchina.demo.util.LocationUtil;
import com.rndchina.demo.util.OkHttpManager;
import com.rndchina.demo.util.PhoneInfoUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by xie on 2018/1/8.
 */

public class VolumeService extends AccessibilityService implements MyLocListener {
    private static final String TAG = "VolumeService";
    private String dhhm;
    private double yzb;
    private double xzb;//经度
    private Vibrator mVibrator;////创建震动服务对象
    private Timer timer = new Timer();
//    private Notification notice=null;
//    private NotificationManager manager=null;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: 开启" );
        mVibrator=(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        LocationUtil instance = LocationUtil.getInstance(this);
        instance.startMonitor();
        instance.addMyLocListener(this);
//        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);;
//        notice = new Notification(R.drawable.logo,"", System.currentTimeMillis());
//        notice.contentView = new RemoteViews(getPackageName(),R.layout.report_progress_layout);
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }
    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mVibrator.vibrate(new long[]{0, 500, 200, 300}, -1);
                http();

            }
        };

        if(event.getAction()==KeyEvent.ACTION_DOWN){
            if(timer==null){
                timer = new Timer();
            }
            timer.schedule(task,5000);
//            Log.e(TAG, "onKeyEvent: timer"+timer );

        }
        if (event.getAction()==KeyEvent.ACTION_UP){
//            Log.e(TAG, "onKeyEvent: timer"+timer );
            timer.cancel();
            timer=null;
            task.cancel();
            System.gc();
        }
        return false;

    }


    /**
     * 用于一键报警网络请求
     */
    private void http() {
        File file = new File("");
        dhhm = PhoneInfoUtils.getInstance().toDhhm();
        SosMsg.ResultBean sosMsg = new SosMsg.ResultBean();
        sosMsg.setZt("99");
        sosMsg.setDhhm(dhhm);
        sosMsg.setGyzb(yzb);
        sosMsg.setGxzb(xzb);
        if(xzb==0.0&&yzb==0.0){
            sosMsg.setGyzb(Constants.YZB);
            sosMsg.setGxzb(Constants.XZB);
            Log.e(TAG, "onItemClick: ------yzb"+Constants.YZB );
        }else {
            Log.e(TAG, "onItemClick:====== xzb"+xzb );
        }
        OkHttpManager.getInstance().postFileRequest(APPUrl.SEND,sosMsg,file, new LoadCallBack<String>(getApplicationContext()) {
            @Override
            public void onSuccess(Call call, Response response, String result) {
                SosMsg sosMsg = GsonUtil.jsontoBean(result);
                int code = sosMsg.getCode();
                Log.e(TAG, "onSuccess: code"+code );
                if(code==1){
//                    notice.contentView.setTextViewText(R.id.report_text,"一键报警成功");
//                    manager.notify(1, notice);
                    sendNotification();
                    Log.e(TAG, "onSuccess: 报警成功");
                }
            }
            @Override
            public void onEror(Call call, int statusCode, Exception e) {
                Log.e(TAG, "onEror: e"+e.getMessage() );
            }
            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }


    @Override
    public void onSendMyLoc(AMapLocation aMapLocation, LocationUtil.MLocation mLoc) {
        this.yzb = mLoc.latitude;
        this.xzb = mLoc.longitude;
    }
    private void sendNotification() {
        NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("警情推送管理提示您:")
                .setContentText("一键报警成功");
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        notifyManager.notify(1, builder.build());
    }

}

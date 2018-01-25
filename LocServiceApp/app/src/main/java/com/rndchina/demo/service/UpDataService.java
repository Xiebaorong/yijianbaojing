package com.rndchina.demo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;


import com.rndchina.demo.BuildConfig;
import com.rndchina.demo.R;
import com.rndchina.demo.base.GlobalValues;
import com.rndchina.demo.util.LocalDataUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpDataService extends Service {
    final private static int DOWN_LOADING=0;//正在下载
    final private static int DOWN_LOAD_FINISH=1;//下载完成
    final  private static int DOWN_FAIL=2;
    private int progress=0;//下载进度
    private String apk_url;
    private String name;
    private NotificationManager manager=null;
    private Notification notice=null;
    private final static String TAG= UpDataService.class.getSimpleName();

    /**
     * 下载保存路径
     */
    private String mSavePath;

    public UpDataService() {
    }

    @Override
    public void onCreate(){
        SharedPreferences sp = UpDataService.this.getSharedPreferences("user", Context.MODE_PRIVATE);
        apk_url = sp.getString("apk_url","empty");
        name = sp.getString("name","empty");
        if(!TextUtils.isEmpty(apk_url)){
            Log.e(TAG, "onCreate: 开始下载");
            apk_url = "http://"+ LocalDataUtils.getInstance().getServerIp()+":"+LocalDataUtils.getInstance().getServerPort()+apk_url;
            Log.e(TAG, "下载地址: apk_url"+apk_url );
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notice = new Notification(R.drawable.logo,"", System.currentTimeMillis());
            notice.contentView = new RemoteViews(getPackageName(),R.layout.download_progress_layout);
            notice.contentView.setProgressBar(R.id.progress_bar,100, 0, false);
            downLoadFile();
//            new downloadApkThread().start();
        }else{
            Log.e(TAG, "onCreate: 停止下载");
            stopSelf();
        }


    }
    private Handler mHandler = new Handler(){
       @Override
       public void handleMessage(Message msg){
           switch (msg.what){
               case DOWN_LOADING:
                   notice.contentView.setProgressBar(R.id.progress_bar,100, progress, false);
                   Log.e(TAG, "handleMessage: progress"+progress );
                   notice.contentView.setTextViewText(R.id.progress_text,progress+"%");
//                   notice.contentView.setTextViewText(R.id.report,"警情推送管理下载中...");
                   manager.notify(1, notice);
                   break;
               case DOWN_LOAD_FINISH:

                       installApk();
                       stopSelf();
                   break;
               case DOWN_FAIL:
                   notice.contentView.setTextViewText(R.id.progress_text,"fail");
                   manager.notify(1, notice);
                   EventBus.getDefault().post(GlobalValues.DOWN_FAIL);
                   stopSelf();
                   break;
           }
       }
    };
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public <T> void downLoadFile() {
        // 判断SD卡是否存在，并且是否具有读写权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            // 获得存储卡的路径
            String sdpath = Environment.getExternalStorageDirectory() + "/";
            mSavePath = sdpath + "ezplug";
            File file = new File(mSavePath);
            // 判断文件目录是否存在
            if (!file.exists())
            {
                file.mkdir();
            }
            OkHttpClient mOkHttpClient = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS)
                    .readTimeout(10000, TimeUnit.MILLISECONDS)
                    .writeTimeout(10000,TimeUnit.MILLISECONDS).build();
            final File apkfile = new File(mSavePath, name+".apk");
            if (apkfile.exists()) {
                apkfile.delete();
            }
            final Request request = new Request.Builder().url(apk_url).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.toString());
                    mHandler.sendEmptyMessage(DOWN_FAIL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    try {
                        long total = response.body().contentLength();
                        Log.e(TAG, "total------>" + total);
                        long current = 0;
                        int unit = (int)(total / 10.5);
                        int progressNow = unit;
                        is = response.body().byteStream();
                        fos = new FileOutputStream(apkfile);
                        while ((len = is.read(buf)) != -1) {
                            current += len;
                            fos.write(buf, 0, len);
                            if(current > progressNow){
                                progress = progress+10;
                                mHandler.sendEmptyMessage(DOWN_LOADING);
                                progressNow += unit;

                            }

                        }
                        fos.flush();
                        // 下载完成
                        mHandler.sendEmptyMessage(DOWN_LOAD_FINISH);
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                        mHandler.sendEmptyMessage(DOWN_FAIL);
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                }
            });
        }

    }

    /**
     * 下载文件线程
     *
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "ezplug";
                    URL url = new URL(apk_url);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    conn.setConnectTimeout(20000);
                    conn.setReadTimeout(15000);
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists())
                    {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, name+".apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    // 缓存
                    byte buf[] = new byte[1024];
                    int length = conn.getContentLength();
                    int unit = (int)(length / 10.5);
                    int progressNow = unit;
                    int count = 0;
                    // 写入到文件中
                    while(true){
                        int read = is.read(buf);
                        count = count + read;

                        //通知更新进度条,为防止更新太频繁，每增长2%更新一次

                        if(count > progressNow){
                            progress = progress+10;
//                            progress = progress+2;
                            mHandler.sendEmptyMessage(DOWN_LOADING);
                            progressNow += unit;
                        }
//                        int progressNow = (int)(((float)count/length)*100);

                        if (read <= 0)
                        {
                            // 下载完成
                            Log.e(TAG, "下载完成: progress"+progress );
                            mHandler.sendEmptyMessage(DOWN_LOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, read);
                    }
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    /**
     * 安装APK文件
     */
    private void installApk()
    {

        // 通过Intent安装APK文件
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
//        UpDataService.this.startActivity(intent);
        File apkfile = new File(mSavePath, name+".apk");
        if (!apkfile.exists())
        {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.e(TAG, "installApk: apkFile"+apkfile);
            Uri contentUri = FileProvider.getUriForFile(getApplication(), BuildConfig.APPLICATION_ID + ".fileProvider", apkfile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        }else {
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        UpDataService.this.startActivity(intent);
    }

}

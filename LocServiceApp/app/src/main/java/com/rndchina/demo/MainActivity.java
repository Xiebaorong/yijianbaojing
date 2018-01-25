package com.rndchina.demo;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.Text;
import com.rndchina.demo.activity.AmapActivity;
import com.rndchina.demo.activity.CaseFragment;
import com.rndchina.demo.activity.SettingFragment;
import com.rndchina.demo.activity.VersionUpdateFragment;
import com.rndchina.demo.adapter.AppAdapter;
import com.rndchina.demo.base.LoadCallBack;
import com.rndchina.demo.base.MyLocListener;
import com.rndchina.demo.bean.SosMsg;
import com.rndchina.demo.service.MonitorService;
import com.rndchina.demo.util.APPUrl;
import com.rndchina.demo.util.AssToFileUtil;
import com.rndchina.demo.util.Constants;
import com.rndchina.demo.util.DialogUtil;
import com.rndchina.demo.util.GsonUtil;
import com.rndchina.demo.util.LocalDataUtils;
import com.rndchina.demo.util.LocationUtil;
import com.rndchina.demo.util.OkHttpManager;
import com.rndchina.demo.util.PhoneInfoUtils;
import com.rndchina.demo.util.ProgressDialogUtils;
import com.rndchina.demo.widget.ScrollLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements MyLocListener {
    //动态申请权限所需
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,

    };

    private final String TAG = MainActivity.class.getSimpleName();
    private final static int RESULT_CODE_GPSSETTING = 0;
    final int[] itemImages = new int[]{R.drawable.now_icon, R.drawable.police, R.drawable.amap, R.drawable.sample, R.drawable.newcoupon_icon, R.drawable.logout};
    String[] itemTexts = new String[]{"报警", "直接报警", "地图", "定位服务", "版本升级", "退出系统"};
    private ScrollLayout mScrollLayout;
    private static final float APP_PAGE_SIZE = 9;
    public static Context mContext;
    private int PageCount;
    private AppAdapter ad;
    private LinearLayout progressLayout;
    private TextView txtLoc, txtErr;
    private GridView appPage;
    List<String> txt_list = new ArrayList<String>();
    List<Integer> img_list = new ArrayList<Integer>();
    private String dhhm;

    private double yzb;
    private double xzb;//经度


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //动态申请权限
        verifyStoragePermissions();

        mContext = this;
        EventBus.getDefault().register(this);
        //获取经纬度工具类
        LocationUtil instance = LocationUtil.getInstance(mContext);
        instance.startMonitor();
        instance.addMyLocListener(this);


//        shared = getSharedPreferences("data", MODE_PRIVATE);
        progressLayout = (LinearLayout) findViewById(R.id.theme_loading_layout);
        progressLayout.setVisibility(View.GONE);
        mScrollLayout = (ScrollLayout) findViewById(R.id.menu_scrollLayout);
        mScrollLayout.setVisibility(View.VISIBLE);
        PageCount = (int) Math.ceil(itemImages.length / APP_PAGE_SIZE);
        txtLoc = (TextView) findViewById(R.id.txtLoc);
        txtErr = (TextView) findViewById(R.id.txtErr);
        for (int i = 0; i < itemImages.length; i++) {
            txt_list.add(itemTexts[i]);
            img_list.add(itemImages[i]);
        }
        ad = new AppAdapter(mContext, img_list, txt_list, PageCount);

        for (int i = 0; i < PageCount; i++) {
            appPage = new GridView(this);
            // get the "i" page data
            appPage.setAdapter(ad);
            appPage.setNumColumns(3);
            appPage.setHorizontalSpacing(1);
            appPage.setVerticalSpacing(1);
            appPage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    String item = (String) txt_list.get(position);
                    view.setBackgroundColor(getResources().getColor(R.color.transparent));
                    if ("退出系统".equals(item)) {
                        DialogUtil.showConfirmCancelDialog(MainActivity.this, "", "是否退出APP？", new DialogUtil.DialogActionListener() {
                            @Override
                            public void onHandle(Bundle bundle) {
                                finish();
                            }
                        }, null);
                    }
//                    else if ("个人设置".equals(item)) {
//                        SettingFragment fragment2 = new SettingFragment();
//                        getSupportFragmentManager().beginTransaction().addToBackStack(TAG).replace(android.R.id.content, fragment2).commit();
//                    }
                    else if ("定位服务".equals(item)) {
                        startWatchService();
                    } else if ("版本升级".equals(item)) {
                        VersionUpdateFragment fragment3 = new VersionUpdateFragment();
                        getSupportFragmentManager().beginTransaction().addToBackStack(TAG).replace(android.R.id.content, fragment3).commit();
                    } else if ("报警".equals(item)) {
                        CaseFragment fragment4 = new CaseFragment();
                        getSupportFragmentManager().beginTransaction().addToBackStack(TAG).replace(android.R.id.content, fragment4).commit();
                    } else if ("直接报警".equals(item)) {
                        http();
                    } else if ("地图".equals(item)) {
                        //获取电话
                        dhhm = PhoneInfoUtils.getInstance().toDhhm();
                        Intent intent = new Intent(MainActivity.this, AmapActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("yzb", yzb);
                        bundle.putDouble("xzb", xzb);
                        if (xzb == 0.0 && yzb == 0.0) {
                            bundle.putDouble("yzb", Constants.YZB);
                            bundle.putDouble("xzb", Constants.XZB);
                            Log.e(TAG, "onItemClick: yzb" + Constants.YZB);
                        }

//                        bundle.putDouble("yzb",46.6815882198596);
//                        bundle.putDouble("xzb",131.134084188877);
                        bundle.putString("dhhm", dhhm);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }

                }

            });
            mScrollLayout.addView(appPage);
            LocalDataUtils.init(this);
            PhoneInfoUtils.init(this);
            initGPS();
            //用于判断自己的应用的AccessibilityService是否在运行
//            if (serviceIsRunning()) {
              if(serviceIsWorked(getPackageName() + ".service.VolumeService")){
                Toast.makeText(this, "辅助服务已经在运行！", Toast.LENGTH_SHORT).show();
            } else {
                startAccessibilityService();
            }


        }
    }

    /**
     * 用于一键报警网络请求
     */
    private void http() {
        showProgressDialog();
        File file = new File("");
        dhhm = PhoneInfoUtils.getInstance().toDhhm();
        SosMsg.ResultBean sosMsg = new SosMsg.ResultBean();
        sosMsg.setZt("99");
        sosMsg.setDhhm(dhhm);

        sosMsg.setGyzb(yzb);
        sosMsg.setGxzb(xzb);
        if (xzb == 0.0 && yzb == 0.0) {
            sosMsg.setGyzb(Constants.YZB);
            sosMsg.setGxzb(Constants.XZB);
            Log.e(TAG, "onItemClick: ------yzb" + Constants.YZB);
        } else {
            Log.e(TAG, "onItemClick:====== xzb" + xzb);
        }
//        sosMsg.setGxzb(131.134084188877);
//        sosMsg.setGyzb(46.6815882198596);
        OkHttpManager.getInstance().postFileRequest(APPUrl.SEND, sosMsg, file, new LoadCallBack<String>(getApplicationContext()) {
            @Override
            public void onSuccess(Call call, Response response, String result) {
                SosMsg sosMsg = GsonUtil.jsontoBean(result);
                int code = sosMsg.getCode();
                Log.e(TAG, "onSuccess: code" + code);
                if (code == 1) {
                    disMissDialog();
                    Intent intent = new Intent(MainActivity.this, AmapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("yzb", yzb);
                    bundle.putDouble("xzb", xzb);
//                    bundle.putDouble("yzb",46.6815882198596);
//                    bundle.putDouble("xzb",131.134084188877);
                    if (xzb == 0.0 && yzb == 0.0) {
                        bundle.putDouble("yzb", Constants.YZB);
                        bundle.putDouble("xzb", Constants.XZB);
                        Log.e(TAG, "onItemClick: yzb" + Constants.YZB);
                    }
                    bundle.putString("dhhm", dhhm);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }


            }

            @Override
            public void onEror(Call call, int statusCode, Exception e) {
                Log.e(TAG, "onEror: e" + e.getMessage());
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    public void verifyStoragePermissions() {
        try {
            int permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "verifyStoragePermissions: " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static boolean serviceIsWorked(String service) {
        ActivityManager myManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Short.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(service)) {
                return true;

            }
        }
        return false;
    }

//    /**
//     * 判断自己的应用的AccessibilityService是否在运行
//     *
//     * @return
//     */
//    private boolean serviceIsRunning() {
//        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Short.MAX_VALUE);
//        for (ActivityManager.RunningServiceInfo info : services) {
//            if (info.service.getClassName().equals(getPackageName() + ".service.VolumeService")) {
//                return true;
//            }
//        }
//        return false;
//    }

    private void startWatchService() {
        if (!serviceIsWorked("com.rndchina.demo.service.MonitorService")) {
            Toast.makeText(mContext, "正在开启定位服务", Toast.LENGTH_LONG).show();
            startService(new Intent(MainActivity.this, MonitorService.class));
            txtLoc.setText("定位服务已开启");
            txtErr.setText("");
        } else {
            DialogUtil.showConfirmCancelDialog(this, "", "定位服务已开启，是否需要关闭？", new DialogUtil.DialogActionListener() {
                @Override
                public void onHandle(Bundle bundle) {
                    stopService(new Intent(MainActivity.this, MonitorService.class));
                    txtLoc.setText("定位服务已关闭");
                    txtErr.setText("");
                }
            }, null);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String info) {
        txtErr.setText(info);
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            DialogUtil.showConfirmCancelDialog(this, "请打开GPS连接", "您的GPS服务没有开启，请先开启GPS", new DialogUtil.DialogActionListener() {

                @Override
                public void onHandle(Bundle bundle) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Toast.makeText(MainActivity.this, "打开后直接点击返回键即可，若不打开返回下次将再次出现", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                }
            }, null);
        } else {
            if (!serviceIsWorked("com.rndchina.demo.service.MonitorService")) {
                txtLoc.setText("定位服务未开启");
                startWatchService();
            } else {
                txtLoc.setText("定位服务已开启");

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_GPSSETTING) {
            if (!serviceIsWorked("com.rndchina.demo.service.MonitorService")) {
                txtLoc.setText("定位服务未开启");
                startWatchService();
            } else {
                txtLoc.setText("定位服务已开启");
            }
        }
    }


    @Override
    public void onSendMyLoc(AMapLocation aMapLocation, LocationUtil.MLocation mLoc) {
        this.yzb = mLoc.latitude;
        this.xzb = mLoc.longitude;
    }

    /**
     * 取消对话框显示
     */
    public void disMissDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgressDialog() {
        showProgressDialog("努力提交中...");
    }

    public void showProgressDialog(String msg) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        try {
            progressDialog.show();
        } catch (WindowManager.BadTokenException exception) {
            exception.printStackTrace();
        }
    }



    /**
     * 前往设置界面开启服务
     */
    private void startAccessibilityService() {
        new AlertDialog.Builder(this)
                .setTitle("开启辅助功能")
                .setIcon(R.drawable.logo)
                .setMessage("使用一键报警功能需要您开启辅助功能")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 隐式调用系统设置界面
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    }
                }).create().show();
    }
}

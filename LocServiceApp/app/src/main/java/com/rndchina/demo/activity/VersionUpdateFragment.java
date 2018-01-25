package com.rndchina.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.rndchina.demo.R;
import com.rndchina.demo.base.BaseFragment;
import com.rndchina.demo.base.GlobalValues;
import com.rndchina.demo.base.LoadCallBack;
import com.rndchina.demo.bean.ResponseBean;
import com.rndchina.demo.bean.VersionInfBean;
import com.rndchina.demo.service.UpDataService;
import com.rndchina.demo.util.APPUrl;
import com.rndchina.demo.util.DialogUtil;
import com.rndchina.demo.util.LocalDataUtils;
import com.rndchina.demo.util.OkHttpManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by dell on 2017-9-5.
 */

public class VersionUpdateFragment extends BaseFragment  {
    private TextView currentVersion,latestVersion;
    private Button update;
    private ImageView topBack,symbol;
    private SharedPreferences sp;
    private String currentVersionName;//当前版本
    private String latestVersionName;//最新版本
    private String versionURL;
    private String updateContent;//更新内容
    private TextView tvDownInfo;
    private final static  int GET_NEW_VER=1;
    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case GET_NEW_VER:
                    String latestV = String.format(getResources().getString(R.string.latest_version), latestVersionName);
                    latestVersion.setText("最新版本:"+latestV);
                    break;
            }
            return false;
        }
    });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_version_update;
    }

    @Override
    protected boolean showActionBar() {
        return true;
    }

    @Override
    protected String getActionBarTitle() {
        return "版本升级";
    }

    @Override
    protected boolean showRightButton() {
        return false;
    }

    private void getLastVersion(){
        OkHttpManager.getInstance().getRequest(APPUrl.DOWNLOAD,
                new LoadCallBack<ResponseBean<VersionInfBean>>(getContext()){

            @Override
            public void onSuccess(Call call, Response response,
                                  ResponseBean<VersionInfBean> responseBean) {
                Log.d("MonitorService",responseBean.getInfo());
                int code = responseBean.getStatus();
                if(code == 0){
                    VersionInfBean versionInfBean = responseBean.getData();
                    versionURL = versionInfBean.getVersionURL();
                    latestVersionName = versionInfBean.getVersionID();
                    updateContent = versionInfBean.getUpdateDate();
                    mHandler.sendEmptyMessage(GET_NEW_VER);
//                    latestVersion.setText(latestVersionName);
                }

            }

            @Override
            public void onEror(Call call, int statusCode, Exception e) {
                latestVersionName="?";
            }
        });
    }

    @Override
    protected void initView(View rootView) {
        EventBus.getDefault().register(this);
        symbol=(ImageView)rootView.findViewById(R.id.version_image);
        Bitmap bitmap = readBitMap(getContext(),R.drawable.szrdlogo);
        symbol.setImageDrawable(new BitmapDrawable(bitmap));
        tvDownInfo = (TextView)rootView.findViewById(R.id.txtdowninfo);
        currentVersion=(TextView)rootView.findViewById(R.id.current_version);
        latestVersion=(TextView)rootView.findViewById(R.id.latest_version);
        update=(Button)rootView.findViewById(R.id.update);


        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        /**
         * 获取当前版本名和最新版本名
         * 当前版本直接使用Globalvalues里的 VERSION_NAME
         */
        currentVersionName = GlobalValues.VERSION_NAME;

        latestVersionName =  GlobalValues.VERSION_NAME;
        String currentV = String.format(getResources().getString(R.string.current_version), currentVersionName);
//        String latestV = String.format(getResources().getString(R.string.latest_version), latestVersionName);
        currentVersion.setText("当前版本:"+currentV);
        latestVersion.setText("最新版本:"+currentV);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update.setEnabled(false);
//                tvDownInfo.setText("正在下载，请等待...");

               if (latestVersionName.equals(currentVersionName)) {
                   DialogUtil.showConfirmDialog(getActivity(),"","当前版本是最新版本");
                    return;
                } else if (latestVersionName.equals("?")) {
                   DialogUtil.showConfirmDialog(getActivity(),"","获取最新版本失败");
                    return;
                }

                Toast.makeText(getActivity(),"正在下载,请稍后...",Toast.LENGTH_LONG).show();
                sp.edit().putBoolean("isUpdate",false).commit();
                sp.edit().putString("apk_url",versionURL).commit();
                String apk_name = versionURL.substring(versionURL.lastIndexOf("/")+1);
                apk_name = apk_name.substring(0,apk_name.indexOf("."));
                sp.edit().putString("name",apk_name).commit();
                Intent intent = new Intent(getActivity(), UpDataService.class);
                getActivity().startService(intent);
            }
        });
        getLastVersion();
    }


    /*
 * 以最省内存的方式读取本地资源的图片
 */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(int tag) {
        if(tag == GlobalValues.DOWN_FAIL){
            tvDownInfo.setText("下载失败...");
            update.setEnabled(true);
        }else if(tag == GlobalValues.DOWN_FAIL){
            tvDownInfo.setText("下载成功");
            update.setEnabled(true);
        }
    }
}

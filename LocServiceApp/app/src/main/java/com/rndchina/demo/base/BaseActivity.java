package com.rndchina.demo.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.MapsInitializer;
import com.rndchina.demo.R;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by xie on 2017/12/27.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    public Context mContext;
    private Toast toast;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;


        if (getLayout() != 0) {
            setContentView(getLayout());
        }

        //新建高德离线地图存放包
        File destDir = new File(Environment.getExternalStorageDirectory() + "/gaodeMap");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        MapsInitializer.sdcardDir = Environment.getExternalStorageDirectory() + "/gaodeMap";
        //子类OnCreate方法
        OnActCreate(savedInstanceState);
        initView();

    }





    protected abstract void initView();

    protected abstract void OnActCreate(Bundle savedInstanceState);

    public abstract int getLayout();



    /**
     * 显示提示
     * @param content
     */
    public void showToast(String content){
        if(toast==null){
            toast=Toast.makeText(mContext,content,toast.LENGTH_SHORT);
        }else{
            toast.setText(content);
        }
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

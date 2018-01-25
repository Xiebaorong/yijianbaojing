package com.rndchina.demo.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.fence.GeoFenceClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.rndchina.demo.R;
import com.rndchina.demo.base.BaseActivity;
import com.rndchina.demo.base.LoadCallBack;
import com.rndchina.demo.bean.SosMsg;
import com.rndchina.demo.util.APPUrl;
import com.rndchina.demo.util.AssToFileUtil;
import com.rndchina.demo.util.GsonUtil;
import com.rndchina.demo.util.LocalDataUtils;
import com.rndchina.demo.util.LocationStyleUtil;
import com.rndchina.demo.util.OkHttpManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class AmapActivity extends BaseActivity implements View.OnClickListener, AMap.OnCameraChangeListener {
    private MapView mMapView = null;
    private AMap aMap;
    private ImageView action_back;
    private TextView action_title;

    private double yzb;//维度
    private double xzb;
    private String dhhm;

    private LocationStyleUtil styleUtil;
    private static final String TAG = "AmapActivity";

    @Override
    protected void OnActCreate(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        AssToFileUtil.importDB(this);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_amap;
    }


    @Override
    protected void initView() {
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();


        }
        View include = findViewById(R.id.amap_header);
        action_back = (ImageView) include.findViewById(R.id.action_back);
        action_title = (TextView) include.findViewById(R.id.action_title);
        action_title.setText("附近警员与监控");
        action_back.setVisibility(View.VISIBLE);
        action_back.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        xzb = extras.getDouble("xzb");
        yzb = extras.getDouble("yzb");
        dhhm = extras.getString("dhhm");
        LatLng latLng = new LatLng(yzb, xzb);
        if (xzb == 0.0) {
            xzb = 131.1412507294;
            yzb = 46.6857367150;
            latLng = new LatLng(yzb, xzb);
            showToast("GPS定位失败");
        }
//            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.setMinZoomLevel(Float.valueOf(11));
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        styleUtil = new LocationStyleUtil(this);
        styleUtil.InitLocation(aMap, xzb, yzb);
        aMap.setOnCameraChangeListener(this);
//           aMap.setOnMapTouchListener(this);
        http(xzb, yzb);
    }


    public void http(Double gxzb, Double gyzb) {

        Map<String, String> params = new HashMap<>();
        params.put("xzb", gxzb + "");
        params.put("yzb", gyzb + "");
        params.put("dhhm", dhhm);
//        Log.e(TAG, "onSuccess: 坐标"+xzb+"---------"+yzb+"+++++"+dhhm );
        OkHttpManager.getInstance().postRequest(APPUrl.RECEIVE, new LoadCallBack<String>(getApplicationContext()) {
                    @Override
                    public void onSuccess(Call call, Response response, String result) {
                        Log.e(TAG, "onSuccess: result" + result);
                        SosMsg sosMsg = GsonUtil.jsontoBean(result);
                        int code = sosMsg.getCode();
                        if (code == 1) {
                            List<SosMsg.CameraBean> cameraBean = sosMsg.getCameraBean();
                            List<SosMsg.ResultBean> soslist = sosMsg.getResult();
                            aMap.clear();
                            styleUtil.JudgeRelation(soslist, aMap);
                            Log.e(TAG, "onSuccess: cameraBean" + cameraBean.size());
                            styleUtil.CameraRalation(cameraBean, aMap);
                        } else {
                            EventBus.getDefault().post("失败");
                        }
                    }

                    @Override
                    public void onEror(Call call, int statusCode, Exception e) {

                    }

                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                }
                , params);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_back:
                finish();
                break;
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition position) {
        LatLng target = position.target;
        http(target.longitude, target.latitude);
    }

}

package com.rndchina.demo.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.rndchina.demo.R;
import com.rndchina.demo.activity.AmapActivity;
import com.rndchina.demo.bean.SosMsg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xie on 2017/12/24.
 */

public class LocationStyleUtil {
    public MyLocationStyle myLocationStyle;
    public Context mContext;
    public static int myImage = R.drawable.location_marker;
    String dhhm;

    public LocationStyleUtil(Context context) {
        this.mContext = context;
    }

    public void InitLocation(AMap aMap, double xzb, double yzb) {
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(1000);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(myImage));
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);

        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);


    }

    public void JudgeRelation(final List<SosMsg.ResultBean> soslist, final AMap aMap) {
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        dhhm = PhoneInfoUtils.getInstance().toDhhm();
        for (SosMsg.ResultBean s : soslist) {
            //提交前通过传入手机号判断是否为本人
            if (!dhhm.equals(s.getDhhm())) {
                MarkerOptions markerOption = new MarkerOptions();
                Log.e("经纬度", "后台传入经纬度: " + s.getGyzb() + "++++++++" + s.getGxzb());
                markerOption.position(new LatLng(s.getGyzb(), s.getGxzb()));
                markerOptionlst.add(markerOption);
                markerOption.anchor(0.5f, 0.5f);

                if (s.getJyfl() == 0) {
                    markerOption.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.location_tongshi)).snippet("电话" + ":" + s.getDhhm()).title("警员姓名:" + s.getJyxm());
                } else if (s.getJyfl() == 1) {
                    markerOption.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.location_suozhang)).snippet("电话" + ":" + s.getDhhm()).title("警员姓名:" + s.getJyxm());
                } else if (s.getJyfl() == 2) {
                    markerOption.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.location)).snippet("电话" + ":" + s.getDhhm()).title("警员姓名:" + s.getJyxm());
                } else {
                    markerOption.icon(BitmapDescriptorFactory
                            .fromResource(myImage));
                }
            }
        }
        ArrayList<Marker> markers = aMap.addMarkers(markerOptionlst, false);
        /**
         * 点击图标显示警员信息
         */
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (aMap != null) {
                    marker.showInfoWindow();
                    return true;
                }

                return false;
            }
        });
        /**
         * 点击弹框跳转至拨号界面
         */
        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String[] split = marker.getSnippet().split(":");
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + split[1]));
                mContext.startActivity(dialIntent);
            }
        });

    }

    public void CameraRalation(List<SosMsg.CameraBean> cameraBean, final AMap map) {
        ArrayList<MarkerOptions> markerlist = new ArrayList<MarkerOptions>();

        for (SosMsg.CameraBean c : cameraBean) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(c.getYzb(), c.getXzb()));

            markerlist.add(markerOptions);
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.camera)).snippet("名字:" + c.getName() + "\n类型:" + c.getType()).title("编号:" + c.getCode());
        }
        ArrayList<Marker> marker = map.addMarkers(markerlist, false);
        /**
         * 点击图标显示摄像头信息
         */
        map.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (map != null) {
                    marker.showInfoWindow();
                    return true;
                }
                return false;
            }
        });
    }

    public void cleanMarker(AMap map) {
//        List<Marker> list = map.getMapScreenMarkers();
//        for (Marker m : list){
//            m.remove();
//
//        }

    }
}

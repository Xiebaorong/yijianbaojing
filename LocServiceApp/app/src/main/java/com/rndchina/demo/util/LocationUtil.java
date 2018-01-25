package com.rndchina.demo.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.rndchina.demo.BuildConfig;
import com.rndchina.demo.base.MyLocListener;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017-6-24.
 */

public class LocationUtil   {
	private  final static String TAG = LocationUtil.class.getSimpleName();
	private AMapLocationClient mLocationClient = null;
	private AMapLocationClientOption mLocationOption = null;
	private static LocationUtil mInstance;
	private MLocation  mBaseLocation = new MLocation();
	private List<MyLocListener> myLocListenerList = new ArrayList<MyLocListener>();

	public static LocationUtil getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new LocationUtil(context);
		}
		return mInstance;
	}
	public void addMyLocListener(MyLocListener locListener){
		myLocListenerList.add(locListener);
	}

	public void removeMyLocListener(MyLocListener locListener){
		if(myLocListenerList != null && myLocListenerList.size() >0){
			myLocListenerList.remove(locListener);
		}
	}

	public void removeAll(){
		myLocListenerList.clear();
	}
	public LocationUtil(Context context) {
		mLocationClient = new AMapLocationClient(context.getApplicationContext());
		//设置定位回调监听
		mLocationClient.setLocationListener(new MyLocationListener());
		initParams();

	}

	private void initParams() {
		//初始化AMapLocationClientOption对象
		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为高精度模式。
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		//设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);

		mLocationOption.setInterval(5000);// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms）
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


	public MLocation getBaseLocation() {
		if (BuildConfig.DEBUG) Log.d("get", "get location");
		return mBaseLocation;
	}
	public void onNOtifyMyLocChange(AMapLocation aMapLocation,MLocation loc){
		if(myLocListenerList != null && myLocListenerList.size() >0){
			for(int i=0;i<myLocListenerList.size();i++){
				MyLocListener listner = myLocListenerList.get(i);
				if(listner != null){
					listner.onSendMyLoc(aMapLocation,loc);
				}
			}
		}
	}


	public class MyLocationListener implements AMapLocationListener{
		@Override
		public void onLocationChanged(AMapLocation aMapLocation) {

			if (aMapLocation != null) {
				if (aMapLocation.getErrorCode() == 0) {
					//可在其中解析amapLocation获取相应内容。
					LatLng latLng = new LatLng(aMapLocation.getLatitude(),
							aMapLocation.getLongitude());//取出经纬度

					mBaseLocation.latitude = aMapLocation.getLatitude();

					mBaseLocation.longitude = aMapLocation.getLongitude();
					Log.e("经纬度", "工具类: "+aMapLocation.getLatitude()+"++++++++"+aMapLocation.getLongitude());
					mBaseLocation.province = aMapLocation.getProvince();
					mBaseLocation.city = aMapLocation.getCity();
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
					onNOtifyMyLocChange(aMapLocation,mBaseLocation);
				} else {
					//定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
					Log.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
					//XLog.e(MSG, "AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
				}
			}
		}

	}



	public class MLocation {
		public double latitude;
		public double longitude;
		public String province;
		public String city;
	}
}


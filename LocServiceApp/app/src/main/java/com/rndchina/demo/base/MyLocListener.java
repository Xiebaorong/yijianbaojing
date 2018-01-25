package com.rndchina.demo.base;

import com.amap.api.location.AMapLocation;
import com.rndchina.demo.util.LocationUtil;


/**
 * Created by dell on 2017-6-29.
 */

public interface MyLocListener {
    public void onSendMyLoc(AMapLocation aMapLocation, LocationUtil.MLocation mLoc);
}

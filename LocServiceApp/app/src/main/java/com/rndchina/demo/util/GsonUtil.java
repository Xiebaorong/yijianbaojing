package com.rndchina.demo.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rndchina.demo.bean.GpsData;
import com.rndchina.demo.bean.Result;
import com.rndchina.demo.bean.SosMsg;

import java.util.List;

/**
 * Created by dell on 2017-9-2.
 */

public class GsonUtil {
    public static String beanToJSONString(GpsData bean) {
        return new Gson().toJson(bean);
    }
    public static String sosMsgToJSONString(SosMsg.ResultBean bean) {
        return new Gson().toJson(bean);
    }
    public static Result jSONStringToResult(String s) {
        return new Gson().fromJson(s,Result.class);
    }
    public static SosMsg jsontoBean(String result) {
        return new Gson().fromJson(result,SosMsg.class);
    }
}

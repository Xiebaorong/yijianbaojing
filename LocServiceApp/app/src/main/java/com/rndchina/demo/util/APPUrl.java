package com.rndchina.demo.util;

/**
 * Created by xie on 2018/1/4.
 * 接口地址
 */

public class APPUrl {
    //   public static String url = "http://"+ LocalDataUtils.getInstance().getServerIp()+":"+LocalDataUtils.getInstance().getServerPort()+"/gps_app/interfaces/sos/sosMsg";
//    public static final String url = "http://192.168.199.238:8080/gps_app/interfaces/sos/sosMsg";//测试
//    public static final String tourl = "http://192.168.199.238:8080/gps_app/interfaces/sos/getJYList";//测试

    private static final String host = "http://"+ LocalDataUtils.getInstance().getServerIp()+":"+LocalDataUtils.getInstance().getServerPort();
    //发送报警信息到后台
    public static final String SEND = host +"/gps_app/interfaces/sos/sosMsg";
    //接收附近警员信息
    public static final String RECEIVE = host+"/gps_app/interfaces/sos/getJYList";
    //发送GPS信息到后台
    public static final String SENDGPS = host +"/gps_app/gpsPath/getGpsPoint";
    //版本升级
    public static final String DOWNLOAD = host +"/gps_app/version/interfaces/current";


}

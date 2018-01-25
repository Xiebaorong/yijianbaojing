package com.rndchina.demo.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by dell on 2017-9-22.
 */

public class PhoneInfoUtils {

    private static String TAG = "PhoneInfoUtils";
    private static PhoneInfoUtils mPhoneInfoUtils;
    private TelephonyManager telephonyManager;
    //移动运营商编号
    private String NetworkOperator;
    private String dhhm;
    private Context context;
    public static synchronized void init(Context cxt) {
        if (mPhoneInfoUtils == null) {
            mPhoneInfoUtils = new PhoneInfoUtils(cxt);
        }
    }
    public PhoneInfoUtils(Context context) {
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }
    public synchronized static PhoneInfoUtils getInstance() {
        if (mPhoneInfoUtils == null) {
            throw new RuntimeException("please init first!");
        }

        return mPhoneInfoUtils;
    }
    //获取sim卡iccid
    public String getIccid() {
        String iccid = "N/A";
        iccid = telephonyManager.getSimSerialNumber();
        return iccid;
    }

    //获取电话号码
    public String getNativePhoneNumber() {
        String nativePhoneNumber = "N/A";
        nativePhoneNumber = telephonyManager.getLine1Number();
        if(nativePhoneNumber.contains("+86")){
            nativePhoneNumber =nativePhoneNumber.substring(3);
        }
        return nativePhoneNumber;
    }

    //获取手机服务商信息
    public String getProvidersName() {
        String providersName = "N/A";
        NetworkOperator = telephonyManager.getNetworkOperator();
        //IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
//        Flog.d(TAG,"NetworkOperator=" + NetworkOperator);
        if (NetworkOperator.equals("46000") || NetworkOperator.equals("46002")) {
            providersName = "中国移动";//中国移动
        } else if(NetworkOperator.equals("46001")) {
            providersName = "中国联通";//中国联通
        } else if (NetworkOperator.equals("46003")) {
            providersName = "中国电信";//中国电信
        }
        return providersName;

    }

    public String getPhoneInfo() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuffer sb = new StringBuffer();

        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());//移动运营商编号
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());//移动运营商名称
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        return  sb.toString();
    }

    //获取电话号码
    public String toDhhm(){
        String phone =  getNativePhoneNumber();
        if(TextUtils.isEmpty(phone)) {
            dhhm = LocalDataUtils.getInstance().getDeviceName();
        }else{
            dhhm = phone;
        }
        return dhhm;
    }
}

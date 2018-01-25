package com.rndchina.demo.bean;

/**
 * Created by dell on 2017-9-2.
 */

public class GpsData {
    String terminal;//终端编号  NOT NULL
    Double dLongitude;//经度   NOT NULL
    Double dLatitude;//纬度    NOT NULL
    String iSpeed;//速度
    String iDirection;//方向
    String iAltitude;//高程
    String iPrecision;//精度
    String screenState;//屏幕状态
    String dateTime;//发送时间


    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public Double getdLongitude() {
        return dLongitude;
    }

    public void setdLongitude(Double dLongitude) {
        this.dLongitude = dLongitude;
    }

    public Double getdLatitude() {
        return dLatitude;
    }

    public void setdLatitude(Double dLatitude) {
        this.dLatitude = dLatitude;
    }

    public String getiSpeed() {
        return iSpeed;
    }

    public void setiSpeed(String iSpeed) {
        this.iSpeed = iSpeed;
    }

    public String getiDirection() {
        return iDirection;
    }

    public void setiDirection(String iDirection) {
        this.iDirection = iDirection;
    }

    public String getiAltitude() {
        return iAltitude;
    }

    public void setiAltitude(String iAltitude) {
        this.iAltitude = iAltitude;
    }

    public String getiPrecision() {
        return iPrecision;
    }

    public void setiPrecision(String iPrecision) {
        this.iPrecision = iPrecision;
    }

    public String getScreenState() {
        return screenState;
    }

    public void setScreenState(String screenState) {
        this.screenState = screenState;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}

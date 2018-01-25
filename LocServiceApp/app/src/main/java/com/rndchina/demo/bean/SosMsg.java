package com.rndchina.demo.bean;

import java.util.Date;
import java.util.List;

/**
 * Created by xie on 2017/12/22.
 */

public class SosMsg {

    private String msg;
    private int code;
    private List<ResultBean> result;
    private List<CameraBean> camera;
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public List<CameraBean> getCameraBean() {
        return camera;
    }

    public void setCamera(List<CameraBean> cameraBean) {
        this.camera = camera;
    }

    public static class CameraBean{
        private String code;
        private String name;
        private String type;
        private Double xzb;
        private Double yzb;
        private String addr;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Double getXzb() {
            return xzb;
        }

        public void setXzb(Double xzb) {
            this.xzb = xzb;
        }

        public Double getYzb() {
            return yzb;
        }

        public void setYzb(Double yzb) {
            this.yzb = yzb;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }
    }

    public static class ResultBean {
        private int id;
        private String sbbh;
        private String jyxm;//
        private String dhhm;//电话号码
        private String dwdm;
        private Double gxzb;//经度
        private Double gyzb;//维度
        private String ajlx;//案件类型
        private String rs;//人数
        private String jqsm;//警情说明
        public String times;//时间
        private Double distance;
        private int jyfl;//
        private String zt;//状态

        public String getZt() {
            return zt;
        }

        public void setZt(String zt) {
            this.zt = zt;
        }


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSbbh() {
            return sbbh;
        }

        public void setSbbh(String sbbh) {
            this.sbbh = sbbh;
        }

        public String getJyxm() {
            return jyxm;
        }

        public void setJyxm(String jyxm) {
            this.jyxm = jyxm;
        }

        public String getDhhm() {
            return dhhm;
        }

        public void setDhhm(String dhhm) {
            this.dhhm = dhhm;
        }

        public String getDwdm() {
            return dwdm;
        }

        public void setDwdm(String dwdm) {
            this.dwdm = dwdm;
        }

        public Double getGxzb() {
            return gxzb;
        }

        public void setGxzb(Double gxzb) {
            this.gxzb = gxzb;
        }

        public Double getGyzb() {
            return gyzb;
        }

        public void setGyzb(Double gyzb) {
            this.gyzb = gyzb;
        }

        public String getAjlx() {
            return ajlx;
        }

        public void setAjlx(String ajlx) {
            this.ajlx = ajlx;
        }

        public String getRs() {
            return rs;
        }

        public void setRs(String rs) {
            this.rs = rs;
        }

        public String getJqsm() {
            return jqsm;
        }

        public void setJqsm(String jqsm) {
            this.jqsm = jqsm;
        }

        public String getTimes() {
            return times;
        }

        public void setTimes(String times) {
            this.times = times;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public int getJyfl() {
            return jyfl;
        }

        public void setJyfl(int jyfl) {
            this.jyfl = jyfl;
        }

    }
}

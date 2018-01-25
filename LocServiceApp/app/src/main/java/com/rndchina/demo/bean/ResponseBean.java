package com.rndchina.demo.bean;

/**
 * Created by dell on 2017-9-5.
 */

public class ResponseBean<T> {
    private int status;
    private String info;
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

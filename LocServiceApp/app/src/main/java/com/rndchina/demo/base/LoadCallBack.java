package com.rndchina.demo.base;

import android.content.Context;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.Request;
/**
 * Created by dell on 2017-9-2.
 */

public abstract class LoadCallBack<T> extends BaseCallBack<T> {
    private Context context;

    public LoadCallBack(Context context) {
        this.context = context;
    }

    private void showDialog() {
    }

    private void hideDialog() {
    }

    public void setMsg(String str) {
    }

    public void setMsg(int  resId) {
    }


    @Override
    public void OnRequestBefore(Request request) {


    }

    @Override
    public void onFailure(Call call, IOException e) {
    }

    @Override
    public void onResponse(Response response) {

    }

    @Override
    public void inProgress(int progress, long total, int id) {

    }
}

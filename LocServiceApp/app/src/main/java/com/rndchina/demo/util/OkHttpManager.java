package com.rndchina.demo.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.rndchina.demo.base.BaseCallBack;
import com.rndchina.demo.bean.Result;
import com.rndchina.demo.bean.SosMsg;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by dell on 2017-9-2.
 */

public class OkHttpManager {
    private static OkHttpManager mOkHttpManager;

    private OkHttpClient mOkHttpClient;

    private Gson mGson;

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("audio/mp3");

    private static final String TAG = "OkHttpManager";
    private OkHttpManager() {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);
        mGson = new Gson();

    }

    //创建 单例模式（OkHttp官方建议如此操作）
    public static OkHttpManager getInstance() {
        if (mOkHttpManager == null) {
            mOkHttpManager = new OkHttpManager();
        }
        return mOkHttpManager;
    }

    /***********************
     * 对外公布的可调方法
     ************************/

    public void getRequest(String url, final BaseCallBack callBack) {
        Request request = buildRequest(url, null, HttpMethodType.GET);
        doRequest(request, callBack);
    }

    public void postRequest(String url, final BaseCallBack callBack, Map<String, String> params) {
        Request request = buildRequest(url, params, HttpMethodType.POST);
        doRequest(request, callBack);
    }

    /**
     *  文件带参数
     * @param url 路径
     * @param sosMsg 传递信息
     * @param file 录音文件
     * @param callBack
     */
    public void postFileRequest(String url ,SosMsg.ResultBean sosMsg,File file,final BaseCallBack callBack){
        Request request = FileRequest(url, sosMsg, HttpMethodType.POST,file);
        doRequest(request,callBack);
    }




    //去进行网络 异步 请求
    private void doRequest(Request request, final BaseCallBack callBack) {
        callBack.OnRequestBefore(request);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callBack.onResponse(response);
                String result = response.body().string();
                if (response.isSuccessful()) {

                    if (callBack.mType == String.class) {
                        callBackSuccess(callBack, call, response, result);
                    } else {
                        try {
                            Object object = mGson.fromJson(result, callBack.mType);//自动转化为 泛型对象
                            callBackSuccess(callBack, call, response, object);
                        } catch (JsonParseException e) {
                            //json解析错误时调用
                            callBack.onEror(call, response.code(), e);
                        }
                    }
                } else {
                    callBack.onEror(call, response.code(), null);
                }

            }

        });


    }
    private Param[] fromMapToParams(Map<String,String> params) {
        if (params == null)
            return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<String> entries = params.keySet();
        Iterator<String> it=entries.iterator();
        int i = 0;
        while (it.hasNext()){
            String key=it.next();
            String value=params.get(key);
            res[i++] = new Param(key, value);
        }

        return res;
    }

    private void callBackSuccess(final BaseCallBack callBack, final Call call, final Response response, final Object object) {
        callBack.onSuccess(call, response, object);

    }
    //创建 Request对象
    private Request buildRequest(String url, Map<String, String> params, HttpMethodType methodType) {

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (methodType == HttpMethodType.GET) {
            builder.get();
        } else if (methodType == HttpMethodType.POST) {
            RequestBody requestBody = buildFormData(params);
            builder.post(requestBody);
        }
        return builder.build();
    }


    private Request FileRequest(String url, SosMsg.ResultBean sosMsg, HttpMethodType post, File file) {
        String json = mGson.toJson(sosMsg);
        RequestBody multipartBody = null;
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (sosMsg.getZt().equals("97")||sosMsg.getZt().equals("99")){
            multipartBody= new MultipartBody.Builder()
                .addFormDataPart("sosMsg",json)
                .build();
        }else if(sosMsg.getZt().equals("98")){
            RequestBody fileBody=RequestBody.create(MEDIA_TYPE_MARKDOWN,file);
            multipartBody= new MultipartBody.Builder()
                    .addFormDataPart("file",file.getName(),fileBody)
                    .addFormDataPart("sosMsg",json)
                    .build();
        }
             builder.post(multipartBody);
        return builder.build() ;
    }


    //构建请求所需的参数表单
    private RequestBody buildFormData(Map<String,String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("platform", "android");
        builder.add("version", "1.0");
        builder.add("key", "123456");
        if (params != null) {
            for (Map.Entry entry :params.entrySet()) {
                builder.add((String)entry.getKey(), (String)entry.getValue());
            }
        }
        return builder.build();
    }

    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

    enum HttpMethodType {
        GET, POST
    }

}

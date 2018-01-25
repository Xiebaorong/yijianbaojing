/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rndchina.demo.util;

import android.content.Context;
import android.content.SharedPreferences;


public class LocalDataUtils {
    /**
     * 保存Preference的name
     */
    public static final String PREFERENCE_NAME = "userInfo";
    private static SharedPreferences mSharedPreferences;
    private static LocalDataUtils mPreferencemManager;
    private static SharedPreferences.Editor editor;


    private String SHARED_KEY_DEVICE="shared_key_dev";
    private String SHARED_KEY_IP="shared_key_ip";
    private String SHARED_KEY_PORT="shared_key_port";

    private LocalDataUtils(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt) {
        if (mPreferencemManager == null) {
            mPreferencemManager = new LocalDataUtils(cxt);
        }
    }

    /**
     * 单例模式，获取instance实例
     *
     * @param
     * @return
     */
    public synchronized static LocalDataUtils getInstance() {
        if (mPreferencemManager == null) {
            throw new RuntimeException("please init first!");
        }

        return mPreferencemManager;
    }


    public void setDeviceName(String dev_name){
        editor.putString(SHARED_KEY_DEVICE,dev_name);
        editor.commit();
    }

    public String  getDeviceName(){
        return mSharedPreferences.getString(SHARED_KEY_DEVICE,"GZMZ0120039");
    }

    public void setServerIp(String ip){
        editor.putString(SHARED_KEY_IP,ip);
        editor.commit();
    }

    public String  getServerIp(){
        return mSharedPreferences.getString(SHARED_KEY_IP,"127.0.0.1");
    }

    public void setServerPort(String port){
        editor.putString(SHARED_KEY_PORT,port);
        editor.commit();
    }

    public String  getServerPort(){
        return mSharedPreferences.getString(SHARED_KEY_PORT,"19820");
    }


}

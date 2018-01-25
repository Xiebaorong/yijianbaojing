package com.rndchina.demo.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.rndchina.demo.activity.AmapActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xie on 2017/12/25.
 * 将apk安装包中双鸭山市离线地图写入手机内存中
 */

public class AssToFileUtil {
    private static final String TAG = "AssToFileUtil";
    public static void importDB(Context context){
        File file = new File(Environment.getExternalStorageDirectory() + "/gaodeMap/data/map/shuangyashan.dat");
        if (file.exists() && file.length() > 0) {
        }else{
            AssetManager asset = context.getAssets();
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = asset.open("shuangyashan.dat");
                fos = new FileOutputStream(file);
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                if (is != null) {
                        is.close();
                }
                if (fos != null) {
                        fos.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "importDB:e "+e.getMessage() );
                }
            }
        }
    }

}

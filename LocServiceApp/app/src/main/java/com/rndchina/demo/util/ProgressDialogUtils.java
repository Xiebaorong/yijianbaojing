package com.rndchina.demo.util;

import android.app.ProgressDialog;
import android.view.WindowManager;

/**
 * Created by xie on 2017/12/26.
 */

public class ProgressDialogUtils {
    private ProgressDialog progressDialog;

    /**
     * 取消对话框显示
     */
    public void disMissDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgressDialog() {
        showProgressDialog("努力加载中...");
    }

    public void showProgressDialog(String msg) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
//        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        try {
            progressDialog.show();
        } catch (WindowManager.BadTokenException exception) {
            exception.printStackTrace();
        }
    }
}

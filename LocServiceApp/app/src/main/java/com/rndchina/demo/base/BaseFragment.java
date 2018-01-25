package com.rndchina.demo.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rndchina.demo.R;




public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    protected View rootView;
    protected ImageView btn_action_back;
    protected TextView mActionBarTitle, btn_action_next;
    private ProgressDialog progressDialog;
    private Toast toast;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            mContext = inflater.getContext().getApplicationContext();
            rootView = inflater.inflate(getLayoutId(), null);
            if (showActionBar()) {
                initActionBar();
                if (showRightButton()) {
                    initActionNext();
                }
            }
            initView(rootView);
        } else {
            ViewGroup vg = (ViewGroup) rootView.getParent();
            if (vg != null) {
                vg.removeView(rootView);
            }
        }
        return rootView;
    }

    private void initActionBar() {
        mActionBarTitle = (TextView) rootView
                .findViewById(R.id.action_title);
        mActionBarTitle.setText(getActionBarTitle());
        btn_action_back = (ImageView) rootView.findViewById(R.id.action_back);
        btn_action_back.setVisibility(View.VISIBLE);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onBackPress()) {
                    dealBack();
                }
            }
        });
    }

    private void initActionNext() {
        btn_action_next = (TextView) rootView.findViewById(R.id.action_next);
        btn_action_next.setText(getRightButtonText());
        btn_action_next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onRightButtonClicked();
            }
        });
    }

    protected abstract int getLayoutId();

    protected abstract boolean showActionBar();

    protected abstract String getActionBarTitle();

    protected abstract boolean showRightButton();

    protected abstract void initView(View rootView);

    protected String getRightButtonText() {
        return null;
    }

    private void dealBack() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            getActivity().finish();
        } else {
            fm.popBackStack();
            Log.e("shengming","tiaozhan");

        }
    }

    protected void onRightButtonClicked() {

    }

    public boolean onBackPress() {
        return false;
    }

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
        showProgressDialog("努力提交中...");
    }

    public void showProgressDialog(String msg) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        try {
            progressDialog.show();
        } catch (WindowManager.BadTokenException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 显示提示
     * @param content
     */
    public void showToast(String content){
        if(toast==null){
            toast=Toast.makeText(mContext,content,toast.LENGTH_SHORT);
        }else{
            toast.setText(content);
        }
        toast.show();
    }
}

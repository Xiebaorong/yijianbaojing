package com.rndchina.demo.activity;

import android.app.Activity;
import android.media.MediaCodec;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rndchina.demo.R;
import com.rndchina.demo.base.BaseFragment;
import com.rndchina.demo.util.LocalDataUtils;
import com.rndchina.demo.util.PhoneInfoUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dell on 2017-8-28.
 */

public class SettingFragment extends BaseFragment implements View.OnClickListener {
    public static String TAG = SettingFragment.class.getSimpleName();
    private EditText edtIPAddr;
    private EditText edtPort;
    private EditText edtDev;
    private TextView phoneNoTv;
    private Button okBtn;
    private Button cancelBtn;
    public SettingFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected boolean showActionBar() {
        return true;
    }

    @Override
    protected String getActionBarTitle() {
        return "设置";
    }

    @Override
    protected boolean showRightButton() {
        return false;
    }

    @Override
    protected void initView(View rootView) {
        edtIPAddr = (EditText) rootView.findViewById(R.id.edt_ip_addr);
        edtPort = (EditText) rootView.findViewById(R.id.edt_port);
        edtDev = (EditText) rootView.findViewById(R.id.edt_device);
        phoneNoTv = (TextView)rootView.findViewById(R.id.tv_phone);
        okBtn =(Button)rootView.findViewById(R.id.btn_commit);
        cancelBtn = (Button)rootView.findViewById(R.id.btn_cancel);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        String ip =LocalDataUtils.getInstance().getServerIp();
        String port =LocalDataUtils.getInstance().getServerPort();
        String devname = LocalDataUtils.getInstance().getDeviceName();

        String phoneno = PhoneInfoUtils.getInstance().getNativePhoneNumber();
        phoneNoTv.setText(phoneno);
        edtIPAddr.setText(ip);
        edtPort.setText(port);
        edtDev.setText(devname);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                String addr = edtIPAddr.getText().toString();
                String port = edtPort.getText().toString();
                String devname = edtDev.getText().toString();
                if(!isIP(addr)){
                    showToast("IP地址不合法，请重新输入");
                    return;
                }
                if(!isPort(port)){
                    showToast("端口号应该为数字，且大于0小于65536");
                    return;
                }
                LocalDataUtils.getInstance().setDeviceName(devname);
                LocalDataUtils.getInstance().setServerIp(addr);
                LocalDataUtils.getInstance().setServerPort(port);
                getFragmentManager().popBackStack();
                break;
            default:
                getFragmentManager().popBackStack();
                break;
        }
    }

    private boolean isPort(String port){
        Pattern pattern = Pattern.compile("^[-+]?[0-9]");
        Matcher mat = pattern.matcher(port);
        boolean isDigit = mat.find();
        if(isDigit){
            int iport = Integer.parseInt(port);
            if(iport>0 && iport<65536){
                return true;
            }
        }
        return false;
    }

    public boolean isIP(String addr)
    {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        //============对之前的ip判断的bug在进行判断
        if (ipAddress==true){
            String ips[] = addr.split("\\.");
            if(ips.length==4){
                try{
                    for(String ip : ips){
                        if(Integer.parseInt(ip)<0||Integer.parseInt(ip)>255){
                            return false;
                        }
                    }
                }catch (Exception e){
                    return false;
                }
                return true;
            }else{
                return false;
            }
        }
        return ipAddress;
    }

}
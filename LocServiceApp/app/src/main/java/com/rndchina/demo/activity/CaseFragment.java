package com.rndchina.demo.activity;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.rndchina.demo.MainActivity;
import com.rndchina.demo.R;
import com.rndchina.demo.base.BaseFragment;
import com.rndchina.demo.base.LoadCallBack;
import com.rndchina.demo.base.MyLocListener;
import com.rndchina.demo.bean.SosMsg;
import com.rndchina.demo.service.MonitorService;
import com.rndchina.demo.util.APPUrl;
import com.rndchina.demo.util.Constants;
import com.rndchina.demo.util.GsonUtil;
import com.rndchina.demo.util.LocationUtil;
import com.rndchina.demo.util.OkHttpManager;
import com.rndchina.demo.util.PhoneInfoUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;

/**
 * Created by xie on 2017/12/21.
 */

public class CaseFragment extends BaseFragment implements MyLocListener,View.OnClickListener, View.OnTouchListener {
    private Spinner caseSpinner;
    private Spinner numberSpinner;
    private EditText beizhuEd;
    private Button sumbitBtn;

    ImageView voiceStart;
    TextView textTransmit;
    TextView voiceTransmit;
    TextView mTvNotice,mTvTime;
    LinearLayout caseText_linear;
    LinearLayout caseVoice_linear;
    private static final String TAG = "CaseFragment";


    private String ajlx;
    private String rs;
    private String jqsm;
    private String dhhm;
    private double yzb;//维度
    private double xzb;//经度
//    private AMapLocationClient mLocationClient = null;

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("audio/mp3");

    //录制
    private File file;
    private ScaleAnimation mScaleBigAnimation;
    private ScaleAnimation mScaleLittleAnimation;
    private String mSoundData = "";//语音数据
    private String dataPath;
    private boolean isStop;  // 录音是否结束的标志 超过两分钟停止
    private boolean isCanceled; // 是否取消录音
    private float downY;
    private MediaRecorder mRecorder;
    private long mStartTime;
    private long mEndTime;
    private int mTime;
    private String randomFileName;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @Override
    protected int getLayoutId() {
        return R.layout.activity_case;
    }

    @Override
    protected boolean showActionBar() {
        return true;
    }

    @Override
    protected String getActionBarTitle() {
        return "案件";
    }

    @Override
    protected boolean showRightButton() {
        return false;
    }

    @Override
    protected void initView(View rootView) {
        initSoundData();
        //经纬度工具类
        LocationUtil instance = LocationUtil.getInstance(getActivity());
        instance.startMonitor();
        instance.addMyLocListener(this);
        //获取电话
        dhhm = PhoneInfoUtils.getInstance().toDhhm();

        caseSpinner = (Spinner) rootView.findViewById(R.id.caseType_spinner);
        numberSpinner = (Spinner) rootView.findViewById(R.id.number_spinner);
        beizhuEd = (EditText) rootView.findViewById(R.id.beizhu_et_activity);
        sumbitBtn = (Button) rootView.findViewById(R.id.submit_button);
        voiceStart = (ImageView) rootView.findViewById(R.id.voiceSubmit_Start);
        caseText_linear = (LinearLayout) rootView.findViewById(R.id.caseText_linear);
        caseVoice_linear = (LinearLayout) rootView.findViewById(R.id.caseVoice_linear);
        textTransmit = (TextView) rootView.findViewById(R.id.textTransmit);
        voiceTransmit = (TextView) rootView.findViewById(R.id.voiceTransmit);
        mTvNotice = (TextView) rootView.findViewById(R.id.chat_tv_sound_notice);
        mTvTime = (TextView) rootView.findViewById(R.id.chat_tv_sound_length);
//        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());

        sumbitBtn.setOnClickListener(this);
        textTransmit.setOnClickListener(this);
        voiceTransmit.setOnClickListener(this);
        sumbitBtn.setOnClickListener(this);
        voiceStart.setOnTouchListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit_button:
                submit();
                break;
            case R.id.textTransmit:
                textTransmit.setBackgroundResource(R.drawable.textright_style);
                voiceTransmit.setBackgroundResource(R.drawable.textleftbai_style);
                textTransmit.setTextColor(this.getResources().getColor(R.color.text_color_bai));
                voiceTransmit.setTextColor(this.getResources().getColor(R.color.text_color_lan));
                caseText_linear.setVisibility(View.VISIBLE);
                caseVoice_linear.setVisibility(View.GONE);
                break;
            case R.id.voiceTransmit:
                textTransmit.setBackgroundResource(R.drawable.textrightbai_style);
                voiceTransmit.setBackgroundResource(R.drawable.textleft_style);
                textTransmit.setTextColor(this.getResources().getColor(R.color.text_color_lan));
                voiceTransmit.setTextColor(this.getResources().getColor(R.color.text_color_bai));
                caseText_linear.setVisibility(View.GONE);
                caseVoice_linear.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void submit() {
        if(caseSpinner.getSelectedItem().toString().equals("请选择案件类型")){
            showToast("请选择案件类型");
            return;
        }
        if (numberSpinner.getSelectedItem().toString().equals("请选择")){
            showToast("请选择警力人数");
            return;
        }
        if (TextUtils.isEmpty(beizhuEd.getText().toString().trim())){
            showToast("请填写简要说明");
            return;
        }
//        if(!(xzb>0)||!(yzb>0)){
//          Toast.makeText(mContext,"无法获取定位信息，发送失败",Toast.LENGTH_SHORT).show();
//
//            return;
//        }
        else {
            ajlx = caseSpinner.getSelectedItem().toString();
            rs= numberSpinner.getSelectedItem().toString();
            jqsm= beizhuEd.getText().toString().trim();

            if(!MainActivity.serviceIsWorked("com.rndchina.demo.service.MonitorService")) {
                showToast("正在开启定位服务");
                getActivity().startService(new Intent(getActivity(), MonitorService.class));
            }else {
                Log.e(TAG, "onSendMyLoc:接口外 xzb"+xzb );
                showProgressDialog();
                SosMsg.ResultBean sosMsgXi = new SosMsg.ResultBean();
                sosMsgXi.setAjlx(ajlx);
                sosMsgXi.setRs(rs);
//                Log.e(TAG, "submit: xzb"+xzb+"=++++"+yzb );
                sosMsgXi.setJqsm(jqsm);
                sosMsgXi.setGyzb(yzb);
                sosMsgXi.setGxzb(xzb);
                if(xzb==0.0&&yzb==0.0){
                    sosMsgXi.setGyzb(Constants.YZB);
                    sosMsgXi.setGxzb(Constants.XZB);
                    Log.e(TAG, "onItemClick: ------yzb"+Constants.YZB );
                }else {
                    Log.e(TAG, "onItemClick:====== xzb"+xzb );
                }
//                sosMsgXi.setGxzb(xzb);
//                sosMsgXi.setGyzb(yzb);
//                sosMsgXi.setGxzb(131.134084188877);
//                sosMsgXi.setGyzb(46.6815882198596);
                sosMsgXi.setDhhm(dhhm);
                sosMsgXi.setZt("97");

                OkHttpManager.getInstance().postFileRequest(APPUrl.SEND, sosMsgXi, file, new LoadCallBack<String>(getActivity().getApplicationContext()) {
                    @Override
                    public void onSuccess(Call call, Response response, String result) {
                        SosMsg sosMsg = GsonUtil.jsontoBean(result);
                        int code = sosMsg.getCode();
                        Log.e(TAG, "onSuccess: code"+code );
                        if(code==1){
                            disMissDialog();
                            Log.e(TAG, "onSuccess: 成功请求" );
                            Intent intent = new Intent(getActivity(),AmapActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putDouble("yzb",yzb);
                            bundle.putDouble("xzb",xzb);
                            if(xzb==0.0&&yzb==0.0){
                                bundle.putDouble("yzb",Constants.YZB);
                                bundle.putDouble("xzb",Constants.XZB);
                                Log.e(TAG, "onItemClick: yzb"+Constants.YZB );
                            }

//                            bundle.putDouble("yzb",46.6815882198596);
//                            bundle.putDouble("xzb",131.134084188877);
                            bundle.putString("dhhm",dhhm);
                            intent.putExtras(bundle);
                            getActivity().startActivity(intent);
                        }


                    }
                    @Override
                    public void onEror(Call call, int statusCode, Exception e) {
                        Log.e(TAG, "onEror: e"+e.getMessage() );
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                });
            }
        }


    }

    @Override
    public void onSendMyLoc(AMapLocation aMapLocation, LocationUtil.MLocation mLoc) {
        this.yzb = mLoc.latitude;
        this.xzb = mLoc.longitude;
        Log.e(TAG, "onSendMyLoc:接口 xzb"+xzb );
    }

    /**
     * 录制语音监听事件
     * @param view
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //当手指第一次按下的时候，执行的操作
                isCanceled = false;
                downY = event.getY();
                voiceStart.setImageDrawable(getResources().getDrawable(R.drawable.record_pressed));
                mTvNotice.setText("向上滑动取消发送");
                randomFileName = getRandomFileName();
                mSoundData = dataPath + randomFileName + ".amr";
                if (mRecorder != null) {
                    mRecorder.reset();
                } else {
                    mRecorder = new MediaRecorder();
                }
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                mRecorder.setOutputFile(mSoundData);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setAudioEncodingBitRate(8000);
                mRecorder.setAudioSamplingRate(8000);
                mRecorder.setAudioChannels(1);
                try {
                    mRecorder.prepare();
                } catch (IOException e) {
                    Log.i("recoder", "prepare() failed-Exception-" + e.toString());
                }
                try {

                    mRecorder.start();
                    mStartTime = System.currentTimeMillis();
                    mTvTime.setText("0" + '"');
                    // TODO 开启定时器
                    mHandler.postDelayed(runnable, 1000);
                } catch (Exception e) {
                    Log.i("recoder", "prepare() failed-Exception-"+e.toString());
                }
                initScaleAnim();
                // TODO 录音过程重复动画
                mScaleBigAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (mScaleLittleAnimation != null) {
                            voiceStart.startAnimation(mScaleLittleAnimation);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mScaleLittleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (mScaleBigAnimation != null) {
                            voiceStart.startAnimation(mScaleBigAnimation);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                voiceStart.startAnimation(mScaleBigAnimation);
                break;
            case MotionEvent.ACTION_MOVE:
                //当手指一直按在屏幕上，并且移动的时候执行的操作
                float moveY = event.getY();
                if (downY - moveY > 100) {
                    isCanceled = true;
                    mTvNotice.setText("松开手指可取消录音");
                    voiceStart.setImageDrawable(getResources().getDrawable(R.drawable.record));
                }
                if (downY - moveY < 20) {
                    isCanceled = false;
                    voiceStart.setImageDrawable(getResources().getDrawable(R.drawable.record_pressed));
                    mTvNotice.setText("向上滑动取消发送");
                }
                break;
            case MotionEvent.ACTION_CANCEL:// 首次开权限时会走这里，录音取消
                Log.i("record_test","权限影响录音录音");
                mHandler.removeCallbacks(runnable);

                // TODO 这里一定注意，先release，还要置为null，否则录音会发生错误，还有可能崩溃
                if (mRecorder != null) {
                    mRecorder.release();
                    mRecorder = null;
                    System.gc();
                }
                voiceStart.setImageDrawable(getResources().getDrawable(R.drawable.record));
                voiceStart.clearAnimation();
                mTvNotice.setText("按住说话");
                isCanceled = true;
                mScaleBigAnimation = null;
                mScaleLittleAnimation = null;
                break;
            case MotionEvent.ACTION_UP:
                if (!isStop) {
                    mEndTime = System.currentTimeMillis();
                    mTime = (int) ((mEndTime - mStartTime) / 1000);
                    stopRecord();
                    voiceStart.setVisibility(View.VISIBLE);
                    if (isCanceled) {
                        Log.e(TAG, "onTouch: 录音取消"+ isCanceled);
                        deleteSoundFileUnSend();
                        mTvTime.setText("0" +'"' );
                        mTvNotice.setText("录音取消");
                    } else {
                        voiceStart.setImageDrawable(getResources().getDrawable(R.drawable.record));
                    }
                }else {
                    mTvTime.setText("0");
                    voiceStart.setImageDrawable(getResources().getDrawable(R.drawable.record));
                    mTvNotice.setText("重新录音");
                }
                break;

        }
        return true;
    }

    // 定时器
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                long endTime = System.currentTimeMillis();
                int time = (int) ((endTime - mStartTime) / 1000);
                mTvTime.setText(time + "" + '"');
                // 限制录音时间不长于两分钟
                if (time > 119) {
                    isStop = true;
                    mTime = time;
                    stopRecord();
                    showToast("时间过长");
                } else {
                    mHandler.postDelayed(this, 1000);
                    isStop = false;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    /**
     * 结束录音
     */
    public void stopRecord() {
        voiceStart.clearAnimation();
        mScaleBigAnimation = null;
        mScaleLittleAnimation = null;
        if (mTime < 1) {
            deleteSoundFileUnSend();
            isCanceled = true;
            showToast("录音时间太短，长按开始录音");
        } else {
            Log.e(TAG, "stopRecord: 录音成功"+isCanceled);
            mTvNotice.setText("录音成功");
//             不加  "" 空串 会出  Resources$NotFoundException 错误
            mTvTime.setText(mTime + "" + '"');
        }
        try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            if(isCanceled == false){
                Log.e(TAG, "stopRecord: isCanceled:"+isCanceled );
                postFile();
            }
        } catch (Exception e) {
            isCanceled = true;
            voiceStart.setVisibility(View.VISIBLE);
            mTvTime.setText("");
            showToast("录音发生错误,请重新录音");
        }
        mHandler.removeCallbacks(runnable);
        if (mRecorder != null) {
            mRecorder = null;
            System.gc();
        }

    }


    /**
     * 初始化录音动画
     */
    public void initScaleAnim() {

        // TODO 放大
        mScaleBigAnimation = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleBigAnimation.setDuration(700);

        // TODO 缩小
        mScaleLittleAnimation = new ScaleAnimation(1.3f, 1.0f, 1.3f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleLittleAnimation.setDuration(700);

    }

    /**
     * 录音完毕后，若不发送，则删除文件
     */
    public void deleteSoundFileUnSend() {
        mTime = 0;
        if (!"".equals(mSoundData)) {
            try {
                File file = new File(mSoundData);
                file.delete();
                mSoundData = "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成一个随机的文件名
     * @return
     */
    public String getRandomFileName() {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        rel = rel + new Random().nextInt(1000)+(10000 + new Random().nextInt(89999));
        return rel;
    }

    /**
     * 录音存放路径
     */
    public void initSoundData() {
        dataPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AsRecrod/Sounds/";
        File folder = new File(dataPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * 发送语音文件
     */
    private void postFile() {
        SosMsg.ResultBean sosMsgXi = new SosMsg.ResultBean();
//        sosMsgXi.setGxzb(131.134084188877);
//        sosMsgXi.setGyzb(46.6815882198596);
        sosMsgXi.setGyzb(yzb);
        sosMsgXi.setGxzb(xzb);
        if(xzb==0.0&&yzb==0.0){
            sosMsgXi.setGyzb(Constants.YZB);
            sosMsgXi.setGxzb(Constants.XZB);
            Log.e(TAG, "onItemClick: ------yzb"+Constants.YZB );
        }else {
            Log.e(TAG, "onItemClick:====== xzb"+xzb );
        }
        sosMsgXi.setDhhm(dhhm);
        sosMsgXi.setZt("98");
        file = new File(dataPath,randomFileName+".amr");
        if (!file.exists()) {
        showToast("文件不存在");
        }
        showProgressDialog();
        OkHttpManager.getInstance().postFileRequest(APPUrl.SEND, sosMsgXi, file,new LoadCallBack<String>(getActivity().getApplicationContext()) {
            @Override
            public void onSuccess(Call call, Response response, String result) {
                SosMsg sosMsg = GsonUtil.jsontoBean(result);
                int code = sosMsg.getCode();
                Log.e(TAG, "onSuccess: code"+code );
                if(code==1){
                    disMissDialog();
                    Intent intent = new Intent(getActivity(),AmapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("yzb",yzb);
                    bundle.putDouble("xzb",xzb);
                    if(xzb==0.0&&yzb==0.0){
                        bundle.putDouble("yzb",Constants.YZB);
                        bundle.putDouble("xzb",Constants.XZB);
                        Log.e(TAG, "onItemClick: yzb"+Constants.YZB );
                    }

//                    bundle.putDouble("xzb",131.134084188877);
//                    bundle.putDouble("xzb",46.6815882198596);
                    bundle.putString("dhhm",dhhm);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }

            }
            @Override
            public void onEror(Call call, int statusCode, Exception e) {

            }
            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }
}

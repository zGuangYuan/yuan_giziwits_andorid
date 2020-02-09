package com.yuan_giziwits_andorid.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.enumration.GizWifiGAgentType;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.yuan_giziwits_andorid.MainActivity;
import com.yuan_giziwits_andorid.Quit.MyApplication;
import com.yuan_giziwits_andorid.R;
import com.yuan_giziwits_andorid.Utils.SharePreferenceUtils;
import com.yuan_giziwits_andorid.Utils.WifiAdminUtils;

import java.util.ArrayList;
import java.util.List;

import static com.gizwits.gizwifisdk.enumration.GizWifiGAgentType.GizGAgentESP;

public class NetConfigActivity extends AppCompatActivity {

    //密码编辑框
    private  EditText mPassward_box;
    //密码显示隐藏按钮
    private CheckBox mPas_Control;
    //显示SSID文本框
    private TextView mSSID_box;
    //开始搜索设备按钮
    private Button mBtn_start_search;
    //获取当前wifi的封装类
    private WifiAdminUtils adminUtils;
    //进度弹窗控件
    private ProgressDialog NetConfig_Dialog;

    @SuppressLint("HandlerLeak")
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //配网成功
            if(msg.what== 110){
                NetConfig_Dialog.setMessage("配网成功");
                //有回调结果把弹窗的按钮隐藏
                NetConfig_Dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                NetConfig_Dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
            }else if(msg.what == 111)
            {
                NetConfig_Dialog.setMessage("配网失败");
                //有回调结果把弹窗的按钮隐藏
                NetConfig_Dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                NetConfig_Dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);
        //全屏显示
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_net_config);
        //初始化view
        initView();
        //获取从网络获取到本地的uid和token
        String uid   =SharePreferenceUtils.getString(NetConfigActivity.this,"_uid",null);
        String token =SharePreferenceUtils.getString(NetConfigActivity.this,"_token",null);
        Log.e("Net_yuangege","uid: "+uid);
        Log.e("Net_yuangege","uid: "+token);
    }

    /**
     * 当Activity开始准备与用户交互时调用
     */
    @Override
    protected void onResume() {
        super.onResume();
        //拿到手机当前连接的wifi名字
        String ssid =adminUtils.getWifiConnectedSsid();
        //判断是否拿到了WiFi名字
        if(ssid!= null){
            mSSID_box.setText(ssid);
        }else{
            mSSID_box.setText("");
        }
        //如果当前没有WiFi连接，那么密码输入框不可输入，按钮不可点击
        boolean isEmptyPass = TextUtils.isEmpty(ssid);
        if(isEmptyPass){
            mPassward_box.setEnabled(false);
            mBtn_start_search.setEnabled(false);
        }
    }

    private void initView() {
        //初始化wifi封装类
        adminUtils =new WifiAdminUtils(this);
        //实例化控件
        mSSID_box =findViewById(R.id.tv_ssid_ID);           //wifi名称显示窗口
        mBtn_start_search=findViewById(R.id.btn_search_ID); //搜索按钮窗口
        mPassward_box =findViewById(R.id.et_password_ID);   //密码编辑框
        mPas_Control =findViewById(R.id.cb_pw_control_ID);  //密码显示隐藏按钮
        QMUITopBar topBar = findViewById(R.id.Net_Config_topBar_ID); //topbar设置
        topBar.setTitle("添加设备");
        topBar.addLeftImageButton(R.mipmap.ic_back,R.id.topBar_left_back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //回到上一个界面
                startActivity(new Intent(NetConfigActivity.this, MainActivity.class));
                finish();
            }
        });

        //对密码框进行动态监听
        mPassward_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            //文本框编辑时候的一个回调函数
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //改变中触发,文本框不为空
                if(!charSequence.toString().isEmpty()){
                    //显示眼睛
                    mPas_Control.setVisibility(View.VISIBLE);
                }else {
                    //否则隐藏
                    mPas_Control.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        //默认眼睛是隐藏的
        mPas_Control.setVisibility(View.GONE);
        //眼睛按钮的监听事件
        mPas_Control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //如果被点击了之后
                if(b){
                    mPassward_box.setInputType(0x90); //显示密码
                }else{
                    mPassward_box.setInputType(0x81); //否则隐藏
                }
            }
        });
        //搜索wifi按钮的监听事件
        mBtn_start_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //得到wifi和密码
                String mssid = mSSID_box.getText().toString().intern(); //且去空格
                String mPasw = mPassward_box.getText().toString().intern();
                //wifi名字和密码都不为空
                if(!mssid.isEmpty()&& !mPasw.isEmpty()){

                    NetConfig_Dialog = new ProgressDialog(NetConfigActivity.this,5);
                    NetConfig_Dialog.setMessage("正在努力配网中...");
                    NetConfig_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    NetConfig_Dialog.setCancelable(false);//屏幕外不可点击
                    NetConfig_Dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                        //使Dialog消失
                                        NetConfig_Dialog.dismiss();
                                }
                            }
                    );
                    NetConfig_Dialog.setButton(DialogInterface.BUTTON_POSITIVE, "好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NetConfig_Dialog.dismiss();
                                    //摧毁当前Activity,返回上一层
                                    finish();
                                }
                            }

                    );
                    NetConfig_Dialog.show(); //显示弹窗
                    //没有回调结果把弹窗的按钮隐藏
                    NetConfig_Dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
                    NetConfig_Dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);


                    //开始配网
                    startAirLink(mssid,mPasw);

                }
                else{
                    Toast.makeText(NetConfigActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void startAirLink(String ssid,String pasw){
        GizWifiSDK.sharedInstance().setListener(listener);
        //List输入为泛型，类型为GizWifiGAgentType中的一种
        List<GizWifiGAgentType> types =new  ArrayList<>();
        types.add(GizGAgentESP); //只需要8266的配网

        //GizWifiSDK.sharedInstance().setDeviceOnboardingDeploy();  //新版的配网方式
        GizWifiSDK.sharedInstance().setDeviceOnboarding(ssid,pasw,
                GizWifiConfigureMode.GizWifiAirLink,
                null,
                30,types);       //旧版的配网方式
    }
    private GizWifiSDKListener listener = new GizWifiSDKListener(){
        //回调方法
        @Override
        public void didSetDeviceOnboarding(GizWifiErrorCode result, GizWifiDevice device) {
            super.didSetDeviceOnboarding(result, device);
            if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS){

                        // 配置成功
                    mHandler.sendEmptyMessage(110);
            }
            else {
                        // 配置失败
                    mHandler.sendEmptyMessage(111);
            }


        }
    };
}

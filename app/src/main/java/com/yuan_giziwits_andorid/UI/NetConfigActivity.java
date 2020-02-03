package com.yuan_giziwits_andorid.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUITopBar;
import com.yuan_giziwits_andorid.MainActivity;
import com.yuan_giziwits_andorid.R;

public class NetConfigActivity extends AppCompatActivity {

    //密码编辑框
    private  EditText mPassward_box;
    //密码显示隐藏按钮
    private CheckBox mPas_Control;
    //显示SSID文本框
    private TextView mSSID_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_net_config);
        initView();


    }

    private void initView() {
        QMUITopBar topBar = findViewById(R.id.Net_Config_topBar_ID);
        topBar.setTitle("添加设备");
        topBar.addLeftImageButton(R.mipmap.ic_back,R.id.topBar_left_back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //回到上一个界面
                startActivity(new Intent(NetConfigActivity.this, MainActivity.class));
                finish();
            }
        });
        //实例化控件
        mPassward_box =findViewById(R.id.et_password_ID);
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

        mPas_Control =findViewById(R.id.cb_pw_control_ID);
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
    }
}

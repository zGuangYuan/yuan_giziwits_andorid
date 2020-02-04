package com.yuan_giziwits_andorid.DevicrControl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.qmuiteam.qmui.widget.QMUITopBar;
import com.yuan_giziwits_andorid.R;

public class MainDeviceControlActivity extends AppCompatActivity {

    /*变量的声明*/

    //顶层框
    private QMUITopBar DeviceControltopBar;
    //进入七彩灯控制的按钮
    private Button mBtn_EnterColorControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为全屏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_device_control);
        //控件初始化
        viewInit();
    }
    /**
     * 功能：控件初始化及其各个按钮的事件的监听
     */
    private void viewInit() {
        //QUMI的 topBar的设置
        //控件实例化
        DeviceControltopBar = findViewById(R.id.DeviceControl_topBar_ID);

        mBtn_EnterColorControl =findViewById(R.id.color_control_enter_ID);
        DeviceControltopBar.setTitle("设备控制界面");
        DeviceControltopBar.addLeftImageButton(R.mipmap.ic_back,R.id.topBar_right_add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtn_EnterColorControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainDeviceControlActivity.this,RGBLightActivity.class));
            }
        });

    }

}

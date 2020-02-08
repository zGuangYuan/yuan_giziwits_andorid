package com.yuan_giziwits_andorid.DevicrControl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.yuan_giziwits_andorid.R;

public class SecondDeviceCtrlActivity extends BaseDeviceControlActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_device_ctrl);
        
        initView();
    }

    private void initView() {
        //topbar控件
        mTopBar = findViewById(R.id.SecondDeviceControl_topBar_ID);
        /*判断别名是否为空，空则选择别名*/
        String deviceTitle = mDevice.getAlias().isEmpty()?mDevice.getProductName():mDevice.getAlias();
        mTopBar.setTitle(deviceTitle+" 的设备控制界面");
        /*顶层状态栏设计*/
        mTopBar.addLeftImageButton(R.mipmap.ic_back,R.id.topBar_right_add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

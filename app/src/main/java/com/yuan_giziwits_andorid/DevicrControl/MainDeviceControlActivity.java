package com.yuan_giziwits_andorid.DevicrControl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yuan_giziwits_andorid.R;
import com.yuan_giziwits_andorid.UI.PatternLockViewActivity;
import com.yuan_giziwits_andorid.Utils.SharePreferenceUtils;

public class MainDeviceControlActivity extends AppCompatActivity {

    /*变量的声明*/
    //门禁的密码
    public static String door_pasw="abc";

    //顶层框
    private QMUITopBar DeviceControltopBar;
    //进入七彩灯控制的按钮
    private Button mBtn_EnterColorControl;

    //门禁开、关、设置的ImageButton
    private ImageButton ib_door_open;
    private ImageButton ib_door_close;
    private ImageButton ib_door_setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为全屏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main_device_control);
        //控件初始化
        viewInit();
    }
    /**
     * 功能：控件初始化及其各个按钮的事件的监听
     */
    private void viewInit() {
        //QUMI的 topBar的设置
        /*控件实例化*/
        //topbar控件
        DeviceControltopBar = findViewById(R.id.DeviceControl_topBar_ID);
        //七彩灯控制控件
        mBtn_EnterColorControl =findViewById(R.id.color_control_enter_ID);
        //门禁开关设置控件
        ib_door_open = (ImageButton) findViewById(R.id.IV_ButtonID);
        ib_door_close= (ImageButton) findViewById(R.id.IV_closeButtonID);
        ib_door_setting =(ImageButton)findViewById(R.id.IV_DoorSettingButtonID);

        /*设置控制界面的topbar*/
        DeviceControltopBar.setTitle("设备控制界面");
        DeviceControltopBar.addLeftImageButton(R.mipmap.ic_back,R.id.topBar_right_add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*进入七彩灯控制的按钮*/
        mBtn_EnterColorControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainDeviceControlActivity.this,RGBLightActivity.class));
            }
        });


        /*绑定门禁按钮开的监听器*/
        ib_door_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入手势解锁界面
                startActivity(new Intent(MainDeviceControlActivity.this, PatternLockViewActivity.class));
            }
        });
        /*绑定门禁密码管理的监听器*/
        ib_door_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入密码修改页面
                startActivity(new Intent(MainDeviceControlActivity.this, LockActivity.class));
            }
        });

    }

    /**
     * 功能：存储开锁密码
     */
    @Override
    protected void onResume() {
        super.onResume();

    }
}

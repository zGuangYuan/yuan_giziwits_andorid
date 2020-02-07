package com.yuan_giziwits_andorid.DevicrControl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yuan_giziwits_andorid.MainActivity;
import com.yuan_giziwits_andorid.R;
import com.yuan_giziwits_andorid.UI.PatternLockViewActivity;
import com.yuan_giziwits_andorid.Utils.SharePreferenceUtils;

import java.util.concurrent.ConcurrentHashMap;

/*继承自父类，其中父类有一个从上一个界面获取对象的一个方法
* 即使在这个类中不调用。也就自动执行。*/
public class MainDeviceControlActivity extends BaseDeviceControlActivity {

    /*变量的声明*/

    ConcurrentHashMap<String, Object> dataMap;

    //顶层框由父类继承
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
        mTopBar = findViewById(R.id.DeviceControl_topBar_ID);
        //七彩灯控制控件
        mBtn_EnterColorControl =findViewById(R.id.color_control_enter_ID);
        //门禁开关设置控件
        ib_door_open = (ImageButton) findViewById(R.id.IV_ButtonID);
        ib_door_close= (ImageButton) findViewById(R.id.IV_closeButtonID);
        ib_door_setting =(ImageButton)findViewById(R.id.IV_DoorSettingButtonID);

        /*设置控制界面的topbar*/
        //设置的标题是设备的别名或者项目的名字，如果别名为空则显示项目的名字
          //注意这个mDevice是父类实现的，所以在这个类也会继承下来
//        if(mDevice.getAlias().isEmpty()){
//            //别名为空,设置为产品的名字
//            DeviceControltopBar.setTitle(mDevice.getProductName()+" 设置控制界面");
//        }else{
//            //设置为别名
//            DeviceControltopBar.setTitle(mDevice.getAlias()+" 的设备控制界面");
//        }
        //判断别名是否为空，空则选择别名
        String deviceTitle = mDevice.getAlias().isEmpty()?mDevice.getProductName():mDevice.getAlias();
        mTopBar.setTitle(deviceTitle+" 的设备控制界面");


        mTopBar.addLeftImageButton(R.mipmap.ic_back,R.id.topBar_right_add_icon).setOnClickListener(new View.OnClickListener() {
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


    /**
     * 复写父类的方法，调用云端数据
     * @param dataMap 保存云端传回来的数据，可控子类调用的方法
     */
    @Override
    protected void receiveCloudData(ConcurrentHashMap<String, Object> dataMap) {
        super.receiveCloudData(dataMap);
        Log.e("yuan12312","子类调用云端数据:"+dataMap);
    }
}

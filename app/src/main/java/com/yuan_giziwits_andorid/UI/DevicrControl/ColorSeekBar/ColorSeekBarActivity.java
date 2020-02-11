package com.yuan_giziwits_andorid.UI.DevicrControl.ColorSeekBar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.yuan_giziwits_andorid.ColorCircularSeekBar.ColorCircularSeekBar;
import com.yuan_giziwits_andorid.ColorCircularSeekBar.ColorTempCircularSeekBar;
import com.yuan_giziwits_andorid.Quit.MyApplication;
import com.yuan_giziwits_andorid.R;
import com.yuan_giziwits_andorid.UI.DevicrControl.BaseDeviceControlActivity;

import java.util.concurrent.ConcurrentHashMap;

public class ColorSeekBarActivity extends BaseDeviceControlActivity  {

    /*
     * ======================================================================
     * 以下定义的字符串是根据云端数据点标识名来定义的
     * ======================================================================
     */
    /** 数据点"开关" 对应的标识名 */
    protected static final String KEY1_ONOFF = "OnOff";
    /** 数据点"每周重复" 对应的标识名 */
    protected static final String KEY2_WEEK_REPEAT = "Week_Repeat";
    /** 数据点"定时开机" 对应的标识名 */
    protected static final String KEY3_TIME_ON_MINUTE = "Time_On_Minute";
    /** 数据点"定时关机" 对应的标识名 */
    protected static final String KEY4_TIME_OFF_MINUTE = "Time_Off_Minute";
    /** 数据点"倒计时" 对应的标识名 */
    protected static final String KEY5_COUNTDOWN_MINUTE = "CountDown_Off_min";
    /** 数据点"能耗" 对应的标识名 */
    protected static final String KEY6_POWER_CONSUMPTION = "Power_Consumption";
    /** 数据点"是否启用定时" 对应的标识名 */
    protected static final String KEY7_TIME_ONOFF = "Time_OnOff";
    /** 数据点"是否启用倒计时" 对应的标识名 */
    protected static final String KEY8_COUNTDOWN_ONOFF = "CountDown_Switch";

    /*
     * ======================================================================
     * 以下定义的是云端数据点对应的存储值
     * ======================================================================
     */
    /** 数据点"Power_Switch"对应的值**/
    protected static Boolean isPowerOn=false;
    /** 数据点Time_OnOff对应的值 */
    protected static Boolean isOpenTiming = false;
    /** 数据点CountDown_OnOff对应的值 */
    protected static Boolean isOpenDelaying = false;
    /** 数据点Power_Consumption对应的值 */
    protected static int numOfConsumption = 0;
    /** 数据点Time_On_Minute对应的值 */
    protected static int timeOnMinute = 0;
    /** 数据点Time_Off_Minute对应的值 */
    protected static int timeOffMinute = 1439;
    /** 数据点CountDown_Minute对应的值 */
    protected static int countDownMinute = 0;
    /** 数据点weekRepeat对应的值 */
    protected static int weekRepeat = 31;
    /** 数据点"Brightness"对应的值**/
    protected static int bringhtness_num=0;
    /** 数据点"mode"对应的值**/
    protected static int mode_num=0;
    /** 数据点"Color_R"对应的值**/
    protected static int color_num_r=0;
    /** 数据点"Color_G"对应的值**/
    protected static int color_num_g=0;
    /** 数据点"Color_B"对应的值**/
    protected static int color_num_b=0;
    /** 数据点"Temperature_R"对应的值**/
    protected static int color_num_temp_r=0;
    /** 数据点"Temperature_G"对应的值**/
    protected static int color_num_temp_g=0;
    /** 数据点"Temperature_B"对应的值**/
    protected static int color_num_temp_b=0;


    /*************控件变量声明*************************/
    private TextView tv_ColorNum;

    /** 返回按钮 */
    private ImageView ivBack;

    /** 标题TextView */
    private TextView tvTitle;

    /** 设置按钮 */
    private ImageView ivSetting;

    /** 延时功能布局 */
    private RelativeLayout rlDelay;

    /** 延时TextView */
    private TextView tvDelay;

    /** 开关按钮 */
    private Button btnPower;

    /** 开关TextView */
    private TextView tvPower;

    /** 整个布局覆盖ImageView */
    private ImageView ivmain;

    /** 整个布局 */
    private RelativeLayout rl_middle;

    /** 关灯布局ImageView */
    private ImageView ivPowerOff;

    /** 中部UI布局 */
    private RelativeLayout rl_top;

    /** 模式切换按钮 */
    private Button btnmode;

    /** 色彩进度条*/
    private ColorCircularSeekBar circularSeekBar;

    /** 色温进度条*/
    private ColorTempCircularSeekBar colorTempCircularSeekBar;

    /** 亮度进度条 */
    private SeekBar sbBrighteness;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_color_seek_bar);
        initView();
       //initEvent();
      // initDevice();
      upDateUI();
    }

    /**
     * 更新UI
     */
    protected void upDateUI() {
        //更新界面标题
        updateTitle();
        //更新电源状态：从云端获取
        updatePowerUI(isPowerOn);
       // updateColor();

    }
    private void updateTitle() {
        tvTitle.setText(mDevice.getProductName());
    }

    /**
     * 更新电源开关切换.
     */
    private void updatePowerUI(boolean isPower) {
        if (!isPower) {
            btnPower.setSelected(true);
            tvPower.setText(getString(R.string.openlight));
            ivmain.setVisibility(View.VISIBLE);
            rl_top.setVisibility(View.VISIBLE);
        } else {
            btnPower.setSelected(false);
            tvPower.setText(getString(R.string.closelight));
            ivPowerOff.setVisibility(View.INVISIBLE);
            rl_middle.setVisibility(View.VISIBLE);
            ivmain.setVisibility(View.INVISIBLE);
        }
    }

    private void initView() {

        sbBrighteness = findViewById(R.id.sbBrighteness);


        //显示RGB颜色
        tv_ColorNum=(TextView) findViewById(R.id.color_num_ID);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivSetting = (ImageView) findViewById(R.id.ivSetting);
        btnPower = (Button) findViewById(R.id.btnPower);
        tvPower = (TextView) findViewById(R.id.tvPower);
        rl_top = (RelativeLayout) findViewById(R.id.rl_top);
        ivmain = (ImageView) findViewById(R.id.ivmain);
        rl_middle = (RelativeLayout) findViewById(R.id.rl_middle);
        ivPowerOff = (ImageView) findViewById(R.id.ivPowerOff);
        //延时预约
        rlDelay = (RelativeLayout) findViewById(R.id.rlDelay);
        tvDelay = (TextView) findViewById(R.id.tvDelay);
        //模式切换
        btnmode = (Button) findViewById(R.id.btnmode);

        //色彩的进度条
        circularSeekBar = (ColorCircularSeekBar) findViewById(R.id.csbSeekbar2);
        circularSeekBar.postInvalidateDelayed(2000);
        circularSeekBar.setMaxProgress(100);
        circularSeekBar.setProgress(30);
        circularSeekBar.setMProgress(0);
        circularSeekBar.postInvalidateDelayed(100);

        //色环改变监听器
        circularSeekBar.setSeekBarChangeListener(new ColorCircularSeekBar.OnSeekChangeListener() {
            @Override
            public void onProgressChange(ColorCircularSeekBar view, int color) {
                // TODO Auto-generated method stub

                cColor(mDevice, color);
                tv_ColorNum.setText("R:" +String.valueOf(color_num_r)+",G:"+String.valueOf(color_num_g)+",B:"+String.valueOf(color_num_b));
            }

        });
        sbBrighteness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bringhtness_num = seekBar.getProgress();
                tv_ColorNum.setText(String.valueOf(bringhtness_num));
                cBrightness( mDevice, seekBar.getProgress());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });

        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPowerAction();
            }
        });






    }
    /* 色彩*/
    public void cColor(GizWifiDevice device, int color){
        sendMoreCommand("Color_R", Color.red(color),
                "Color_G", Color.green(color), "Color_B", Color.blue(color));
        color_num_r = Color.red(color);
        color_num_g = Color.green(color);
        color_num_b = Color.blue(color);
    }
    /*
     * 更新色彩:从云端更新
     */
    private void updateColor(int color) {
        circularSeekBar.setInnerColor(Color.argb(255,color_num_r, color_num_g, color_num_b));

    }

    /* 亮度*/
    public void cBrightness( GizWifiDevice device, int bringhtness_num) {
        sendCommendToCloud("Brightness", bringhtness_num);
    }

    /* 开关*/
    private void btnPowerAction() {
        sendCommendToCloud("Power_Switch", !isPowerOn);
        isPowerOn =!isPowerOn;
        if (!isPowerOn == true) {
            //未打开电源
            ivmain.setVisibility(View.VISIBLE);
            rl_top.setVisibility(View.VISIBLE);
            powerOff();


        } else {  //电源打开
            ivPowerOff.setVisibility(View.INVISIBLE);
            rl_middle.setVisibility(View.VISIBLE);
            ivmain.setVisibility(View.INVISIBLE);
        }
    }
    private void powerOff() {
        Bitmap mBitmap = Bitmap.createBitmap(rl_middle.getWidth(), rl_middle.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        rl_middle.draw(canvas);
        ivPowerOff.setVisibility(View.VISIBLE);
        ivPowerOff.setImageBitmap(mBitmap);
        rl_middle.setVisibility(View.INVISIBLE);

    }


    @Override
    protected void receiveCloudData(ConcurrentHashMap<String, Object> dataMap) {
        super.receiveCloudData(dataMap);
        getDataFromDateMap(dataMap);

    }
    public void sendMoreCommand(String key, Object value,String key1, Object value1,String key2, Object value2) {
//		int sn = 5; // 如果App不使用sn，此处可写成 int sn = 0;
        int sn = 0;
        ConcurrentHashMap<String, Object> command = new ConcurrentHashMap<String, Object>();
        command.put(key, value);
        command.put(key1, value1);
        command.put(key2, value2);
        mDevice.write(command, sn);
    }

    /**
     * <p>
     * Description:从设备返回的dataMap中获取数据
     * </p>
     *
     * @return true 获取成功，false 获取失败，表示接收到数据为空
     */
    @SuppressWarnings("unchecked")
    protected void getDataFromDateMap(ConcurrentHashMap<String, Object> dataMap) {
        // 已定义的设备数据点，有布尔、数值和枚举型数据
        if (dataMap.get("data") != null) {
            ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");

            for (String key : map.keySet()) {
                // 开关
                if (key.equals("Power_Switch")) {
                    isPowerOn = (Boolean) map.get(key);
                }
                //亮度
                if (key.equals("Brightness")) {
                    bringhtness_num = (Integer) map.get(key);
                }
                //模式
                if (key.equals("mode")) {
                    mode_num = (Integer) map.get(key);
                }
                //颜色调节_红色
                if (key.equals("Color_R")) {
                    color_num_r = (Integer) map.get(key);
                }
                //颜色调节_绿色
                if (key.equals("Color_G")) {
                    color_num_g = (Integer) map.get(key);
                }
                //颜色调节_蓝色
                if (key.equals("Color_B")) {
                    color_num_b = (Integer) map.get(key);
                }
                //色温调节_红色
                if (key.equals("Temperature_R")) {
                    color_num_temp_r = (Integer) map.get(key);
                }
                //色温调节_绿色
                if (key.equals("Temperature_G")) {
                    color_num_temp_g = (Integer) map.get(key);
                }
                //色温调节_蓝色
                if (key.equals("Temperature_B")) {
                    color_num_temp_b = (Integer) map.get(key);
                }
                //延时关灯
                if (key.equals("CountDown_Off_min")) {
                    countDownMinute = (Integer) map.get(key);
                }
                //开启关闭延时
                if (key.equals("CountDown_Switch")) {
                    isOpenDelaying = (Boolean) map.get(key);
                }

            }
        }
    }
}

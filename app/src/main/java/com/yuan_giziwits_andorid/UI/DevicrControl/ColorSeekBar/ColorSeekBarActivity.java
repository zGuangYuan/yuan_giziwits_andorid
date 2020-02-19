package com.yuan_giziwits_andorid.UI.DevicrControl.ColorSeekBar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yuan_giziwits_andorid.ColorSeekBar.ColorCircularSeekBar;
import com.yuan_giziwits_andorid.Quit.MyApplication;
import com.yuan_giziwits_andorid.R;
import com.yuan_giziwits_andorid.UI.DevicrControl.BaseDeviceControlActivity;
import com.yuan_giziwits_andorid.UI.NetConfigActivity;
import com.yuan_giziwits_andorid.Utils.L;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ColorSeekBarActivity extends BaseDeviceControlActivity  {

    private int mColor;
    private float mHue;//色调范围0-360
    private float mSat;//饱和度范围0-1
    private float mVal;//亮度范围0-1

    /*
     * ======================================================================
     * 以下定义的是云端数据点对应的存储值
     * ======================================================================
     */
    /** 数据点"Power_Switch"对应的值**/
    protected static Boolean isPowerOn=false;
    /** 数据点"TimerOn_Switch"对应的值**/
    protected static Boolean isTimerOn=false;
    /** 数据点"TimerOff_Switch"对应的值**/
    protected static Boolean isTimerOff=false;
    /** 数据点"CountDown_On_value"对应的值**/
    protected static int timeOnMinute=0;
    /** 数据点"CountDown_Off_value"对应的值**/
    protected static int timeOffMinute=0;
    /** 数据点"Brightness"对应的值**/
    protected static int bringhtness_num=0;
    /** 数据点"Color_R"对应的值**/
    protected static int color_num_r=0;
    /** 数据点"Color_R"对应的值**/
    protected static int color_num_g=0;
    /** 数据点"Color_B"对应的值**/
    protected static int color_num_b=0;


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



    /** 色彩进度条*/
    private ColorCircularSeekBar circularSeekBar;


    //在本类应用的云端数据点
    ConcurrentHashMap<String, Object> map;

    /** 亮度进度条 */
    private SeekBar sbBrighteness;
    /**设置冷暖灯按钮*/
    private Button btn_White;
    private Button btn_Warm;
    /**延时开关指示*/
    private CheckBox delay_OnOff_indicate;
    /**延时的文本指示*/
    private TextView tv_delay_indicate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        //全屏模式
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_color_seek_bar);
        initView();
    }

    /**
     * 更新UI
     */
    protected void upDateUI() {

        //更新界面标题
        updateTitle();
        //更新电源状态：从云端获取
        updatePowerUI(isPowerOn);
        //更新色彩
        updateColor(Color.argb(255,color_num_r, color_num_g, color_num_b));
        //更新亮度值
        updateBrighteness(bringhtness_num);
        //把上次的颜色还原到存储在mColor变量之中,将RGB ->int类型
        mColor=Color.rgb(color_num_r, color_num_g, color_num_b);
        //更新定时器UI本地变量和UI的状态
        updateTimerStates();

    }

    private void updateTimerStates() {
        //重要有其中定时打开
        if(isTimerOn || isTimerOff){
            delay_OnOff_indicate.setChecked(true);
            //定时开灯开关
            if(isTimerOn && !isTimerOff){
                tv_delay_indicate.setText("定时开灯: ");
                tvDelay.setText(timeOnMinute +" 分钟");
            }else if(isTimerOff && !isTimerOn){
                tv_delay_indicate.setText("定时关灯: ");
                tvDelay.setText(timeOffMinute +" 分钟");
            }
        }else{
            delay_OnOff_indicate.setChecked(false);
            tv_delay_indicate.setText("无定时任务");
            tvDelay.setText(" ");
        }
    }

    /**
     * 更新标题
     */
    private void updateTitle() {
        if(isTimerOn || isTimerOff){
            if(isTimerOn && !isTimerOff){
                tvTitle.setText("定时开: "+timeOnMinute +" 分钟");
                tvTitle.setTextColor(Color.parseColor("#9FD661"));
            }else if(isTimerOff && !isTimerOn){
                tvTitle.setText("定时关: "+timeOffMinute +" 分钟");
                tvTitle.setTextColor(Color.parseColor("#9FD661"));
            }

        }else if(!isTimerOn && !isTimerOff){
            tvTitle.setTextColor(Color.parseColor("#FFFFFF"));
            if(!mDevice.getAlias().isEmpty()){
                tvTitle.setText(mDevice.getAlias());
            }else{
                tvTitle.setText(mDevice.getProductName());
            }
        }
    }

    /**
     * 更新电源开关切换.
     */
    private void updatePowerUI(boolean isPower) {
        //如果电源处于关闭的状态
        if (!isPower) {
            //设置按钮的状态
            btnPower.setSelected(false);
            //显示开灯
            tvPower.setText(getString(R.string.openlight));
            ivmain.setVisibility(View.VISIBLE);
            powerOff();
            //rl_top.setVisibility(View.VISIBLE);
        } else {
            btnPower.setSelected(true);
            tvPower.setText(getString(R.string.closelight));

            /*如果电源关闭：设置色环部分不可操作*/
            ivPowerOff.setVisibility(View.INVISIBLE);
            rl_middle.setVisibility(View.VISIBLE);
            //且
            ivmain.setVisibility(View.INVISIBLE);
        }
    }

    private void initView() {

        //延时开关灯
        delay_OnOff_indicate = findViewById(R.id.delay_OnOff_ID);
        tv_delay_indicate =findViewById(R.id.delaytxt);
        //冷暖灯设置
        btn_White = findViewById(R.id.setWhiteLight_ID);
        btn_Warm = findViewById(R.id.setWarmLight_ID);
        //亮度调节
        sbBrighteness = findViewById(R.id.sbBrighteness);
        //显示RGB颜色
        tv_ColorNum=(TextView) findViewById(R.id.color_num_ID);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivSetting = (ImageView) findViewById(R.id.ivSetting);
        btnPower = (Button) findViewById(R.id.btnPower);
        tvPower = (TextView) findViewById(R.id.tvPower);
        ivmain = (ImageView) findViewById(R.id.ivmain);
        rl_middle = (RelativeLayout) findViewById(R.id.rl_middle);
        ivPowerOff = (ImageView) findViewById(R.id.ivPowerOff);
        //延时预约
        rlDelay = (RelativeLayout) findViewById(R.id.rlDelay);
        tvDelay = (TextView) findViewById(R.id.tvDelay);


        //色彩的进度条
        circularSeekBar = (ColorCircularSeekBar) findViewById(R.id.csbSeekbar2);
        circularSeekBar.postInvalidateDelayed(2000);
        circularSeekBar.setMaxProgress(100);
        circularSeekBar.setProgress(30);
        circularSeekBar.setMProgress(0);
        circularSeekBar.postInvalidateDelayed(100);

        //色环改变监听器
        circularSeekBar.setSeekBarChangeListener(new ColorCircularSeekBar.OnSeekChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChange(ColorCircularSeekBar view, int color) {
                // TODO Auto-generated method stub
                int toColor;
                //先把色环的颜色取出
                mColor = color;
                //把取出的颜色的调整一下亮度,调整为30%（最暗）
                toColor = adjustBritness(30);
                //把改变后的亮度存储
                bringhtness_num = 30;
                //此时，灯的状态应该是打开的，需要刷新电源的状态才行
                //把进度条调整一下
                sbBrighteness.setProgress(bringhtness_num);
                //设置默认亮度,把颜色和亮度发送的云端（发送指令）,以及把电源的状态也更新上去
                cColorAndBrightness(mDevice, toColor,bringhtness_num,true);
                tv_ColorNum.setText("R:" +String.valueOf(color_num_r)+",G:"
                        +String.valueOf(color_num_g)+",B:"
                        +String.valueOf(color_num_b));

            }

        });
        //推动条监听事件
        sbBrighteness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int ToClor; //亮度转换后的RGB值
                //进度条
                bringhtness_num = seekBar.getProgress();
                //调节亮度至指定的进度，返回调整后的亮度值
                ToClor = adjustBritness(bringhtness_num);
                //颜色值和亮度值一起发送到云端
                cColorAndBrightness(mDevice,ToClor,bringhtness_num,true);
                //RGB显示在屏幕上
                tv_ColorNum.setText("R:" +String.valueOf(color_num_r)+",G:"
                        +String.valueOf(color_num_g)+",B:"
                        +String.valueOf(color_num_b));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });

        //电源按钮事件
        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnPowerAction();
                //更新电源状态：从云端获取
                updatePowerUI(isPowerOn);
            }
        });
        //后退按钮监听事件
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //冷灯模式设置按钮监听事件
        btn_White.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWhiteLight();
            }
        });
        btn_Warm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWarmLight();
            }
        });
        //更多按钮监听时间
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //在topbar右边点击加号显示出来的items
                final String[] items = new String[]{"定时开灯", "定时关灯","取消定时"};
                new QMUIDialog.MenuDialogBuilder(ColorSeekBarActivity.this)
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //并没有定时关灯任务时(任务无冲突，但是有衣蛾相同的任务)
                                        if(!isTimerOff && isTimerOn){
                                            Toast.makeText(ColorSeekBarActivity.this, "已经有定开灯任务了哦！" , Toast.LENGTH_SHORT).show();

                                        }else if(!isTimerOff ){
                                            //当前的灯处于打开中状态
                                            if(isTimerOn){

                                                Toast.makeText(ColorSeekBarActivity.this, "当前灯处于打开状态呢！" , Toast.LENGTH_SHORT).show();
                                            }
                                            //定时开灯的时间，单位：分钟
                                            HowLongToOpenDialog();
                                        } else{  //任务冲突
                                            Toast.makeText(ColorSeekBarActivity.this, "！任务冲突！", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 1:
                                        //并没有定时开灯的任务
                                       if(!isTimerOn && isTimerOff){
                                           Toast.makeText(ColorSeekBarActivity.this, "已经有定关灯任务了哦！" , Toast.LENGTH_SHORT).show();
                                       }else if(!isTimerOn){
                                           //定时关灯的时间，单位：分钟
                                           HowLongToCloseDialog();
                                       } else{
                                           Toast.makeText(ColorSeekBarActivity.this, "！任务冲突！", Toast.LENGTH_SHORT).show();
                                       }
                                        break;
                                    case 2:
                                        if(isTimerOn || isTimerOff){
                                            //取消定时
                                            CancleTimer();
                                        }else{
                                            Toast.makeText(ColorSeekBarActivity.this, "当前没有定时任务哦！" , Toast.LENGTH_SHORT).show();
                                        }

                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

    }


    /**
     * 功能：取消定时
     */
    private void CancleTimer() {
        //弹窗询问是否取消定时
        new QMUIDialog.MessageDialogBuilder(ColorSeekBarActivity.this)
                .setTitle("定时器")
                .setMessage("确定要取消定时吗？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        if(delay_OnOff_indicate.isChecked()){

                            cTimerdata(false,false,0,0);

                        }else{
                            Toast.makeText(ColorSeekBarActivity.this, "抱歉！当前无定时任务", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    }
                })
                .show();
    }

    //定时开灯的时间，单位：分钟
    private void HowLongToOpenDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(ColorSeekBarActivity.this);
        builder.setTitle("定时开灯(分钟)")
                .setPlaceholder("请输入定时开灯的时间")
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        String minsToOpen = builder.getEditText().getText().toString().trim();
                        if(!isTimerOff){
                            if (minsToOpen != null && minsToOpen.length() > 0) {
                                if (isInteger(minsToOpen)) {
                                    int mins_num =Integer.valueOf(minsToOpen);
                                    if(mins_num > 0 && mins_num <500){
                                        Toast.makeText(ColorSeekBarActivity.this, mins_num+" 分钟后,将为您开灯", Toast.LENGTH_SHORT).show();
                                        //设置本地变量定时开灯，并且设置开灯分钟数
                                        cTimerdata(true,false,mins_num,0);
                                        dialog.dismiss();

                                    }else{
                                        Toast.makeText(ColorSeekBarActivity.this, "设定超出限时", Toast.LENGTH_SHORT).show();
                                        tvDelay.setText(" ");
                                    }
                                } else {
                                    Toast.makeText(ColorSeekBarActivity.this, "请输入数字", Toast.LENGTH_SHORT).show();
                                    tvDelay.setText(" ");
                                }
                            }else {
                                Toast.makeText(ColorSeekBarActivity.this, "请输入具体时间", Toast.LENGTH_SHORT).show();
                                tvDelay.setText(" ");
                            }
                        }else{
                            Toast.makeText(ColorSeekBarActivity.this, "抱歉，您已经打开定时关灯功能了哦！\n" +
                                    "请先关闭定时器再操作", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }
    //定时关灯的时间，单位：分钟
    private void HowLongToCloseDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(ColorSeekBarActivity.this);
        builder.setTitle("定时关灯(分钟)")
                .setPlaceholder("请输入定时关灯的时间")
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        String minsToClose = builder.getEditText().getText().toString().trim();
                        if(!isTimerOn){
                            if (minsToClose != null && minsToClose.length() > 0) {
                                if (isInteger(minsToClose)) {
                                    int mins_num =Integer.valueOf(minsToClose);
                                    if(mins_num > 0 && mins_num <500){
                                        Toast.makeText(ColorSeekBarActivity.this, minsToClose+" 分钟后,将为您关灯", Toast.LENGTH_SHORT).show();
                                        //判断，定时开和定时关不能同时进行
                                        if(!isTimerOn){
                                            cTimerdata(false,true,0,mins_num);
                                            dialog.dismiss();
                                        }else{
                                            Toast.makeText(ColorSeekBarActivity.this, "抱歉，您已经打开定时开灯功能了哦！\n" +
                                                    "请先关闭定时器再操作", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }

                                    }else{
                                        Toast.makeText(ColorSeekBarActivity.this, "设定超出限时", Toast.LENGTH_SHORT).show();
                                        tvDelay.setText(" ");
                                    }
                                } else {
                                    Toast.makeText(ColorSeekBarActivity.this, "请输入数字", Toast.LENGTH_SHORT).show();
                                    tvDelay.setText(" ");
                                }
                            }else {
                                Toast.makeText(ColorSeekBarActivity.this, "请输入具体时间", Toast.LENGTH_SHORT).show();
                                tvDelay.setText(" ");
                            }
                        }else{
                            Toast.makeText(ColorSeekBarActivity.this, "抱歉，您已经打开定时关灯功能了哦！\n" +
                                    "请先关闭定时器再操作", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    }
                })
                .show();
    }

    /*方法二：推荐，速度最快
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    /*亮度设置*/
    private int adjustBritness(int k){
        float[] hsv = new float[3]; //保存hvs的值
        //取色之后存储到 mColor， RGB - HSV
        Color.colorToHSV(mColor, hsv);
        //通过 进度条改变亮度
        mHue = hsv[0];
        mSat = hsv[1];
        mVal = (float) k / 100;
        if (mVal < 0.35) {
            mVal = 0.35f;
        }
        //HSV -RGB
        return Color.HSVToColor(new float[]{mHue, mSat, mVal});
    }


    /**
     * 设置白光
     */
    @SuppressLint("SetTextI18n")
    private void setWhiteLight(){
        //将此次颜色改变更新到拖动条能更新的位置
        mColor=0x595959;
        //状态存储到本地，以及同步到云端->暖色，亮度30，电源开
        cColorAndBrightness(mDevice,mColor,30,true);

    }

    /**
     * 设置暖光
     */
    @SuppressLint("SetTextI18n")
    private void setWarmLight() {

        //将此次颜色改变更新到拖动条能更新的位置
        mColor=0x593D00;
        //状态存储到本地，以及同步到云端->暖色，亮度30，电源开
        cColorAndBrightness(mDevice,mColor,30,true);
    }

    /**
     * 同步本地，发送到云端的数据：定时开关灯的状态，定时时间
     */
    private void cTimerdata(Boolean TimerOnSta,Boolean TimerOffSta,int OnTime,int OffTime){
        //设置本地变量定时开灯，并且设置开灯分钟数
        isTimerOn =TimerOnSta;
        isTimerOff =TimerOffSta;
        timeOnMinute =OnTime;
        timeOffMinute = OffTime;
        //发送到云端以便同步
        //先发送数值，先同步
        sendCommendToCloud("CountDown_On_value",timeOnMinute);
        sendCommendToCloud("CountDown_Off_value",timeOffMinute);
        sendCommendToCloud("TimerOn_Switch",isTimerOn);
        sendCommendToCloud("TimerOff_Switch",isTimerOff);
        //更新UI
        updateTimerStates();

    }



    /* 同步本地，发送云端的数据：颜色，亮度，电源状态*/
    @SuppressLint("SetTextI18n")
    public void cColorAndBrightness(GizWifiDevice device, int color, int bringhtness, Boolean powerStatus){
        sendMoreCommand("Color_R", Color.red(color),
                        "Color_G", Color.green(color),
                        "Color_B", Color.blue(color),
                        "Brightness",bringhtness_num,
                        "Power_Switch",powerStatus);
        color_num_r = Color.red(color);
        color_num_g = Color.green(color);
        color_num_b = Color.blue(color);
        //RGB显示在屏幕上:更新UI
        tv_ColorNum.setText("R:" +String.valueOf(color_num_r)+",G:"
                +String.valueOf(color_num_g)+",B:"
                +String.valueOf(color_num_b));
        bringhtness_num = bringhtness;
        //更新亮度的UI
        sbBrighteness.setProgress(bringhtness_num);
        isPowerOn = true;
    }
    /*
     * 更新色彩:从云端更新
     */
    private void updateColor(int color) {
        circularSeekBar.setInnerColor(Color.argb(255,color_num_r, color_num_g, color_num_b));
    }


    /*
     * 更新亮度值到UI
     */
    private void updateBrighteness(int bringhtness_num) {

        sbBrighteness.setProgress(bringhtness_num);
    }

    /* 开关*/
    private void btnPowerAction() {
        //对上次的状态进行取反
        isPowerOn =!isPowerOn;
        //发送给云端
        sendCommendToCloud("Power_Switch", isPowerOn);
        /*对于第一次没有任何颜色的漏洞修复*/
        if(isPowerOn && color_num_r==0 && color_num_g ==0 && color_num_b ==0){
            cColorAndBrightness(mDevice,0x595959,30,true);
        }
        /*未打开电源的状态*/
        if (!isPowerOn == true) {

            //中层的imageView的蒙版可见 颜色 #aaaaaa
            ivmain.setVisibility(View.VISIBLE);
            //顶层的整个布局可见
            //rl_top.setVisibility(View.VISIBLE);
            powerOff();


        } else {  //电源打开
            //色环中的imageview消失
            ivPowerOff.setVisibility(View.INVISIBLE);
            //中层布局可见
            rl_middle.setVisibility(View.VISIBLE);
            //整体的蒙版消失
            ivmain.setVisibility(View.INVISIBLE);
        }
    }
    private void powerOff() {
        //获取色环部分的长度和宽度
        Bitmap mBitmap = Bitmap.createBitmap(rl_middle.getWidth(), rl_middle.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        rl_middle.draw(canvas);
        //设置iv可见，也就是设置一个颜色盖上而已
        ivPowerOff.setVisibility(View.VISIBLE);
        //画一个画布，盖上了就不可点击里面的东西了
        ivPowerOff.setImageBitmap(mBitmap);
        //中层的蒙版消失（也就是色环的位置）
        rl_middle.setVisibility(View.INVISIBLE);

    }
    private void upDataPowerAction(){

    }



    //发送四个命令
    public void sendMoreCommand(String key, Object value,
                                String key1, Object value1,
                                String key2, Object value2,
                                String key3, Object value3,
                                String key4, Object value4) {
//		int sn = 5; // 如果App不使用sn，此处可写成 int sn = 0;
        int sn = 0;
        ConcurrentHashMap<String, Object> command = new ConcurrentHashMap<String, Object>();
        command.put(key, value);
        command.put(key1, value1);
        command.put(key2, value2);
        command.put(key3, value3);
        command.put(key4, value4);
        mDevice.write(command, sn);
    }

    @Override
    protected void receiveCloudData(ConcurrentHashMap<String, Object> dataMap) {
        super.receiveCloudData(dataMap);
        getDataFromDateMap(dataMap);
       //执行父类的方法：把更新UI放在这里
        upDateUI();

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

            map = (ConcurrentHashMap<String, Object>) dataMap.get("data");

            for (String key : map.keySet()) {
                // 开关
                if (key.equals("Power_Switch")) {
                    isPowerOn = (Boolean) map.get(key);
                }
                //亮度
                if (key.equals("Brightness")) {
                    bringhtness_num = (Integer) map.get(key);
                }
                //定时关指示
                if (key.equals("TimerOff_Switch")) {
                    isTimerOff = (Boolean) map.get(key);
                }
                //定时开指示
                if (key.equals("TimerOn_Switch")) {
                    isTimerOn = (Boolean) map.get(key);
                }
                //定时关数值
                if (key.equals("CountDown_Off_value")) {
                    timeOffMinute = (Integer) map.get(key);
                }
                //定时开数值
                if (key.equals("CountDown_On_value")) {
                    timeOnMinute = (Integer) map.get(key);
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

            }
        }
    }

}

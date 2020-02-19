package com.yuan_giziwits_andorid.UI.DevicrControl.TimingSocket;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.yuan_giziwits_andorid.Quit.MyApplication;
import com.yuan_giziwits_andorid.R;
import com.yuan_giziwits_andorid.UI.DevicrControl.BaseDeviceControlActivity;
import com.yuan_giziwits_andorid.UI.DevicrControl.MainDeviceControlActivity;
import com.yuan_giziwits_andorid.Utils.TimerUtils;

import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

public class TimingSocketActivity extends BaseDeviceControlActivity implements TimePickerDialog.OnTimeSetListener {


    //变量用于判断是哪一个定时器，定时器是开还是关
    /*
    1. TimerStates = 1 ,定时器1开 。 TimerStates =2 ，定时器1关
    2. TimerStates = 3 ,定时器1开 。 TimerStates =4 ，定时器1关
    3. TimerStates = 5 ,定时器1开 。 TimerStates =6 ，定时器1关
    4. TimerStates = 7 ,定时器1开 。 TimerStates =8 ，定时器1关
    * */
    protected static int TimerStates=0;

    //变量用于取消插座定时
    protected static int WhichTimer = 0;

    /**总共四组数据点：云端对应的数据点*/

    /** 数据点"Power_Switch_1"对应的值**/
    protected static Boolean isPowerOn_1=false;
    /** 数据点"TimerOn_Switch_1"对应的值**/
    protected static Boolean isTimerOn_1=false;
    /** 数据点"TimerOff_Switch_1"对应的值**/
    protected static Boolean isTimerOff_1=false;
    /** 数据点"CountDown_On_value_1"对应的值**/
    protected static int timeOnMinute_1=0;
    /** 数据点"CountDown_Off_value_1"对应的值**/
    protected static int timeOffMinute_1=0;


    /** 数据点"Power_Switch_2"对应的值**/
    protected static Boolean isPowerOn_2=false;
    /** 数据点"TimerOn_Switch_2"对应的值**/
    protected static Boolean isTimerOn_2=false;
    /** 数据点"TimerOff_Switch_2"对应的值**/
    protected static Boolean isTimerOff_2=false;
    /** 数据点"CountDown_On_value_2"对应的值**/
    protected static int timeOnMinute_2=0;
    /** 数据点"CountDown_Off_value_2"对应的值**/
    protected static int timeOffMinute_2=0;

    /** 数据点"Power_Switch_3"对应的值**/
    protected static Boolean isPowerOn_3=false;
    /** 数据点"TimerOn_Switch_3"对应的值**/
    protected static Boolean isTimerOn_3=false;
    /** 数据点"TimerOff_Switch_3"对应的值**/
    protected static Boolean isTimerOff_3=false;
    /** 数据点"CountDown_On_value_3"对应的值**/
    protected static int timeOnMinute_3=0;
    /** 数据点"CountDown_Off_value_3"对应的值**/
    protected static int timeOffMinute_3=0;

    /** 数据点"Power_Switch_4"对应的值**/
    protected static Boolean isPowerOn_4=false;
    /** 数据点"TimerOn_Switch_4"对应的值**/
    protected static Boolean isTimerOn_4=false;
    /** 数据点"TimerOff_Switch_4"对应的值**/
    protected static Boolean isTimerOff_4=false;
    /** 数据点"CountDown_On_value_4"对应的值**/
    protected static int timeOnMinute_4=0;
    /** 数据点"CountDown_Off_value_4"对应的值**/
    protected static int timeOffMinute_4=0;

    //当前时间
    private Calendar nowTimes;

    /*变量的声明*/
    //在本类应用的云端数据点
    ConcurrentHashMap<String, Object> socketDataMap;

    //顶层框
    private QMUITopBar mTopBar;



    //下拉刷新
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //显示同步的tipDialog
    private QMUITipDialog SynTipDialog;
    //点击定时按钮
    private Button btn_Timer1;
    private Button btn_Timer2;
    private Button btn_Timer3;
    private Button btn_Timer4;

   //插座开关按钮
    private CheckBox cb_Timer1;
    private CheckBox cb_Timer2;
    private CheckBox cb_Timer3;
    private CheckBox cb_Timer4;

    //用于查看定时器的状态信息用，只有定时器打开时才显示出来
    private CheckBox cb_timer_states_1;
    private CheckBox cb_timer_states_2;
    private CheckBox cb_timer_states_3;
    private CheckBox cb_timer_states_4;

    //用于显示定时器的信息
    private TextView tv_showTimerMessage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈，退出用
        MyApplication.getInstance().addActivity(this);
        //设置为全屏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_timing_socket);
        //控件初始化
        viewInit();
    }

    private void viewInit() {
        //QUMI的 topBar的设置
        //显示定时器信息
        tv_showTimerMessage =findViewById(R.id.show_TimerStates_ID);
        /*控件实例化*/
        //定时按钮
        btn_Timer1 =(Button) findViewById(R.id.btn_timerSwitch1_ID);
        btn_Timer2 =(Button) findViewById(R.id.btn_timerSwitch2_ID);
        btn_Timer3 =(Button) findViewById(R.id.btn_timerSwitch3_ID);
        btn_Timer4 =(Button) findViewById(R.id.btn_timerSwitch4_ID);
        //定时器按钮的点击事件
        onTimerBtnClickEvent();

        //电源开关按钮
        cb_Timer1 = (CheckBox) findViewById(R.id.cb_SocketPowerSwitch1_ID);
        cb_Timer2 = (CheckBox) findViewById(R.id.cb_SocketPowerSwitch2_ID);
        cb_Timer3 = (CheckBox) findViewById(R.id.cb_SocketPowerSwitch3_ID);
        cb_Timer4 = (CheckBox) findViewById(R.id.cb_SocketPowerSwitch4_ID);
        //插座开关的点击事件
        onPowerSwitchClickEvent();

        //显示定时器状态的按钮
        cb_timer_states_1 = findViewById(R.id.cb_Timerstates1_ID);
        cb_timer_states_2 = findViewById(R.id.cb_Timerstates2_ID);
        cb_timer_states_3 = findViewById(R.id.cb_Timerstates3_ID);
        cb_timer_states_4 = findViewById(R.id.cb_Timerstates4_ID);
        //状态按钮的点击事件
        onTimerStatesClickEvent();



        //下拉刷新
        mSwipeRefreshLayout = findViewById(R.id.SocketSwipeRefreshLayout_ID);

        //设置下拉的颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.white);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_color_theme_1,
                R.color.app_color_theme_2,
                R.color.app_color_theme_3,
                R.color.app_color_theme_4,
                R.color.app_color_theme_5,
                R.color.app_color_theme_6);
        //手动调用通知系统测量
        mSwipeRefreshLayout.measure(0,0);
        //打开页面就是下拉的状态
        //mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //重新获取云端数据，并保存在新的变量之中
                receieCloudDataAgain(socketDataMap);
            }
        });

        /*顶层状态栏设计*/
        //顶层状态栏
        mTopBar = findViewById(R.id.SocketControl_topBar_ID);
        mTopBar.addLeftImageButton(R.mipmap.ic_back,R.id.topBar_right_add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置标题为app名字
        mTopBar.setTitle("定时插座");



    }
    /**
     * 功能：下拉刷新，再次接收云端的数据同步到UI，接着进行一些弹窗的UI显示
     * @param MiandataMap  接受数据后，放置的位置
     */
    private void receieCloudDataAgain(ConcurrentHashMap<String, Object> MiandataMap) {
        //下拉之后同步云端数据
        receiveCloudData(MiandataMap);
        //触发QMUI的刷新控件
        SynTipDialog = new QMUITipDialog.Builder(TimingSocketActivity.this)
                .setTipWord("正在同步...")  //显示内容
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)  //显示类型
                .create();
        SynTipDialog.show();
        //不为空，说明 同步成功
        if(!MiandataMap.isEmpty()){
            //更新一下UI界面
            updatePowerUI();
            //正在刷新标志消失，下拉刷新消失
            SynTipDialog.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            //显示同步成功
            SynTipDialog = new QMUITipDialog.Builder(TimingSocketActivity.this)
                    .setTipWord("同步成功")  //显示内容
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)  //显示类型
                    .create();
            SynTipDialog.show();
            //1.5s后消失
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //消失弹窗
                    SynTipDialog.dismiss();
                }
            },1500);
        }
        else{  //返回数据为空，则同步失败

            //正在刷新标志消失，下拉刷新消失
            SynTipDialog.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            //显示同步成功
            SynTipDialog = new QMUITipDialog.Builder(TimingSocketActivity.this)
                    .setTipWord("同步失败，请稍后重试")  //显示内容
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)  //显示类型
                    .create();
            SynTipDialog.show();
            //1.5s后消失
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //消失弹窗
                    SynTipDialog.dismiss();
                }
            },1500);

        }
    }



    /**
     * 定时器状态显示的点击事件
     */
    private void onTimerStatesClickEvent() {
        cb_timer_states_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_timer_states_1.isChecked()){
                    if(isTimerOn_1 && !isTimerOff_1){
                        tv_showTimerMessage.setText("定时器A状态 \n ---定时开--- \n 剩余:"+ timeOnMinute_1/60+"分钟");
                    }
                    else if(!isTimerOn_1 && isTimerOff_1){
                        tv_showTimerMessage.setText("定时器A状态 \n ----定时关----\n 剩余:"+ timeOffMinute_1/60+"分钟");
                    }
                }else{
                    tv_showTimerMessage.setText(" ");
                }

            }
        });
        cb_timer_states_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_timer_states_2.isChecked()){
                    if(isTimerOn_2 && !isTimerOff_2){
                        tv_showTimerMessage.setText("定时器B状态 \n ---定时开--- \n 剩余:"+ timeOnMinute_2/60+"分钟");
                    }
                    else if(!isTimerOn_2 && isTimerOff_2){
                        tv_showTimerMessage.setText("定时器B状态 \n ----定时关----\n 剩余:"+ timeOffMinute_2/60+"分钟");
                    }
                }else{
                    tv_showTimerMessage.setText(" ");
                }

            }
        });
        cb_timer_states_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_timer_states_3.isChecked()){
                    if(isTimerOn_3 && !isTimerOff_3){
                        tv_showTimerMessage.setText("定时器C状态 \n ---定时开--- \n 剩余:"+ timeOnMinute_3/60+"分钟");
                    }
                    else if(!isTimerOn_3 && isTimerOff_3){
                        tv_showTimerMessage.setText("定时器C状态 \n ----定时关----\n 剩余:"+ timeOffMinute_3/60+"分钟");
                    }
                }else{
                    tv_showTimerMessage.setText(" ");
                }

            }
        });
        cb_timer_states_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_timer_states_4.isChecked()){
                    if(isTimerOn_4 && !isTimerOff_4){
                        tv_showTimerMessage.setText("定时器D状态 \n ---定时开--- \n 剩余:"+ timeOnMinute_4/60+"分钟");
                    }
                    else if(!isTimerOn_4 && isTimerOff_4){
                        tv_showTimerMessage.setText("定时器D状态 \n ----定时关----\n 剩余:"+ timeOffMinute_4/60+"分钟");
                    }
                }else{
                    tv_showTimerMessage.setText(" ");
                }

            }
        });
    }


    /**
     * 插座开关的点击事件
     */
    private void onPowerSwitchClickEvent() {
        //第一个插座开关的按钮
        cb_Timer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_Timer1.isChecked()){
                    //如果此时定时开功能已经开启，但是还是要打开电源是会产生矛盾的，提醒用户
                    if(isTimerOn_1){
                        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                                .setTitle("指令矛盾")
                                .setMessage("您已经有一个计划打开插座任务 \n 确定现在打开插座吗？\n" +
                                        "(确认将会取消定时任务)")
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        isPowerOn_1 = false;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_1",false);
                                        //更新UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        //取消定时开任务
                                        CancleTimer(1);
                                        isPowerOn_1=true;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_1",true);
                                        //防止本地更新错误，再次更新电源的UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }else{  //不会产生矛盾
                        //直接打开
                        isPowerOn_1=true;
                        //更新到云端
                        sendCommendToCloud("Power_Switch_1",true);
                        //防止本地更新错误，再次更新电源的UI
                        updatePowerUI();
                    }

                }else{
                    //定时关的任务已经有，但是还是要关闭电源，产生矛盾
                    if(isTimerOff_1) {
                        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                                .setTitle("指令矛盾")
                                .setMessage("您已经有一个计划关闭插座任务 \n 确定现在关闭插座吗？\n" +
                                        "(确认将会取消定时任务)")
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        isPowerOn_1 = true;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_1",true);
                                        //更新UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {

                                        //取消定时开任务
                                        CancleTimer(1);
                                        isPowerOn_1 = false;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_1",false);
                                        //防止本地更新错误，再次更新电源的UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }else{
                        isPowerOn_1 =false;
                        //更新到云端
                        sendCommendToCloud("Power_Switch_1",false);
                        //防止本地更新错误，再次更新电源的UI
                        updatePowerUI();
                    }
                }
            }
        });
        //第2个插座开关的按钮
        cb_Timer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_Timer2.isChecked()){
                    //如果此时定时开功能已经开启，但是还是要打开电源是会产生矛盾的，提醒用户
                    if(isTimerOn_2){
                        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                                .setTitle("指令矛盾")
                                .setMessage("您已经有一个计划打开插座任务 \n 确定现在打开插座吗？\n" +
                                        "(确认将会取消定时任务)")
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        isPowerOn_2 = false;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_2",false);
                                        //更新UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        //取消定时开任务
                                        CancleTimer(2);
                                        isPowerOn_2=true;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_2",true);
                                        //防止本地更新错误，再次更新电源的UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }else{  //不会产生矛盾
                        //直接打开
                        isPowerOn_2=true;
                        //更新到云端
                        sendCommendToCloud("Power_Switch_2",true);
                        //防止本地更新错误，再次更新电源的UI
                        updatePowerUI();
                    }

                }else{
                    //定时关的任务已经有，但是还是要关闭电源，产生矛盾
                    if(isTimerOff_2) {
                        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                                .setTitle("指令矛盾")
                                .setMessage("您已经有一个计划关闭插座任务 \n 确定现在关闭插座吗？\n" +
                                        "(确认将会取消定时任务)")
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        isPowerOn_2 = true;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_1",true);
                                        //更新UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {

                                        //取消定时开任务
                                        CancleTimer(2);
                                        isPowerOn_2 = false;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_2",false);
                                        //防止本地更新错误，再次更新电源的UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }else{
                        isPowerOn_2 =false;
                        //更新到云端
                        sendCommendToCloud("Power_Switch_2",false);
                        //防止本地更新错误，再次更新电源的UI
                        updatePowerUI();
                    }
                }
            }
        });
        //第3个插座开关的按钮
        cb_Timer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_Timer3.isChecked()){
                    //如果此时定时开功能已经开启，但是还是要打开电源是会产生矛盾的，提醒用户
                    if(isTimerOn_3){
                        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                                .setTitle("指令矛盾")
                                .setMessage("您已经有一个计划打开插座任务 \n 确定现在打开插座吗？\n" +
                                        "(确认将会取消定时任务)")
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        isPowerOn_3 = false;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_3",false);
                                        //更新UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        //取消定时开任务
                                        CancleTimer(3);
                                        isPowerOn_3=true;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_3",true);
                                        //防止本地更新错误，再次更新电源的UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }else{  //不会产生矛盾
                        //直接打开
                        isPowerOn_3=true;
                        //更新到云端
                        sendCommendToCloud("Power_Switch_3",true);
                        //防止本地更新错误，再次更新电源的UI
                        updatePowerUI();
                    }

                }else{
                    //定时关的任务已经有，但是还是要关闭电源，产生矛盾
                    if(isTimerOff_3) {
                        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                                .setTitle("指令矛盾")
                                .setMessage("您已经有一个计划关闭插座任务 \n 确定现在关闭插座吗？\n" +
                                        "(确认将会取消定时任务)")
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        isPowerOn_3 = true;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_3",true);
                                        //更新UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {

                                        //取消定时开任务
                                        CancleTimer(3);
                                        isPowerOn_3 = false;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_3",false);
                                        //防止本地更新错误，再次更新电源的UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }else{
                        isPowerOn_3 =false;
                        //更新到云端
                        sendCommendToCloud("Power_Switch_3",false);
                        //防止本地更新错误，再次更新电源的UI
                        updatePowerUI();
                    }
                }
            }
        });
        //第4个插座开关的按钮
        cb_Timer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_Timer4.isChecked()){
                    //如果此时定时开功能已经开启，但是还是要打开电源是会产生矛盾的，提醒用户
                    if(isTimerOn_4){
                        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                                .setTitle("指令矛盾")
                                .setMessage("您已经有一个计划打开插座任务 \n 确定现在打开插座吗？\n" +
                                        "(确认将会取消定时任务)")
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        isPowerOn_4 = false;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_4",false);
                                        //更新UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        //取消定时开任务
                                        CancleTimer(4);
                                        isPowerOn_4=true;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_4",true);
                                        //防止本地更新错误，再次更新电源的UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }else{  //不会产生矛盾
                        //直接打开
                        isPowerOn_4=true;
                        //更新到云端
                        sendCommendToCloud("Power_Switch_4",true);
                        //防止本地更新错误，再次更新电源的UI
                        updatePowerUI();
                    }

                }else{
                    //定时关的任务已经有，但是还是要关闭电源，产生矛盾
                    if(isTimerOff_4) {
                        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                                .setTitle("指令矛盾")
                                .setMessage("您已经有一个计划关闭插座任务 \n 确定现在关闭插座吗？\n" +
                                        "(确认将会取消定时任务)")
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        isPowerOn_4 = true;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_4",true);
                                        //更新UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {

                                        //取消定时开任务
                                        CancleTimer(4);
                                        isPowerOn_4 = false;
                                        //更新到云端
                                        sendCommendToCloud("Power_Switch_4",false);
                                        //防止本地更新错误，再次更新电源的UI
                                        updatePowerUI();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }else{
                        isPowerOn_4 =false;
                        //更新到云端
                        sendCommendToCloud("Power_Switch_4",false);
                        //防止本地更新错误，再次更新电源的UI
                        updatePowerUI();
                    }
                }
            }
        });
    }

    /**
     * 定时器按钮的点击事件
     */
    private void onTimerBtnClickEvent() {
        //第一个插座的定时器
        btn_Timer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择是定时开灯或者是定时关灯
                //在topbar右边点击加号显示出来的items
                final String[] items = new String[]{"定时打开插座", "定时关闭插座","取消插座定时"};
                new QMUIDialog.MenuDialogBuilder(TimingSocketActivity.this)
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //并没有定时打开任务时(任务无冲突，但是有相同的任务)
                                        if(isTimerOn_1 && !isTimerOff_1){
                                            Toast.makeText(TimingSocketActivity.this, "已经有定时打开插座A任务了哦！" , Toast.LENGTH_SHORT).show();

                                        }
                                        //没有定时关任务（任务不冲突）
                                        else if(!isTimerOff_1 ){
                                            //但是当前的灯就是打开的状态，询问是否关闭当前的插座
                                            if(isPowerOn_1){
                                                //是否需要关闭当前电源
                                                isNeedToClosePower1();
                                                //在如果确认了，那么就执行和else一样的操作，否则不打开定时器

                                            }
                                            else{  //当前插座没有打开
                                                //弹出时间选择的对话框,选择完成之后会有回调函数，在回调中去判断
                                                //是定时开还是定时关状态
                                                TimerStates = 1;
                                                showDiaogTimesPicker_open();
                                            }
                                        }

                                        else{  //任务冲突
                                            Toast.makeText(TimingSocketActivity.this, "！任务冲突！", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                        /**定时关*/
                                    case 1:
                                        //已经有定时开任务了（不冲突，但是有相同任务）
                                        if(!isTimerOn_1 && isTimerOff_1){
                                            Toast.makeText(TimingSocketActivity.this, "已经有定关灯任务了哦！" , Toast.LENGTH_SHORT).show();
                                        }
                                        //当前没有定时开任务（不冲突）
                                        else if(!isTimerOn_1){
                                            //但是当前的灯就是关闭的状态，询问是否先打开插座的电源
                                            if(!isPowerOn_1){
                                                //是否需要关闭当前电源
                                                isNeedToOpenPower1();
                                                //在如果确认了，那么就执行和else一样的操作，否则不打开定时器

                                            }
                                            else{  //当前插座没有打开
                                                //弹出时间选择的对话框,选择完成之后会有回调函数，在回调中去判断
                                                //是定时开还是定时关状态
                                                TimerStates = 2;
                                                showDiaogTimesPicker_close();
                                            }
                                        } else{
                                            Toast.makeText(TimingSocketActivity.this, "！任务冲突！", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 2:
                                        if(isTimerOn_1 || isTimerOff_1){
                                            //取消定时
                                            WhichTimer = 1;
                                            CancleTimer(WhichTimer);
                                            TimerStates = 0;
                                            btn_Timer1.setBackground(getResources().getDrawable(R.drawable.timing_open));

                                        }else{
                                            Toast.makeText(TimingSocketActivity.this, "当前没有定时任务哦！" , Toast.LENGTH_SHORT).show();
                                        }

                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).show();

            }
        });
        //第2个插座的定时器
        btn_Timer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择是定时开灯或者是定时关灯
                //在topbar右边点击加号显示出来的items
                final String[] items = new String[]{"定时打开插座", "定时关闭插座","取消插座定时"};
                new QMUIDialog.MenuDialogBuilder(TimingSocketActivity.this)
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //并没有定时打开任务时(任务无冲突，但是有相同的任务)
                                        if(isTimerOn_2 && !isTimerOff_2){
                                            Toast.makeText(TimingSocketActivity.this, "已经有定时打开插座B任务了哦！" , Toast.LENGTH_SHORT).show();

                                        }
                                        //没有定时关任务（任务不冲突）
                                        else if(!isTimerOff_2 ){
                                            //但是当前的灯就是打开的状态，询问是否关闭当前的插座
                                            if(isPowerOn_2){
                                                //是否需要关闭当前电源
                                                isNeedToClosePower2();
                                                //在如果确认了，那么就执行和else一样的操作，否则不打开定时器

                                            }
                                            else{  //当前插座没有打开
                                                //弹出时间选择的对话框,选择完成之后会有回调函数，在回调中去判断
                                                //是定时开还是定时关状态
                                                TimerStates = 3;
                                                showDiaogTimesPicker_open();
                                            }
                                        }

                                        else{  //任务冲突
                                            Toast.makeText(TimingSocketActivity.this, "！任务冲突！", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    /**定时关*/
                                    case 1:
                                        //已经有定时开任务了（不冲突，但是有相同任务）
                                        if(!isTimerOn_2 && isTimerOff_2){
                                            Toast.makeText(TimingSocketActivity.this, "已经有定关灯任务了哦！" , Toast.LENGTH_SHORT).show();
                                        }
                                        //当前没有定时开任务（不冲突）
                                        else if(!isTimerOn_2){
                                            //但是当前的灯就是关闭的状态，询问是否先打开插座的电源
                                            if(!isPowerOn_2){
                                                //是否需要关闭当前电源
                                                isNeedToOpenPower2();
                                                //在如果确认了，那么就执行和else一样的操作，否则不打开定时器

                                            }
                                            else{  //当前插座没有打开
                                                //弹出时间选择的对话框,选择完成之后会有回调函数，在回调中去判断
                                                //是定时开还是定时关状态
                                                TimerStates = 4;
                                                showDiaogTimesPicker_close();
                                            }
                                        } else{
                                            Toast.makeText(TimingSocketActivity.this, "！任务冲突！", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 2:
                                        if(isTimerOn_2 || isTimerOff_2){
                                            //取消定时
                                            WhichTimer = 2;
                                            CancleTimer(WhichTimer);
                                            TimerStates = 0;
                                            btn_Timer2.setBackground(getResources().getDrawable(R.drawable.timing_open));

                                        }else{
                                            Toast.makeText(TimingSocketActivity.this, "当前没有定时任务哦！" , Toast.LENGTH_SHORT).show();
                                        }

                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).show();

            }
        });
        //第3个插座的定时器
        btn_Timer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择是定时开灯或者是定时关灯
                //在topbar右边点击加号显示出来的items
                final String[] items = new String[]{"定时打开插座", "定时关闭插座","取消插座定时"};
                new QMUIDialog.MenuDialogBuilder(TimingSocketActivity.this)
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //并没有定时打开任务时(任务无冲突，但是有相同的任务)
                                        if(isTimerOn_3 && !isTimerOff_3){
                                            Toast.makeText(TimingSocketActivity.this, "已经有定时打开插座A任务了哦！" , Toast.LENGTH_SHORT).show();

                                        }
                                        //没有定时关任务（任务不冲突）
                                        else if(!isTimerOff_3 ){
                                            //但是当前的灯就是打开的状态，询问是否关闭当前的插座
                                            if(isPowerOn_3){
                                                //是否需要关闭当前电源
                                                isNeedToClosePower3();
                                                //在如果确认了，那么就执行和else一样的操作，否则不打开定时器

                                            }
                                            else{  //当前插座没有打开
                                                //弹出时间选择的对话框,选择完成之后会有回调函数，在回调中去判断
                                                //是定时开还是定时关状态
                                                TimerStates = 5;
                                                showDiaogTimesPicker_open();
                                            }
                                        }

                                        else{  //任务冲突
                                            Toast.makeText(TimingSocketActivity.this, "！任务冲突！", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    /**定时关*/
                                    case 1:
                                        //已经有定时开任务了（不冲突，但是有相同任务）
                                        if(!isTimerOn_3 && isTimerOff_3){
                                            Toast.makeText(TimingSocketActivity.this, "已经有定关灯任务了哦！" , Toast.LENGTH_SHORT).show();
                                        }
                                        //当前没有定时开任务（不冲突）
                                        else if(!isTimerOn_3){
                                            //但是当前的灯就是关闭的状态，询问是否先打开插座的电源
                                            if(!isPowerOn_3){
                                                //是否需要关闭当前电源
                                                isNeedToOpenPower3();
                                                //在如果确认了，那么就执行和else一样的操作，否则不打开定时器

                                            }
                                            else{  //当前插座没有打开
                                                //弹出时间选择的对话框,选择完成之后会有回调函数，在回调中去判断
                                                //是定时开还是定时关状态
                                                TimerStates = 6;
                                                showDiaogTimesPicker_close();
                                            }
                                        } else{
                                            Toast.makeText(TimingSocketActivity.this, "！任务冲突！", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 2:
                                        if(isTimerOn_3 || isTimerOff_3){
                                            //取消定时
                                            WhichTimer = 3;
                                            CancleTimer(WhichTimer);
                                            TimerStates = 0;
                                            btn_Timer3.setBackground(getResources().getDrawable(R.drawable.timing_open));

                                        }else{
                                            Toast.makeText(TimingSocketActivity.this, "当前没有定时任务哦！" , Toast.LENGTH_SHORT).show();
                                        }

                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).show();

            }
        });
        //第4个插座的定时器
        btn_Timer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择是定时开灯或者是定时关灯
                //在topbar右边点击加号显示出来的items
                final String[] items = new String[]{"定时打开插座", "定时关闭插座","取消插座定时"};
                new QMUIDialog.MenuDialogBuilder(TimingSocketActivity.this)
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //并没有定时打开任务时(任务无冲突，但是有相同的任务)
                                        if(isTimerOn_4 && !isTimerOff_4){
                                            Toast.makeText(TimingSocketActivity.this, "已经有定时打开插座A任务了哦！" , Toast.LENGTH_SHORT).show();

                                        }
                                        //没有定时关任务（任务不冲突）
                                        else if(!isTimerOff_4 ){
                                            //但是当前的灯就是打开的状态，询问是否关闭当前的插座
                                            if(isPowerOn_4){
                                                //是否需要关闭当前电源
                                                isNeedToClosePower4();
                                                //在如果确认了，那么就执行和else一样的操作，否则不打开定时器

                                            }
                                            else{  //当前插座没有打开
                                                //弹出时间选择的对话框,选择完成之后会有回调函数，在回调中去判断
                                                //是定时开还是定时关状态
                                                TimerStates = 7;
                                                showDiaogTimesPicker_open();
                                            }
                                        }

                                        else{  //任务冲突
                                            Toast.makeText(TimingSocketActivity.this, "！任务冲突！", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    /**定时关*/
                                    case 1:
                                        //已经有定时开任务了（不冲突，但是有相同任务）
                                        if(!isTimerOn_4 && isTimerOff_4){
                                            Toast.makeText(TimingSocketActivity.this, "已经有定关灯任务了哦！" , Toast.LENGTH_SHORT).show();
                                        }
                                        //当前没有定时开任务（不冲突）
                                        else if(!isTimerOn_4){
                                            //但是当前的灯就是关闭的状态，询问是否先打开插座的电源
                                            if(!isPowerOn_4){
                                                //是否需要关闭当前电源
                                                isNeedToOpenPower4();
                                                //在如果确认了，那么就执行和else一样的操作，否则不打开定时器

                                            }
                                            else{  //当前插座没有打开
                                                //弹出时间选择的对话框,选择完成之后会有回调函数，在回调中去判断
                                                //是定时开还是定时关状态
                                                TimerStates = 8;
                                                showDiaogTimesPicker_close();
                                            }
                                        } else{
                                            Toast.makeText(TimingSocketActivity.this, "！任务冲突！", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 2:
                                        if(isTimerOn_4|| isTimerOff_4){
                                            //取消定时
                                            WhichTimer = 4;
                                            CancleTimer(WhichTimer);
                                            TimerStates = 0;
                                            btn_Timer4.setBackground(getResources().getDrawable(R.drawable.timing_open));

                                        }else{
                                            Toast.makeText(TimingSocketActivity.this, "当前没有定时任务哦！" , Toast.LENGTH_SHORT).show();
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
     * 功能：取消插座的定时功能
     */
    private void CancleTimer(int whichone) {
        //取消定时器，同时把定时器显示的状态清除
        tv_showTimerMessage.setText(" ");
        switch(whichone){
            case 1:
                //取消插座A的定时器
                //跟新本地和云端的数据
                cTimerdata1(false,false,0,0);
                //更新UI
                upDateUI();
                break;
            case 2:
                //取消插座A的定时器
                //跟新本地和云端的数据
                cTimerdata2(false,false,0,0);
                //更新UI
                upDateUI();
                break;
            case 3:
                //取消插座A的定时器
                //跟新本地和云端的数据
                cTimerdata3(false,false,0,0);
                //更新UI
                upDateUI();
                break;
            case 4:
                //取消插座A的定时器
                //跟新本地和云端的数据
                cTimerdata4(false,false,0,0);
                //更新UI
                upDateUI();
                break;
            default:
                break;
        }
    }

    /**
     * 询问是否需要打开插座A
     */
    private void isNeedToOpenPower1() {
        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                .setTitle("确认打开插座")
                .setMessage("当前插座关闭,是否打开？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(TimingSocketActivity.this,
                                "抱歉,请先打开插座后操作！",
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //关闭灯
                        isPowerOn_1 = true;
                        sendCommendToCloud("Power_Switch_1", true);
                        //更新电源的UI
                        updatePowerUI();
                        //时间选择器
                        TimerStates = 2;
                        showDiaogTimesPicker_close();
                        dialog.dismiss();
                    }
                })
                .show();
    }
    /**
     * 询问是否需要关闭插座A
     */
    private void isNeedToClosePower1() {
        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                .setTitle("确认关闭插座")
                .setMessage("当前插座打开,是否关闭？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(TimingSocketActivity.this,
                                "抱歉,请先关闭插座后操作！",
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //关闭灯
                        isPowerOn_1 = false;
                        sendCommendToCloud("Power_Switch_1", false);
                        //更新电源的UI
                        updatePowerUI();
                        //时间选择器
                        TimerStates = 1;
                        showDiaogTimesPicker_open();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 询问是否需要打开插座B
     */
    private void isNeedToOpenPower2() {
        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                .setTitle("确认打开插座")
                .setMessage("当前插座关闭,是否打开？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(TimingSocketActivity.this,
                                "抱歉,请先打开插座后操作！",
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //关闭灯
                        isPowerOn_2 = true;
                        sendCommendToCloud("Power_Switch_2", true);
                        //更新电源的UI
                        updatePowerUI();
                        //时间选择器
                        TimerStates = 4;
                        showDiaogTimesPicker_close();
                        dialog.dismiss();
                    }
                })
                .show();
    }
    /**
     * 询问是否需要关闭插座B
     */
    private void isNeedToClosePower2() {
        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                .setTitle("确认关闭插座")
                .setMessage("当前插座打开,是否关闭？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(TimingSocketActivity.this,
                                "抱歉,请先关闭插座后操作！",
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //关闭灯
                        isPowerOn_2 = false;
                        sendCommendToCloud("Power_Switch_2", false);
                        //更新电源的UI
                        updatePowerUI();
                        //时间选择器
                        TimerStates = 3;
                        showDiaogTimesPicker_open();
                        dialog.dismiss();
                    }
                })
                .show();
    }
    /**
     * 询问是否需要打开插座C
     */
    private void isNeedToOpenPower3() {
        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                .setTitle("确认打开插座")
                .setMessage("当前插座关闭,是否打开？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(TimingSocketActivity.this,
                                "抱歉,请先打开插座后操作！",
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //关闭灯
                        isPowerOn_3 = true;
                        sendCommendToCloud("Power_Switch_3", true);
                        //更新电源的UI
                        updatePowerUI();
                        //时间选择器
                        TimerStates = 6;
                        showDiaogTimesPicker_close();
                        dialog.dismiss();
                    }
                })
                .show();
    }
    /**
     * 询问是否需要关闭插座D
     */
    private void isNeedToClosePower3() {
        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                .setTitle("确认关闭插座")
                .setMessage("当前插座打开,是否关闭？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(TimingSocketActivity.this,
                                "抱歉,请先关闭插座后操作！",
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //关闭灯
                        isPowerOn_3 = false;
                        sendCommendToCloud("Power_Switch_3", false);
                        //更新电源的UI
                        updatePowerUI();
                        //时间选择器
                        TimerStates = 5;
                        showDiaogTimesPicker_open();
                        dialog.dismiss();
                    }
                })
                .show();
    }
    /**
     * 询问是否需要打开插座D
     */
    private void isNeedToOpenPower4() {
        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                .setTitle("确认打开插座")
                .setMessage("当前插座关闭,是否打开？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(TimingSocketActivity.this,
                                "抱歉,请先打开插座后操作！",
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //关闭灯
                        isPowerOn_4 = true;
                        sendCommendToCloud("Power_Switch_4", true);
                        //更新电源的UI
                        updatePowerUI();
                        //时间选择器
                        TimerStates = 8;
                        showDiaogTimesPicker_close();
                        dialog.dismiss();
                    }
                })
                .show();
    }
    /**
     * 询问是否需要关闭插座C
     */
    private void isNeedToClosePower4() {
        new QMUIDialog.MessageDialogBuilder(TimingSocketActivity.this)
                .setTitle("确认关闭插座")
                .setMessage("当前插座打开,是否关闭？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(TimingSocketActivity.this,
                                "抱歉,请先关闭插座后操作！",
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //关闭灯
                        isPowerOn_4 = false;
                        sendCommendToCloud("Power_Switch_4", false);
                        //更新电源的UI
                        updatePowerUI();
                        //时间选择器
                        TimerStates = 7;
                        showDiaogTimesPicker_open();
                        dialog.dismiss();
                    }
                })
                .show();
    }


    /**
     * description：按定时按钮弹出时钟选择器的对话框
     */
    private void showDiaogTimesPicker_open() {
        nowTimes = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                TimingSocketActivity.this,
                nowTimes.get(Calendar.HOUR_OF_DAY),
                nowTimes.get(Calendar.MINUTE),
                true
        );
        tpd.setTitle("选择一个时间点打开插座");
        tpd.show(getFragmentManager(), "what");
        //设置显示状态的按钮可见
        tv_showTimerMessage.setVisibility(View.VISIBLE);
    }
    /**
     * description：按定时按钮弹出时钟选择器的对话框
     */
    private void showDiaogTimesPicker_close() {
        //得到本地时间
        nowTimes = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                TimingSocketActivity.this,
                nowTimes.get(Calendar.HOUR_OF_DAY),
                nowTimes.get(Calendar.MINUTE),
                true
        );
        tpd.setTitle("选择一个时间点关闭插座");
        tpd.show(getFragmentManager(), "what");
        //显示状态的按钮可见
        //cb_timer_states_1.setVisibility(View.VISIBLE);
    }


    /**
     * 功能：时间选择后的回调函数
     * @param view
     * @param hourOfDay
     * @param minute
     * @param second
     */
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        //小时数值
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        //分钟数值
        String minuteString = minute < 10 ? "0" + minute : "" + minute;

        int times = Integer.parseInt(hourString) * 100 + Integer.parseInt(minuteString);
        //拿到当前到用户想要的时间差,转换成秒数
        int sendTimes = TimerUtils.creatTimers(times + "");

       // Toast.makeText(TimingSocketActivity.this,String.valueOf(sendTimes)+"Timerstates:"+TimerStates,Toast.LENGTH_SHORT).show();
        switch (TimerStates){
            case 1:
                cTimerdata1(true,false,sendTimes,0);
                break;
            case 2:
                cTimerdata1(false, true, 0, sendTimes);
                break;
            case 3:
                cTimerdata2(true, false, sendTimes, 0);
                break;
            case 4:
                cTimerdata2(false, true, 0, sendTimes);
                break;
            case 5:
                cTimerdata3(true, false, sendTimes, 0);
                break;
            case 6:
                cTimerdata3(false, true, 0, sendTimes);
                break;
            case 7:
                cTimerdata4(true, false, sendTimes, 0);
                break;
            case 8:
                cTimerdata4(false, true, 0, sendTimes);
                break;
            default:
                break;
        }
    }


    /**
     * 功能：进去界面更新UI
     */
    private void upDateUI() {
        //更新电源状态
        updatePowerUI();
        //更新定时器状态
        updateTimerStates();
    }

    /**
     * 更新定时器的UI状态
     */
    private void updateTimerStates() {
        //只要有其中定时打开:定时器A
        if(isTimerOn_1 || isTimerOff_1){
            //改变定时器的颜色为 绿色
            btn_Timer1.setBackground(getResources().getDrawable(R.drawable.timing_open));
            cb_timer_states_1.setVisibility(View.VISIBLE);
            //定时开灯开关
            if(isTimerOn_1 && !isTimerOff_1){

            }
            //定时关
            else if(isTimerOff_1 && !isTimerOn_1){

            }
        }else{
            //把定时器换回原来的颜色 黄色
            btn_Timer1.setBackground(getResources().getDrawable(R.drawable.timing));
            cb_timer_states_1.setVisibility(View.INVISIBLE);
            tv_showTimerMessage.setText(" ");
        }
        //只要有其中定时打开:定时器B
        if(isTimerOn_2 || isTimerOff_2){
            //改变定时器的颜色为 绿色
            btn_Timer2.setBackground(getResources().getDrawable(R.drawable.timing_open));
            cb_timer_states_2.setVisibility(View.VISIBLE);
            //定时开灯开关
            if(isTimerOn_2 && !isTimerOff_2){

            }
            //定时关
            else if(isTimerOff_2 && !isTimerOn_2){

            }
        }else{
            //把定时器换回原来的颜色 黄色
            btn_Timer2.setBackground(getResources().getDrawable(R.drawable.timing));
            cb_timer_states_2.setVisibility(View.INVISIBLE);
            tv_showTimerMessage.setText(" ");
        }
        //只要有其中定时打开:定时器C
        if(isTimerOn_3 || isTimerOff_3){
            //改变定时器的颜色为 绿色
            btn_Timer3.setBackground(getResources().getDrawable(R.drawable.timing_open));
            cb_timer_states_3.setVisibility(View.VISIBLE);
            //定时开灯开关
            if(isTimerOn_3 && !isTimerOff_3){

            }
            //定时关
            else if(isTimerOff_3 && !isTimerOn_3){

            }
        }else{
            //把定时器换回原来的颜色 黄色
            btn_Timer3.setBackground(getResources().getDrawable(R.drawable.timing));
            cb_timer_states_3.setVisibility(View.INVISIBLE);
            tv_showTimerMessage.setText(" ");
        }
        //只要有其中定时打开:定时器D
        if(isTimerOn_4 || isTimerOff_4){
            //改变定时器的颜色为 绿色
            btn_Timer4.setBackground(getResources().getDrawable(R.drawable.timing_open));
            cb_timer_states_4.setVisibility(View.VISIBLE);
            //定时开灯开关
            if(isTimerOn_4 && !isTimerOff_4){

            }
            //定时关
            else if(isTimerOff_4 && !isTimerOn_4){

            }
        }else{
            //把定时器换回原来的颜色 黄色
            btn_Timer4.setBackground(getResources().getDrawable(R.drawable.timing));
            cb_timer_states_4.setVisibility(View.INVISIBLE);
            tv_showTimerMessage.setText(" ");
        }


    }
    /**
     * 更新电源开关切换.
     */
    private void updatePowerUI() {
        //如果电源处于关闭的状态
        if (!isPowerOn_1) {
            //设置按钮的状态
            cb_Timer1.setChecked(false);
        } else {
            cb_Timer1.setChecked(true);
        }
        if (!isPowerOn_2) {
            //设置按钮的状态
            cb_Timer2.setChecked(false);

        } else {
            cb_Timer2.setChecked(true);
        }
        if (!isPowerOn_3) {
            //设置按钮的状态
            cb_Timer3.setChecked(false);

        } else {
            cb_Timer3.setChecked(true);
        }
        if (!isPowerOn_4) {
            //设置按钮的状态
            cb_Timer4.setChecked(false);
        } else {
            cb_Timer4.setChecked(true);
        }
    }
    /**
     * 同步本地，发送到云端的数据(定时器A)：定时开关灯的状态，定时时间
     */
    private void cTimerdata1(Boolean TimerOnSta,Boolean TimerOffSta,int OnTime,int OffTime){
        //设置本地变量定时开灯，并且设置开灯分钟数
        isTimerOn_1 =TimerOnSta;
        isTimerOff_1 =TimerOffSta;
        timeOnMinute_1 =OnTime;
        timeOffMinute_1 = OffTime;
        //发送到云端以便同步
        //先发送数值，先同步
        sendCommendToCloud("CountDown_On_value_1",timeOnMinute_1);
        sendCommendToCloud("CountDown_Off_value_1",timeOffMinute_1);
        sendCommendToCloud("TimerOn_Switch_1",isTimerOn_1);
        sendCommendToCloud("TimerOff_Switch_1",isTimerOff_1);
        //更新UI
        updateTimerStates();

    }
    /**
     * 同步本地，发送到云端的数据(定时器B)：定时开关灯的状态，定时时间
     */
    private void cTimerdata2(Boolean TimerOnSta,Boolean TimerOffSta,int OnTime,int OffTime){
        //设置本地变量定时开灯，并且设置开灯分钟数
        isTimerOn_2 =TimerOnSta;
        isTimerOff_2 =TimerOffSta;
        timeOnMinute_2 =OnTime;
        timeOffMinute_2 = OffTime;
        //发送到云端以便同步
        //先发送数值，先同步CountDown_On_value_2
        sendCommendToCloud("CountDown_On_value_2",timeOnMinute_2);
        sendCommendToCloud("CountDown_Off_value_2",timeOffMinute_2);
        sendCommendToCloud("TimerOn_Switch_2",isTimerOn_2);
        sendCommendToCloud("TimerOff_Switch_2",isTimerOff_2);
        //更新UI
        updateTimerStates();

    }
    /**
     * 同步本地，发送到云端的数据(定时器C)：定时开关灯的状态，定时时间
     */
    private void cTimerdata3(Boolean TimerOnSta,Boolean TimerOffSta,int OnTime,int OffTime){
        //设置本地变量定时开灯，并且设置开灯分钟数
        isTimerOn_3 =TimerOnSta;
        isTimerOff_3 =TimerOffSta;
        timeOnMinute_3 =OnTime;
        timeOffMinute_3 = OffTime;
        //发送到云端以便同步
        //先发送数值，先同步
        sendCommendToCloud("CountDown_On_value_3",timeOnMinute_3);
        sendCommendToCloud("CountDown_Off_value_3",timeOffMinute_3);
        sendCommendToCloud("TimerOn_Switch_3",isTimerOn_3);
        sendCommendToCloud("TimerOff_Switch_3",isTimerOff_3);
        //更新UI
        updateTimerStates();

    }
    /**
     * 同步本地，发送到云端的数据(定时器D)：定时开关灯的状态，定时时间
     */
    private void cTimerdata4(Boolean TimerOnSta,Boolean TimerOffSta,int OnTime,int OffTime){
        //设置本地变量定时开灯，并且设置开灯分钟数
        isTimerOn_4 =TimerOnSta;
        isTimerOff_4 =TimerOffSta;
        timeOnMinute_4=OnTime;
        timeOffMinute_4 = OffTime;
        //发送到云端以便同步
        //先发送数值，先同步
        sendCommendToCloud("CountDown_On_value_4",timeOnMinute_4);
        sendCommendToCloud("CountDown_Off_value_4",timeOffMinute_4);
        sendCommendToCloud("TimerOn_Switch_4",isTimerOn_4);
        sendCommendToCloud("TimerOff_Switch_4",isTimerOff_4);
        //更新UI
        updateTimerStates();

    }

    /**
     * @param dataMap 继承自 BaseDeviceControlActivity保存云端传回来的数据，可控子类调用的方法 protected
     */
    @Override
    protected void receiveCloudData(ConcurrentHashMap<String, Object> dataMap) {
        super.receiveCloudData(dataMap);
        //在本方法可以获取云端的数据点 dataMap
        getDataFromDateMap(dataMap);
        //执行父类的方法：把更新UI放在这里，可以更新UI操作
        upDateUI();
    }
    /**
     * @param dataMap 把云端的数据进行分解，同步到本地变量当中
     */
    private void getDataFromDateMap(ConcurrentHashMap<String, Object> dataMap) {
        // 已定义的设备数据点，有布尔、数值和枚举型数据
        if (dataMap.get("data") != null) {
            socketDataMap = (ConcurrentHashMap<String, Object>) dataMap.get("data");
            for (String key : socketDataMap.keySet()) {
                /*四个插座，四组数据*/
                /*第一组数据*/
                // 开关
                if (key.equals("Power_Switch_1")) {
                    isPowerOn_1 = (Boolean) socketDataMap.get(key);
                }
                //定时开指示
                if (key.equals("TimerOn_Switch_1")) {
                    isTimerOn_1 = (Boolean) socketDataMap.get(key);
                }
                //定时关指示
                if (key.equals("TimerOff_Switch_1")) {
                    isTimerOff_1 = (Boolean) socketDataMap.get(key);
                }
                //定时开数值
                if (key.equals("CountDown_On_value_1")) {
                    timeOnMinute_1 = (Integer) socketDataMap.get(key);
                }
                //定时关数值
                if (key.equals("CountDown_Off_value_1")) {
                    timeOffMinute_1 = (Integer) socketDataMap.get(key);
                }

                /*第2组数据*/
                // 开关
                if (key.equals("Power_Switch_2")) {
                    isPowerOn_2 = (Boolean) socketDataMap.get(key);
                }
                //定时开指示
                if (key.equals("TimerOn_Switch_2")) {
                    isTimerOn_2 = (Boolean) socketDataMap.get(key);
                }
                //定时关指示
                if (key.equals("TimerOff_Switch_2")) {
                    isTimerOff_2 = (Boolean) socketDataMap.get(key);
                }
                //定时开数值
                if (key.equals("CountDown_On_value_2")) {
                    timeOnMinute_2 = (Integer) socketDataMap.get(key);
                }
                //定时关数值
                if (key.equals("CountDown_Off_value_2")) {
                    timeOffMinute_2 = (Integer) socketDataMap.get(key);
                }

                /*第3组数据*/
                // 开关
                if (key.equals("Power_Switch_3")) {
                    isPowerOn_3 = (Boolean) socketDataMap.get(key);
                }
                //定时开指示
                if (key.equals("TimerOn_Switch_3")) {
                    isTimerOn_3 = (Boolean) socketDataMap.get(key);
                }
                //定时关指示
                if (key.equals("TimerOff_Switch_3")) {
                    isTimerOff_3 = (Boolean) socketDataMap.get(key);
                }
                //定时开数值
                if (key.equals("CountDown_On_value_3")) {
                    timeOnMinute_3 = (Integer) socketDataMap.get(key);
                }
                //定时关数值
                if (key.equals("CountDown_Off_value_3")) {
                    timeOffMinute_3 = (Integer) socketDataMap.get(key);
                }
                /*第3组数据*/
                // 开关
                if (key.equals("Power_Switch_4")) {
                    isPowerOn_4 = (Boolean) socketDataMap.get(key);
                }
                //定时开指示
                if (key.equals("TimerOn_Switch_4")) {
                    isTimerOn_4 = (Boolean) socketDataMap.get(key);
                }
                //定时关指示
                if (key.equals("TimerOff_Switch_4")) {
                    isTimerOff_4 = (Boolean) socketDataMap.get(key);
                }
                //定时开数值
                if (key.equals("CountDown_On_value_4")) {
                    timeOnMinute_4 = (Integer) socketDataMap.get(key);
                }
                //定时关数值
                if (key.equals("CountDown_Off_value_4")) {
                    timeOffMinute_4 = (Integer) socketDataMap.get(key);
                }


            }
        }
    }


}

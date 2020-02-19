package com.yuan_giziwits_andorid.UI.DevicrControl.GateControl;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yuan_giziwits_andorid.LOCK.LockActivity;
import com.yuan_giziwits_andorid.LOCK.PatternLockViewActivity;
import com.yuan_giziwits_andorid.Quit.MyApplication;
import com.yuan_giziwits_andorid.R;
import com.yuan_giziwits_andorid.UI.DevicrControl.BaseDeviceControlActivity;
import com.yuan_giziwits_andorid.UI.DevicrControl.TimingSocket.TimingSocketActivity;

import java.util.concurrent.ConcurrentHashMap;

public class GateControlActivity extends BaseDeviceControlActivity  {



    /** 数据点"Lock_Status"对应的值**/
    protected static Boolean lock_states=false;


    //禁止进入的标志
    private ImageView banEnter;
    //倒数计数
    private ImageView countdown;
    //门禁开、关、设置的ImageButton
    private ImageButton ib_door_open;
    private ImageButton ib_door_close;
    //下拉刷新
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //显示同步的tipDialog
    private QMUITipDialog SynTipDialog;
    //顶层框
    private QMUITopBar mTopBar;
    //在本类应用的云端数据点
    ConcurrentHashMap<String, Object> GateDataMap;

    private LinearLayout mLinearLayout;

    @SuppressLint("HandlerLeak")
    Handler UIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //动画倒计时完毕
            if(msg.what ==101){
                //图片不可见
                countdown.setVisibility(View.INVISIBLE);
                //关闭门控的锁
                mLinearLayout.setBackground(getResources().getDrawable(R.drawable.bg_door));
                //禁止进入标志
                banEnter.setVisibility(View.VISIBLE);
            }
        }
    };

    //创建消息处理器
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){

        //创建数组,保存图片资源的id
        final int[] arrayId = {0, R.drawable.night, R.drawable.eight, R.drawable.seven,
                                  R.drawable.sis, R.drawable.five, R.drawable.four,
                                  R.drawable.three, R.drawable.two, R.drawable.one, R.drawable.start};

        //处理从主线程中发过来的消息
        @Override
        public void handleMessage(Message msg) {

            //获得从子线程中发过来的数据
            int index = (int) msg.obj;

            //设置ImageView控件中显示的图片
            countdown.setImageResource(arrayId[index]);

            //设置图片的缩放比例
            countdown.setScaleX(0);
            countdown.setScaleY(0);

            //设置x方向上的缩放动画
            ObjectAnimator oa1 = ObjectAnimator.ofFloat(countdown, "scaleX", 0, 1);
            oa1.setDuration(500);

            //设置y方向上的缩放动画
            ObjectAnimator oa2 = ObjectAnimator.ofFloat(countdown, "scaleY", 0, 1);
            oa2.setDuration(500);

            //创建动画师集合
            AnimatorSet set = new AnimatorSet();

            //设置所有的动画一起播放
            set.playTogether(oa1, oa2);

            //播放动画
            set.start();
        }
    };
    //创建播放倒计时动画的线程
    public void createAnimationThread() {
        countdown.setVisibility(View.VISIBLE);
        //创建一个子线程
        Thread thread = new Thread(){

            //执行子线程
            @Override
            public void run() {

                for (int i = 1; i <= 10; i++) {

                    //创建消息对象
                    Message message = handler.obtainMessage();

                    //设置消息对象携带的数据
                    message.obj = i;

                    //将消息发送到主线程的消息处理器
                    handler.sendMessage(message);

                    try {
                        //暂停1000毫秒
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //动画执行完了
                    if(i == 10){
                        UIHandler.sendEmptyMessage(101);

                    }
                }
            }
        };

        //启动子线程
        thread.start();
    }


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
        setContentView(R.layout.activity_gate_control);
        initView();
    }

    private void initView() {

        banEnter = findViewById(R.id.iv_BanEnter_ID);
        countdown  =findViewById(R.id.iv_CountDown_ID);
        //修改背景色
        mLinearLayout =findViewById(R.id.GateChangeBG_ID);
        //topbar控件
        mTopBar = findViewById(R.id.GateControl_topBar_ID);
        //下拉刷新
        mSwipeRefreshLayout = findViewById(R.id.GateCtrlSwipeRefreshLayout_ID);
        //门禁开关设置控件
        ib_door_open = (ImageButton) findViewById(R.id.IV_ButtonID);
        ib_door_close= (ImageButton) findViewById(R.id.IV_closeButtonID);


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
                receieCloudDataAgain(GateDataMap);
            }
        });

        /*顶层状态栏设计*/
        mTopBar.addLeftImageButton(R.mipmap.ic_back,R.id.topBar_right_add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置标题为app名字
        mTopBar.setTitle("门控");
        //在topbar添加一个图标，是一个加号的图片
        mTopBar.addRightImageButton(R.mipmap.ic_setting2,R.id.topBar_Gete_setting_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在topbar右边点击加号显示出来的items
                final String[] items = new String[]{"修改密码", "其他"};
                new QMUIDialog.MenuDialogBuilder(GateControlActivity.this)
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //进入密码修改页面
                                        startActivity(new Intent(GateControlActivity.this, LockActivity.class));
                                        break;
                                    case 1:

                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        /*绑定门禁按钮开的监听器*/
        ib_door_open.setOnClickListener(new View.OnClickListener() {
            Intent intent =null;
            @Override
            public void onClick(View v) {
                //进入手势解锁界面
                intent = new Intent(GateControlActivity.this,PatternLockViewActivity.class);
                startActivityForResult(intent,0);

            }
        });
        ib_door_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭门控的锁
                mLinearLayout.setBackground(getResources().getDrawable(R.drawable.bg_door));
                banEnter.setVisibility(View.VISIBLE);
            }
        });


    }

    /**
     * 功能：下拉刷新，再次接收云端的数据同步到UI，接着进行一些弹窗的UI显示
     * @param MiandataMap  接受数据后，放置的位置
     */
    private void receieCloudDataAgain(ConcurrentHashMap<String, Object> MiandataMap) {
        //下拉之后同步云端数据
        receiveCloudData(MiandataMap);
        //触发QMUI的刷新控件
        SynTipDialog = new QMUITipDialog.Builder(GateControlActivity.this)
                .setTipWord("正在同步...")  //显示内容
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)  //显示类型
                .create();
        SynTipDialog.show();
        //不为空，说明 同步成功
        if (!MiandataMap.isEmpty()) {
            //更新一下UI界面
            //正在刷新标志消失，下拉刷新消失
            SynTipDialog.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            //显示同步成功
            SynTipDialog = new QMUITipDialog.Builder(GateControlActivity.this)
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
            }, 1500);
        } else {  //返回数据为空，则同步失败

            //正在刷新标志消失，下拉刷新消失
            SynTipDialog.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            //显示同步成功
            SynTipDialog = new QMUITipDialog.Builder(GateControlActivity.this)
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
            }, 1500);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = data.getExtras().getString("result");
        String ps = "ok";
        String fail = "fail";
        if(result.indexOf(ps)!=-1 | ps.indexOf(result)!=-1)  //说明输入正确
        {
            //成功解锁
            mLinearLayout.setBackground(getResources().getDrawable(R.drawable.bg_dooropen));
            mQMUITipDialog = new QMUITipDialog.Builder(GateControlActivity.this)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                    .setTipWord("解锁成功")
                    .create();
            mQMUITipDialog.show();
            //1.5s后弹窗消失
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mQMUITipDialog.dismiss();
                }

            },1500);
            //创建播放倒计时动画的线程
            createAnimationThread();
            //禁止进入的标志
            banEnter.setVisibility(View.INVISIBLE);
        }else if(result.indexOf(fail)!=-1 | ps.indexOf(fail)!=-1){
            //开锁失败
            mLinearLayout.setBackground(getResources().getDrawable(R.drawable.bg_door));
            mQMUITipDialog = new QMUITipDialog.Builder(GateControlActivity.this)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                        .setTipWord("解锁失败")
                        .create();
                mQMUITipDialog.show();
                //1.5s后弹窗消失
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mQMUITipDialog.dismiss();
                    }
                },1500);
                //禁止进入标志
                banEnter.setVisibility(View.VISIBLE);
        }
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
        //upDateUI();
    }
    private void getDataFromDateMap(ConcurrentHashMap<String, Object> dataMap) {
        // 已定义的设备数据点，有布尔、数值和枚举型数据
        if (dataMap.get("data") != null) {
            GateDataMap = (ConcurrentHashMap<String, Object>) dataMap.get("data");
            for (String key : GateDataMap.keySet()) {
                /*第一组数据*/
                // 开关
                //定时关指示
                if (key.equals("GateDataMap")) {
                    lock_states = (Boolean) GateDataMap.get(key);
                }
            }
        }
    }
}

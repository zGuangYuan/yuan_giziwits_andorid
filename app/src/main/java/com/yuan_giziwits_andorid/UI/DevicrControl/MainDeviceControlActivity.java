package com.yuan_giziwits_andorid.UI.DevicrControl;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yuan_giziwits_andorid.LOCK.LockActivity;
import com.yuan_giziwits_andorid.Quit.MyApplication;
import com.yuan_giziwits_andorid.R;
import com.yuan_giziwits_andorid.LOCK.PatternLockViewActivity;
import com.yuan_giziwits_andorid.UI.DevicrControl.GateControl.GateControlActivity;
import com.yuan_giziwits_andorid.UI.DevicrControl.SubMainActivity.RGBLightActivity;

import java.util.concurrent.ConcurrentHashMap;




/*继承自父类，其中父类有一个从上一个界面获取对象的一个方法
* 即使在这个类中不调用。也就自动执行。*/
public class MainDeviceControlActivity extends BaseDeviceControlActivity {


    /*变量的声明*/
    //在本类应用的云端数据点
    ConcurrentHashMap<String, Object> MaindataMap;

    //顶层框
    private QMUITopBar mTopBar;

    //下拉刷新
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //显示同步的tipDialog
    private QMUITipDialog SynTipDialog;

    //测试用
    private TextView mTv_data_temp;
    private TextView mTv_data_hum;
    private CheckBox mCheckbox01_ID;

    //定义临时的全局变量,存储分析网络获取的点
    private int LED_R;
    private int LED_G;
    private int LED_B;
    private boolean power=false;

    //定义网络的数据点不可被修改所以用final被修饰
    private static final String KEY_LED_R = "LED_R";
    private static final String KEY_POWER = "power";

   @SuppressLint("HandlerLeak")
   private Handler mHandler = new Handler(){
       @Override
       public void dispatchMessage(Message msg) {
           super.dispatchMessage(msg);
           if(msg.what==108){
               upDateUI();
           }
       }
   };
    /**
     * 异步更新UI
     */
    private void upDateUI() {
        Log.e("yuan12312","红色值:"+LED_R);
        mTv_data_temp.setText(String.valueOf(LED_R));

        //同步云端的数据到UI上
        if(power == true){
            mCheckbox01_ID.setChecked(true);
        }else {
            mCheckbox01_ID.setChecked(false);
        }
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

        //下拉刷新
        mSwipeRefreshLayout = findViewById(R.id.MainDeviceCtrlSwipeRefreshLayout_ID);


        //测试使用
        mTv_data_temp = (TextView) findViewById(R.id.tv_data_temp);
        mTv_data_hum = (TextView) findViewById(R.id.tv_data_hum);
        mCheckbox01_ID = (CheckBox) findViewById(R.id.checkbox01_ID);

        mCheckbox01_ID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sendCommendToCloud(KEY_POWER,true);
                }else{
                    sendCommendToCloud(KEY_POWER,false);
                }
            }
        });



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
               receieCloudDataAgain(MaindataMap);
            }
        });

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
        //设置标题为app名字
        mTopBar.setTitle("门控");
        //在topbar添加一个图标，是一个加号的图片
        mTopBar.addRightImageButton(R.mipmap.ic_add,R.id.topBar_right_add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在topbar右边点击加号显示出来的items
                final String[] items = new String[]{"大厅定时", "客厅定时","食厅定时"};
                new QMUIDialog.MenuDialogBuilder(MainDeviceControlActivity.this)
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //进入密码修改页面
                                        break;
                                    case 1:

                                        break;
                                    case 2:

                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).show();
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
            SynTipDialog = new QMUITipDialog.Builder(MainDeviceControlActivity.this)
                    .setTipWord("正在同步...")  //显示内容
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)  //显示类型
                    .create();
            SynTipDialog.show();
            Log.e("yuan12312","重新刷新后的数据:"+MiandataMap);
            //不为空，说明 同步成功
            if(!MiandataMap.isEmpty()){
                //正在刷新标志消失，下拉刷新消失
                SynTipDialog.dismiss();
                mSwipeRefreshLayout.setRefreshing(false);
                //显示同步成功
                SynTipDialog = new QMUITipDialog.Builder(MainDeviceControlActivity.this)
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
                SynTipDialog = new QMUITipDialog.Builder(MainDeviceControlActivity.this)
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
     * 复写父类的方法，调用云端数据
     * @param dataMap 保存云端传回来的数据，可控子类调用的方法
     */
    @Override
    protected void receiveCloudData(ConcurrentHashMap<String, Object> dataMap) {
        super.receiveCloudData(dataMap);
        //分析收到的数据
        analyzeReceiData(dataMap);
    }

    /**
     * 去分析dataMap里面的数据内容
     * @param dataMap
     */
    private void analyzeReceiData(ConcurrentHashMap<String, Object> dataMap) {
        //如果得到的数据不为空
        if(dataMap.get("data")!=null){
            //保存到这类的全局变量当中
            MaindataMap = (ConcurrentHashMap<String, Object>) dataMap.get("data");

            for (String dataKey : MaindataMap.keySet()){
                //通过云端定义的数据点标识
                if(dataKey.equals(KEY_LED_R)){
                    //把数据点拿出来保存至本地
                    LED_R = (int) MaindataMap.get(KEY_LED_R);
                }
                if(dataKey.equals(KEY_POWER)){
                    //把数据点拿出来保存至本地,然后本地一下云端的按钮
                    power = (boolean) MaindataMap.get(KEY_POWER);
                }
            }
            //通知更新UI
            mHandler.sendEmptyMessage(108);

        }
    }
}

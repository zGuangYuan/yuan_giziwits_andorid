package com.yuan_giziwits_andorid.DevicrControl;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yuan_giziwits_andorid.Utils.WifiAdminUtils;

import java.util.concurrent.ConcurrentHashMap;


public abstract class BaseDeviceControlActivity extends AppCompatActivity {

    //接受从MainActivity成功订阅的设备
    protected GizWifiDevice mDevice;
    //弹窗
    protected QMUITipDialog mQMUITipDialog;
    //顶层topBar
    protected QMUITopBar mTopBar;
    //网络状态广播
    private NetWorkChangedReceiver netWorkChangedReceiver;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==122){
                //取消同步状态变为同步成功
                mQMUITipDialog.dismiss();
                mQMUITipDialog = new QMUITipDialog.Builder(BaseDeviceControlActivity.this)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                        .setTipWord("同步成功")
                        .create();
                mQMUITipDialog.show();
                //1.5s后弹窗消失
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mQMUITipDialog.dismiss();
                    }
                },1000);
            }else if(msg.what==121){
                mQMUITipDialog = new QMUITipDialog.Builder(BaseDeviceControlActivity.this)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                        .setTipWord("设备下线，请检查手机网络或设备电源")
                        .create();
                mQMUITipDialog.show();
                //1.5s后弹窗消失
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mQMUITipDialog.dismiss();
                        //退出控制界面
                        finish();
                    }
                },1500);
            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDevice();
        //注册广播
        initNetBrocastReceiver();
    }

    protected  void initNetBrocastReceiver(){

        netWorkChangedReceiver = new NetWorkChangedReceiver();
        //此处表示拦截安卓系统网络状态改变的广播
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(netWorkChangedReceiver,intentFilter);

    }


    /**
     * 获取订阅的对象并初始化一些参数
     */
    protected  void initDevice(){
        //拿到上一个界面传来的一个设备对象
        mDevice = this.getIntent().getParcelableExtra("yuan_device01");
        //设置设备云端回调的监听,这一步很重要
        mDevice.setListener(mGizwitDeviceListener);


        //已进入控制界面就是弹窗，正在刷新
        mQMUITipDialog = new QMUITipDialog.Builder(BaseDeviceControlActivity.this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在和云端同步...")
                .create();
        mQMUITipDialog.show();
        //判断，如果10s之后仍然在同步信息，则获取失败，否则，获取成功
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(mQMUITipDialog.isShowing()){
                    mQMUITipDialog.dismiss();
                    mQMUITipDialog = new QMUITipDialog.Builder(BaseDeviceControlActivity.this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                            .setTipWord("同步失败，请检查网络！")
                            .create();
                    mQMUITipDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //退出控制界面
                            finish();
                        }
                    },1500);
                }

            }
        },10000);
        //打印一下：
        Log.e("yuan1231","从MianAcitviy传来的一个对象"+mDevice);

        //Toast.makeText(BaseDeviceControlActivity.this, "订阅对象获取成功" , Toast.LENGTH_SHORT).show();
    }


    /**
     *
     * @param dataMap 保存云端传回来的数据，可控子类调用的方法 protected
     *               此时，如果子类继承了父类有这个方法，通过这个方法就可以获得云端数据
     */
    protected void receiveCloudData(ConcurrentHashMap<String, Object> dataMap){
    }
    /*
     * 内部类，设备状态监听器。实行设备信息修改的监听.此处
     */
    private GizWifiDeviceListener mGizwitDeviceListener = new GizWifiDeviceListener(){

        @Override
        public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> dataMap, int sn) {
            super.didReceiveData(result, device, dataMap, sn);
            /*详细见 GizWifiErrorCode 枚举定义。GIZ_SDK_SUCCESS 表示成功，
                其他为失败。失败时，dataMap 为空字典*/
            if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS){
                //如果成功说明打dataMap不为空，那么就可以接受云端发送的信息了
                if(!dataMap.isEmpty()){
                    mHandler.sendEmptyMessage(122);
                    receiveCloudData(dataMap);
                    Log.e("yuan12312","接受云端的数据:"+dataMap);
                }
            }
        }

        /**
         * @param device
         * @param netStatus 该回调主动上报设备的网络状态变化，当设备重上电、断电或可控时会触发该回调
         */
        @Override
        public void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
            super.didUpdateNetStatus(device, netStatus);
            //进入控制界面的触发
            if(netStatus == GizWifiDeviceNetStatus.GizDeviceControlled ){
                //此时正在刷新的页面应该消失，使用Handler去操作
                mHandler.sendEmptyMessage(120);

            }
            //设备下线了
            else if(netStatus == GizWifiDeviceNetStatus.GizDeviceOffline){
                //则退出控制界面
                mHandler.sendEmptyMessage(121);
            }
            Log.e("yuan12312","didUpdateNetStatus:"+netStatus);
        }
    };

     /**
     * 下发控制的封装函数
     * @param key   设置的键（标志名）：和云端对应
     * @param value 设置的值
     */
    protected void sendCommendToCloud(String key,Object value){
        if(value == null)
            return;
        ConcurrentHashMap<String, Object> dataMap = new ConcurrentHashMap<>();
        dataMap.put(key,value);
        //推送到云端
        mDevice.write(dataMap,5);
    }

    //内部类，获取手机网络状态发生变化的广播
    private class NetWorkChangedReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            //获取到手机处于断开网络状态
            if(info  == null  || !info.isConnected()){
                Log.e("yuan12312","网络断开！");

            }
            //切换到移动网络
            if(info.getType() ==ConnectivityManager.TYPE_MOBILE){
                Log.e("yuan12312","切换到移动网络！");
            }
            //处于WiFi的网络
            if(info.getType()==ConnectivityManager.TYPE_WIFI){
                Log.e("yuan12312","切换到WiFi网络！");
            }
            if(info == null){
                return;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出之后，取消订阅云端消息
        mDevice.setListener(null);
        mDevice.setSubscribe("0b24bb3a613344589f5aded3bdbc82d5",false);
        //取消广播的监听
        unregisterReceiver(netWorkChangedReceiver);
    }
}

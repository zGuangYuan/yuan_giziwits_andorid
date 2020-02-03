package com.yuan_giziwits_andorid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.yuan_giziwits_andorid.UI.NetConfigActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        setContentView(R.layout.activity_main);
        //初始化SDK
        initSDK();
        //初始化UI
        initView();
    }

    private void initView() {
        //控件实例化
        QMUITopBar topBar = findViewById(R.id.topBar_ID);
        //设置标题
        topBar.setTitle("智家App");
        //在topbar添加一个图标，是一个加号的图片
        topBar.addRightImageButton(R.mipmap.ic_add,R.id.topBar_right_add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹窗
               Toast.makeText(MainActivity.this,"点击加号",Toast.LENGTH_SHORT).show();
               startActivity(new Intent(MainActivity.this, NetConfigActivity.class));
               finish();
            }
        });

    }

    private void initSDK(){
        // 设置 SDK 监听
        GizWifiSDK.sharedInstance().setListener(mListener);
        // 设置 AppInfo

        ConcurrentHashMap<String, String> appInfo =  new ConcurrentHashMap<>();
        appInfo.put("appId", "13d4933f6748458782bbd3e83b19b99e");
        appInfo.put("appSecret", "7c3a23b8ce2943bbb2a82ae7cf86f93c");
        // 设置要过滤的设备 productKey 列表。不过滤则直接传 null
        List<ConcurrentHashMap<String, String>> productInfo = new ArrayList<>();
        ConcurrentHashMap<String, String> product =  new ConcurrentHashMap<>();
        product.put("productKey", "35786ce0d056450b8dff3da6e2b08c71");
        product.put("productSecret", "0b24bb3a613344589f5aded3bdbc82d5");
        productInfo.add(product);
        // 指定要切换的域名信息。使用机智云生产环境则传 null
        ConcurrentHashMap<String, Object> cloudServiceInfo =  new ConcurrentHashMap<String, Object>();
        cloudServiceInfo.put("openAPIInfo", "your_api_domain");
        // 调用 SDK 的启动接口
        GizWifiSDK.sharedInstance().startWithAppInfo(this, appInfo,productInfo, null, false);
        // 实现系统事件通知回调
    }
    GizWifiSDKListener mListener = new GizWifiSDKListener() {
        @Override
        //通知事件的下发
        public void didNotifyEvent(GizEventType eventType, Object
                eventSource, GizWifiErrorCode eventID, String eventMessage) {
            if (eventType == GizEventType.GizEventSDK) {
                // SDK发生异常的通知
                Log.i("GizWifiSDK", "SDK event happened: " + eventID + ", " +
                        eventMessage);
            } else if (eventType == GizEventType.GizEventDevice) {
                // 设备连接断开时可能产生的通知
                GizWifiDevice mDevice = (GizWifiDevice)eventSource;
                Log.i("GizWifiSDK", "device mac: " + mDevice.getMacAddress()
                        + " disconnect caused by eventID: " + eventID + ", eventMessage: " +
                        eventMessage);
            } else if (eventType == GizEventType.GizEventM2MService) {
                // M2M服务返回的异常通知
                Log.i("GizWifiSDK", "M2M domain " + (String)eventSource + " exception happened, eventID: " + eventID + ", eventMessage: " +
                        eventMessage);
            } else if (eventType == GizEventType.GizEventToken) {
                // token失效通知
                Log.i("GizWifiSDK", "token " + (String)eventSource + " expired: " + eventMessage);
            }
        }
    };
}

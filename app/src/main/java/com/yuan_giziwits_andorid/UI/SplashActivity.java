package com.yuan_giziwits_andorid.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.yuan_giziwits_andorid.DevicrControl.LockActivity;
import com.yuan_giziwits_andorid.DevicrControl.MainDeviceControlActivity;
import com.yuan_giziwits_andorid.LOCK.WelcomeLockActivity;
import com.yuan_giziwits_andorid.MainActivity;
import com.yuan_giziwits_andorid.Quit.MyApplication;
import com.yuan_giziwits_andorid.R;
import com.yuan_giziwits_andorid.Utils.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

//欢迎界面
public class SplashActivity extends AppCompatActivity {

    @SuppressLint(value = "HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 107){
                startActivity(new Intent(SplashActivity.this, WelcomeLockActivity.class));
                //finish();

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_splash);





        //检查安卓的版本,如果版本低于6.0则，动态申请一些危险权限
        checkAndroidVersin();
    }
    //检查安卓的版本
    private void checkAndroidVersin() {
        // M代表是6.0或以上版本，则需要动态授权
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M)
        {
            
//            
//            <!-- -添加家机智云需要获取的权限 ,其中危险权限如下-->
//    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
//    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
//    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
//    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
            
            requestRunbPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,Manifest.permission.ACCESS_FINE_LOCATION
            ,Manifest.permission.ACCESS_WIFI_STATE
            ,Manifest.permission.READ_PHONE_STATE});
        }
        //如果版本低于6.0则直接跳过获取权限的步骤
        else {
            mHandler.sendEmptyMessageDelayed(107,1000);
        }
    }

    private void requestRunbPermissions(String[] strings) {
        int status = 0;
        for (String permission:strings) {
            //检查是数组里面的权限是否全部授权
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED) {
                //如果有其中一个没有授予权限的，请求授权，这个在安装app的时候会弹窗
                ActivityCompat.requestPermissions(this, strings, 108);
            }else{
                status++;
            }
        }
        //代表已经全部授权
        if(status==5){
            //弹窗
            //Toast.makeText(this,"所需的权限已经全部开启，无需再次申请！",Toast.LENGTH_SHORT).show();
            //直接跳转到新的Activity
            mHandler.sendEmptyMessageDelayed(107,2500);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 108:
                if(grantResults.length > 0 ){
                    //新建一个List,用于存放没有被授权的权限
                    List<String> deniedPermission = new ArrayList<>();
                    //逐个获取权限
                    for(int i=0;i<grantResults.length;i++){
                        //如果这个权限没有被授予
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            //添加到List列表中，但是并没有去申请权限
                            deniedPermission.add(permissions[i]);
                        }
                    }
                    if(deniedPermission.isEmpty()){
                        //弹窗
                        //Toast.makeText(this,"所需的权限全部开启！",Toast.LENGTH_SHORT).show();
                        //1.5s后跳转
                        mHandler.sendEmptyMessageDelayed(107,1500);


                    }else{
                        //弹窗
                        Toast.makeText(this,"您拒绝了部分的权限，需手动开启！",Toast.LENGTH_SHORT).show();
                        //2.5s后跳转
                        mHandler.sendEmptyMessageDelayed(107,2500);

                    }

                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //进入页面先初始化一下进入软件的解锁密码
        //SharePreferenceUtils.putString(SplashActivity.this,"enter_pasw","01258");
    }


}

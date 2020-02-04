package com.yuan_giziwits_andorid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yuan_giziwits_andorid.Adapter.LVDevicesAdapter;
import com.yuan_giziwits_andorid.UI.NetConfigActivity;
import com.yuan_giziwits_andorid.Utils.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {

    //uid和token的全局变量
    private String uid;
    private String token;
    //Listview显示框
    private ListView lv_BoundDevices;
    //初始化适配器
    private LVDevicesAdapter adapter;
    //创建一个集合变量，用于存放机智云绑定设备列表的信息
    private List<GizWifiDevice> GiziwitsdeviceList;
    //变量的声明
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //刷新的弹窗
    private QMUITipDialog refleshTipdialog;
    //提示用户刷新成功的弹窗
    private QMUITipDialog mqmuiTipDialog;
    //用户修改别名的 Dialog
    private QMUIDialog mDialog_modify_deviceName;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==109){
                //通知适配器
                adapter.notifyDataSetChanged();
            }
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        // 设置 SDK 监听,保证每次打开Acticity,能有设置SDK回调的监听
        GizWifiSDK.sharedInstance().setListener(mListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //初始化SDK
        initSDK();
        //初始化UI
        initView();
    }

    private void initView() {
        //新建一个新的集合，用于存储设备信息
        GiziwitsdeviceList = new ArrayList<>();
        //实例化控件ListView
        lv_BoundDevices = findViewById(R.id.lv_BoundDevices_ID);
        //控件实例化
        QMUITopBar topBar = findViewById(R.id.topBar_ID);
        lv_BoundDevices = findViewById(R.id.lv_BoundDevices_ID);
        //设置标题
        topBar.setTitle("智家App");
        //在topbar添加一个图标，是一个加号的图片
        topBar.addRightImageButton(R.mipmap.ic_add,R.id.topBar_right_add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹窗
               //Toast.makeText(MainActivity.this,"点击加号",Toast.LENGTH_SHORT).show();
               startActivity(new Intent(MainActivity.this, NetConfigActivity.class));
            }
        });


        //获取绑定设备列表
        getBoundDevicesList();
        //调用适配器
        adapter = new LVDevicesAdapter(this,GiziwitsdeviceList);
        lv_BoundDevices.setAdapter(adapter);
        lv_BoundDevices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //把点击条目的设备信息作为参数传递进入
                showLongDialogOnClick(GiziwitsdeviceList.get(position));
                return true;
            }
        });

        //实例化下拉下拉刷新空间，然后初始化
        mSwipeRefreshLayout =findViewById(R.id.SwipeRefreshLayout_ID);
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
        mSwipeRefreshLayout.setRefreshing(true);
        //设置手动下拉的监听事件
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //触发QMUI的刷新控件
                refleshTipdialog = new QMUITipDialog.Builder(MainActivity.this)
                        .setTipWord("正在刷新...")  //显示内容
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)  //显示类型
                        .create();
                refleshTipdialog.show();
                //下拉控件出发之后最多3s后消失
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //拿到SDK里面的设备,其中包含绑定和未绑定的设备
                        if(GizWifiSDK.sharedInstance().getDeviceList().size() !=0){
                            GiziwitsdeviceList.clear();  //清空一下集合
                            GiziwitsdeviceList.addAll(GizWifiSDK.sharedInstance().getDeviceList()); //拿到绑定和没有绑定的设备
                            adapter.notifyDataSetChanged(); //调用适配器，去刷新数据
                        }
                        //两个刷新的控件消失
                        refleshTipdialog.dismiss();
                        mSwipeRefreshLayout.setRefreshing(false);
                        //显示另外一个弹窗，通知用户刷新成功或者失败
                        if(GiziwitsdeviceList.size() == 0){
                            mqmuiTipDialog = new QMUITipDialog.Builder(MainActivity.this)
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_NOTHING)
                                    .setTipWord("暂无设备")
                                    .create();
                            mqmuiTipDialog.show();
                        }else{
                            mqmuiTipDialog = new QMUITipDialog.Builder(MainActivity.this)
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                    .setTipWord("获取成功")
                                    .create();
                            mqmuiTipDialog.show();
                        }
                        //显示获取成功之后 1.5s把这个标志取消掉
                        mSwipeRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mqmuiTipDialog.dismiss();
                            }
                        },1500);


                    }
                },3000);
            }
        });
        //3s之后自从收回刷新状态
        lv_BoundDevices.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        },3000);
    }


    /**
     * 功能：长按Listview的回调函数,目的：弹窗，修改别名
     * @param device   需要修改别名的设备
     */
    private void showLongDialogOnClick(final GizWifiDevice device) {
        final String[] items = new String[]{"重命名","解绑设备"};
       //Toast.makeText(MainActivity.this,"点击",Toast.LENGTH_SHORT).show();
        new QMUIDialog.MenuDialogBuilder(MainActivity.this)
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            //重命名设备
                            case 0:
                                showReDeviceNameDialog(device);
                                break;
                            //解绑设备
                            case 1:
                                showUnbindDeviceDialog(device);
                                break;

                        }
                        //Toast.makeText(MainActivity.this, "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .show();

    }


    /**
     * 功能：解绑设备
     * @param device
     */
    private void showUnbindDeviceDialog(final GizWifiDevice device) {
        new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                .setTitle("解绑设备")
                .setMessage("确定要解绑当前设备吗？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();

                        GizWifiSDK.sharedInstance().setListener(mListener);
                        GizWifiSDK.sharedInstance().unbindDevice(uid, token,
                                device.getDid());
                    }
                })
                .show();
    }

    /**
     * 功能：设备重命名的Dialog，弹出输入框
     * @param device
     */
    private void showReDeviceNameDialog(final GizWifiDevice device) {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(MainActivity.this);
        builder.setTitle("修改设备名字")
                .setPlaceholder("在此输入别名")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        String newName = builder.getEditText().getText().toString().trim();
                        //输入的数据不为空
                        if (newName != null && newName.length() > 0) {
                            // device是从设备列表中获取到的设备实体对象
                            device.setListener(mGizwitDeviceListener);
                            device.setCustomInfo(null, newName);

                            dialog.dismiss();
                        } else {  //输入为空
                            Toast.makeText(MainActivity.this, "请填入别名", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    /**
     * 获取绑定设备列表函数
     */
    private void getBoundDevicesList() {
        //获取从网络获取到本地的uid和token
        //把局部变量，变成全局变量 ： ctrl +alt + f
        uid = SharePreferenceUtils.getString(MainActivity.this,"_uid",null);
        token = SharePreferenceUtils.getString(MainActivity.this,"_token",null);
        if(uid !=null && token !=null){
            //获取绑定设备,有uid和token,获取指定绑定设备
            GizWifiSDK.sharedInstance().getBoundDevices(uid, token);
        }
    }

    private void initSDK(){

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

            //SDK初始化成功
            if (eventType == GizEventType.GizEventSDK) {
                //匿名登陆
                GizWifiSDK.sharedInstance().userLoginAnonymous();

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
        @Override
        public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
            super.didUserLogin(result, uid, token);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                    // 登录成功
                Log.e("yuangege","登陆成功");
                //打印uid和token
                Log.e("yuangege","uid: "+uid);
                Log.e("yuangege","token: "+token);
                //把uid和token存储到本地
                SharePreferenceUtils.putString(MainActivity.this,"_uid",uid);
                SharePreferenceUtils.putString(MainActivity.this,"_token",token);


            } else {
                    // 登录失败
                    Log.e("yuangege","登陆失败");
            }
        }
        /**
         * @param result
         * @param deviceList 已经在局域网发现的设备，也就是连接到路由器中，包括未绑定的设备
         */
        //这个函数是发现连接到云项目的设备才对，此时并没没有绑定设备
        @Override
        public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
            super.didDiscovered(result, deviceList);
                // 提示错误原因
            if(result != GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                Log.e("yuan123", "result: " + result.name());
            }
                // 显示设备列表
            Log.e("yuan123", "发现的设备列表: " + deviceList);
            //每次拿到数据就清空一下设备集合
            GiziwitsdeviceList.clear();
            //然后再去拿取回调的数据
            GiziwitsdeviceList.addAll(deviceList);
            //绑定设备
            for(int i=0; i < deviceList.size();i++){
                //如果设备没有绑定
                if(!deviceList.get(i).isBind()){
                    //开始绑定,调用bindRemoteDevice（）函数，需要productkey和productscret等，故传一个GizWifiDevice的形参进去
                    startBindDevice(deviceList.get(i));
                }
            }
            mHandler.sendEmptyMessage(109);
        }

        /**
         * 功能：绑定设备的回调
         * @param result
         * @param did
         */
        @Override
        public void didBindDevice(GizWifiErrorCode result, String did) {
            super.didBindDevice(result, did);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 绑定成功
                Toast.makeText(MainActivity.this,"恭喜，设备绑定成功",Toast.LENGTH_SHORT).show();
            } else {
                // 绑定失败
                Toast.makeText(MainActivity.this,"绑定失败咯！",Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * 功能：解绑设备的回调方法
         * @param result
         * @param did
         */
        @Override
        public void didUnbindDevice(GizWifiErrorCode result, String did) {
            super.didUnbindDevice(result, did);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Toast.makeText(MainActivity.this,"设备解绑成功",Toast.LENGTH_SHORT).show();
            } else {
                // 解绑失败
                Toast.makeText(MainActivity.this,"设备解绑失败！",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void startBindDevice(GizWifiDevice device) {
        if(uid!=null && token !=null){
            //绑定远程设备
            GizWifiSDK.sharedInstance().bindRemoteDevice(uid,token, device.getMacAddress(),
                    "35786ce0d056450b8dff3da6e2b08c71",
                    "0b24bb3a613344589f5aded3bdbc82d5");
        }
    }

    //*实行设备信息修改的监听，此处监听了别名的修改*/
    private GizWifiDeviceListener mGizwitDeviceListener = new GizWifiDeviceListener() {
        @Override
        public void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice
                device) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 修改成功，重新获取网络的设备信息，刷新一个页面
                if(GizWifiSDK.sharedInstance().getDeviceList().size()!=0){
                    //把放置设备的集合
                    GiziwitsdeviceList.clear();
                    GiziwitsdeviceList.addAll(GizWifiSDK.sharedInstance().getDeviceList());
                    //通知适配器，更改页面的别名
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "修改成功 " , Toast.LENGTH_SHORT).show();
                }

            } else {
                 // 修改失败
                Toast.makeText(MainActivity.this, "修改失败，请稍后重试 " , Toast.LENGTH_SHORT).show();
            }
        }
    };
}

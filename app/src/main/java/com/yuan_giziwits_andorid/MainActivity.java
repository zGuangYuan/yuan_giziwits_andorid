package com.yuan_giziwits_andorid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yuan_giziwits_andorid.Adapter.LVDevicesAdapter;
import com.yuan_giziwits_andorid.UI.DevicrControl.ColorSeekBar.ColorSeekBarActivity;
import com.yuan_giziwits_andorid.UI.DevicrControl.GateControl.GateControlActivity;
import com.yuan_giziwits_andorid.UI.DevicrControl.MainDeviceControlActivity;
import com.yuan_giziwits_andorid.LOCK.PaswSettingActivity;
import com.yuan_giziwits_andorid.Quit.MyApplication;
import com.yuan_giziwits_andorid.UI.DevicrControl.TimingSocket.TimingSocketActivity;
import com.yuan_giziwits_andorid.UI.NetConfigActivity;
import com.yuan_giziwits_andorid.Utils.Constant;
import com.yuan_giziwits_andorid.Utils.L;
import com.yuan_giziwits_andorid.Utils.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {

    //uid和token的全局变量
    private String uid;
    private String token;
    //上下文
    private Context mContext;

    //设备集合,在一个app中的项目集合
    private List<GizWifiDevice> GiziwitsdeviceList;

    //Listview显示设备列表
    private ListView mDeviceList;

    //适配器
    private LVDevicesAdapter adapter;

    //下拉刷新
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //刷新的弹窗
    private QMUITipDialog refleshTipdialog;
    //提示用户刷新成功的弹窗
    private QMUITipDialog mqmuiTipDialog;
    //用户修改别名的 Dialog
    private QMUIDialog mDialog_modify_deviceName;


    //顶层栏
    private QMUITopBar mQMUItopBar;

    //Handler主要用于UI操作
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
        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);
        //上下文
        mContext = this;
        //初始化SDK
        initSDK();
        //初始化UI
        initView();
    }

    private void initView() {

        /*控件实例化*/
        //实例化下拉下拉刷新空间，然后初始化
        mSwipeRefreshLayout =findViewById(R.id.SwipeRefreshLayout_ID);
        //新建一个新的集合，用于存储设备信息
        GiziwitsdeviceList = new ArrayList<>();
        //实例化控件ListView
        mDeviceList = findViewById(R.id.lv_BoundDevices_ID);
        //顶层栏
        mQMUItopBar = findViewById(R.id.topBar_ID);



        //设置标题为app名字
        mQMUItopBar.setTitle(R.string.App_name);
        //在topbar添加一个图标，是一个加号的图片
        mQMUItopBar.addRightImageButton(R.mipmap.ic_add,R.id.topBar_right_add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在topbar右边点击加号显示出来的items
                final String[] items = new String[]{"一键配网", "扫描添加"};
                new QMUIDialog.MenuDialogBuilder(MainActivity.this)
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        startActivityForResult(new Intent(MainActivity.this, NetConfigActivity.class), 105);
                                        break;
                                    case 1:
                                       // Intent i = new Intent(MainActivity.this, CaptureActivity.class);
                                       // startActivityForResult(i, REQUEST_QR_CODE);
                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        //topbar左边设置密码的按钮
        mQMUItopBar.addLeftImageButton(R.mipmap.ic_setting2,R.id.topBar_pasw_setting_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PaswSettingActivity.class));
            }
        });




        //设置下拉的颜色
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_color_theme_1,
                R.color.app_color_theme_2, R.color.app_color_theme_3,
                R.color.app_color_theme_4, R.color.app_color_theme_5, R.color.app_color_theme_6);
        //手动调用通知系统测量
        mSwipeRefreshLayout.measure(0,0);
        //打开页面就是下拉的状态,这句并不是废话
        mSwipeRefreshLayout.setRefreshing(true);

        //下拉刷新功能
        pullDownRefresh();


        //设备列表控件初始化并绑定上数据,可能有上一次的数据
        adapter = new LVDevicesAdapter(this,GiziwitsdeviceList);
        //调用设备器进行显示
        mDeviceList.setAdapter(adapter);
        //3s之后下拉刷新消失
        mDeviceList.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);


        //根据登录成功后的uid和token去得到绑定设备列表
        getLocalDevice();


        /*点击ListView的回调事件*/
        //长按ListView对应设备的回调函数
        mDeviceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //把点击条目的设备信息作为参数传递进入
                showLongDialogOnClick(GiziwitsdeviceList.get(position));
                return true;
            }
        });
        //轻触ListView对应设备的回调函数
        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //把点击条目的设备信息作为参数传递进入
                showTouchDialogOnClick(GiziwitsdeviceList.get(position));
            }
        });



    }


    /**
     * 主界面下拉刷新函数,并获取云端的设备到 GiziwitsdeviceList 这个集合
     */
    private void pullDownRefresh() {

        //设置手动下拉的监听事件,下拉刷新才会触发
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //触发QMUI的刷新控件
                refleshTipdialog = new QMUITipDialog.Builder(MainActivity.this)
                        .setTipWord("正在刷新...")  //显示内容
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)  //显示类型
                        .create();
                refleshTipdialog.show();
                //下拉控件出发之后最多3s后消失,这里面可以在主线程调用
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //打印总共有几个设备
                        L.e("设备列表：onRefresh GizWifiSDK.sharedInstance().getDeviceList():" + GizWifiSDK.sharedInstance().getDeviceList());
                        //拿到SDK里面的设备,其中包含绑定和未绑定的设备
                        if(GizWifiSDK.sharedInstance().getDeviceList().size() !=0){
                            GiziwitsdeviceList.clear();  //清空一下集合
                            GiziwitsdeviceList.addAll(GizWifiSDK.sharedInstance().getDeviceList()); //拿到绑定和没有绑定的设备
                            adapter.notifyDataSetChanged(); //调用适配器，去刷新数据
                        }
                        //两个刷新的控件消失
                        refleshTipdialog.dismiss();
                        mSwipeRefreshLayout.setRefreshing(false);

                        //获取网络状态
                        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                        //获取到手机处于断开网络状态
                        if(info  == null  || !info.isConnected()){
                            Log.e("yuan12312","网络断开！");
                            mqmuiTipDialog = new QMUITipDialog.Builder(MainActivity.this)
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                    .setTipWord("获取失败，请检查手机网络！")
                                    .create();
                            mqmuiTipDialog.show();
                            //把ListView隐藏，不可点击
                            mDeviceList.setVisibility(View.INVISIBLE);

                        }else{   //有网络

                            mDeviceList.setVisibility(View.VISIBLE);
                            //显示另外一个弹窗，通知用户刷新成功或者失败
                            if(GiziwitsdeviceList.size() == 0){
                                mqmuiTipDialog = new QMUITipDialog.Builder(MainActivity.this)
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
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
                        }
                        //显示获取成功之后 1.5s把这个标志取消掉
                        mSwipeRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mqmuiTipDialog.dismiss();
                            }
                        },1500);
                    }
                },1000);
            }
        });

    }

    /**
     * 功能：轻触Listview的回调函数,目的：进入设备控制界面
     * @param device
     */
    private void showTouchDialogOnClick(GizWifiDevice device) {
        if(device.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOffline){
            Toast.makeText(MainActivity.this,"！当前设备处于离线状态，请检查设备是否上电！",Toast.LENGTH_SHORT).show();
            return;
        }else{
            // mDevice 是从设备列表中获取到的设备实体对象
            device.setListener(mGizwitDeviceListener);  //注意监听类是Device的
            L.e("开始订阅 " + device.getProductName());
            ///此处订阅，需要识别pk和ps
            switch (device.getProductKey()) {
                case Constant.FIRST_PK:
                    device.setSubscribe(Constant.FIRST_PS, true);
                    break;
                case Constant.SECOND_PK:
                    device.setSubscribe(Constant.SECOND_PS, true);
                    break;
                case Constant.THIRD_PK:
                    device.setSubscribe(Constant.THIRD_PS, true);
                    break;
                case Constant.FORTH_PK:
                    device.setSubscribe(Constant.FORTH_PS, true);
                    break;
                case Constant.FIFTH_PK:
                    device.setSubscribe(Constant.FIFTH_PS, true);
                    break;
            }
        }
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
     * 功能：解绑设备的弹窗Dialog
     * * @param device
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
     * 根据uid和token去获取设备列表
     */
    private void getLocalDevice() {
        //获取从网络获取到本地的uid和token
        //把局部变量，变成全局变量 ： ctrl +alt + f
        uid = SharePreferenceUtils.getString(MainActivity.this,"_uid",null);
        token = SharePreferenceUtils.getString(MainActivity.this,"_token",null);
        //把uid和token打印出来,
        L.e("getLocalDevice: ");
        L.e("uid:" + uid + ",token:" + token);
        if(uid !=null && token !=null){
            //获取绑定设备,有uid和token,获取指定绑定设备
            GizWifiSDK.sharedInstance().getBoundDevices(uid, token);
        }
    }

    /**
     * 功能：SDK初始化函数，主要设置产品名字之类的
     */
    private void initSDK(){

        /*此处初始化sdk*/
        // 设置 第一个app
        ConcurrentHashMap<String, String> appInfo = new ConcurrentHashMap<>();
        appInfo.put("appId", Constant.APP_ID);
        appInfo.put("appSecret", Constant.APP_SECRET);

        //创建一个信息 ArrayList
        List<ConcurrentHashMap<String, String>> productInfo = new ArrayList<>();

        //每个产品对应一个ConcurrentHashMap<String, String>对象

        //第1个app的信息
        ConcurrentHashMap<String, String> product1 = new ConcurrentHashMap<>();
        product1.put("productKey", Constant.FIRST_PK);
        product1.put("productSecret", Constant.FIRST_PS);
        productInfo.add(product1);

        //第2个app的信息
        ConcurrentHashMap<String, String> product2 = new ConcurrentHashMap<>();
        product2.put("productKey", Constant.SECOND_PK);
        product2.put("productSecret", Constant.SECOND_PS);
        productInfo.add(product2);

        //第3个app的信息
        ConcurrentHashMap<String, String> product3 = new ConcurrentHashMap<>();
        product3.put("productKey", Constant.THIRD_PK);
        product3.put("productSecret", Constant.THIRD_PS);
        productInfo.add(product3);

        //第4个app的信息
        ConcurrentHashMap<String, String> product4 = new ConcurrentHashMap<>();
        product4.put("productKey", Constant.FORTH_PK);
        product4.put("productSecret", Constant.FORTH_PS);
        productInfo.add(product4);

        //第5个app的信息
        ConcurrentHashMap<String, String> product5 = new ConcurrentHashMap<>();
        product5.put("productKey", Constant.FIFTH_PK);
        product5.put("productSecret", Constant.FIFTH_PS);
        productInfo.add(product5);

        // 调用 SDK 的启动接口
        GizWifiSDK.sharedInstance().startWithAppInfo(this, appInfo, productInfo, null, false);
        //获取SDK版本
        String version = GizWifiSDK.sharedInstance().getVersion();
        //打印出SDK版本
        L.e("version：" + version);

    }

    /**
     * 功能：机智云提供的SDK有关的监听类函数，可以监听各种类别的事件
     */
    GizWifiSDKListener mListener = new GizWifiSDKListener() {
        @Override
        //通知事件的下发的监听
        public void didNotifyEvent(GizEventType eventType, Object eventSource, GizWifiErrorCode eventID, String eventMessage) {
            L.e("机智云的SDK匿名登录前结果：" + eventType);
            //SDK初始化成功
            if (eventType == GizEventType.GizEventSDK) {
                //匿名登陆
                GizWifiSDK.sharedInstance().userLoginAnonymous();
                L.e("SDK初始化成功，开始匿名登陆");
            }
        }
        /**
         * 功能：获取uid和token
         * @param result
         * @param uid
         * @param token
         */
        @Override
        public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
            super.didUserLogin(result, uid, token);
            L.e("机智云的SDK匿名登录结果：" + result);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                //把uid和token存储到本地
                SharePreferenceUtils.putString(MainActivity.this,"_uid",uid);
                SharePreferenceUtils.putString(MainActivity.this,"_token",token);
                L.e("匿名登陆成功，把uid和token保存到sp");
                //得到uid和之后再次获取一下设备列表
                getLocalDevice();

            } else {
                    // 登录失败
                L.e("登陆失败");
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

                L.e("根据uid和token回调返回的设备列表： didDiscovered 回调函数");
                L.e("设备列表个数：  " + deviceList.size());
                L.e("设备列表： deviceList:" + deviceList);
                //每次拿到数据就清空一下设备集合
                GiziwitsdeviceList.clear();
                //然后再去拿取回调的数据
                GiziwitsdeviceList.addAll(deviceList);
                //逐个绑定设备
                for(int i=0; i < GiziwitsdeviceList.size();i++){
                    //如果设备没有绑定
                    if(!deviceList.get(i).isBind()){
                        L.e("第" + i +"个未绑定，开始绑定");

                        //开始绑定,调用bindRemoteDevice（）函数，需要productkey和productscret等，故传一个GizWifiDevice的形参进去
                        startBindDevice(deviceList.get(i));
                    }else{
                        L.e("第" + i +"个已经绑定，无需重复绑定");
                    }
                }

            //通知适配器可以去更新ListView的UI了
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
            L.e("开始绑定后返回的错误码：didBindDevice result:" + result);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                L.e("设备绑定成功------------------->它的ID是: " + did);
                // 绑定成功
                Toast.makeText(MainActivity.this,"恭喜，设备绑定成功"+did,Toast.LENGTH_SHORT).show();

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

    /**
     * 功能：绑定远程设备
     * @param device  绑定的设备
     */
    private void startBindDevice(GizWifiDevice device) {


        //局部变量
        String uid = SharePreferenceUtils.getString(this, "_uid", null);
        String token = SharePreferenceUtils.getString(this, "_token", null);

        if(uid!=null && token !=null){

            //根据 pk 去判断需要填的ps
            if(device.getProductKey().equals(Constant.FIRST_PK)){
                L.e("                                                                " );
                L.e("<----------------------毕设01_七彩灯------------------------------->" );
                L.e("|绑定的设备的  MacAddress:" + device.getMacAddress());
                L.e("|uid:" + uid);
                L.e("|token:" + token);
                L.e("|getMacAddress:" + device.getMacAddress());
                L.e("|getProductKey:" + device.getProductKey());
                L.e("|getProductScret:" + Constant.FIRST_PS);
                L.e("<------------------------毕设01_七彩灯----------------------------->" );
                L.e("                                                                " );
                GizWifiSDK.sharedInstance().bindRemoteDevice(uid,token, device.getMacAddress(),device.getProductKey(),Constant.FIRST_PS);

            }
            else if(device.getProductKey().equals(Constant.SECOND_PK))
            {
                L.e("                                                                " );
                L.e("<----------------------毕设02_门控------------------------------->" );
                L.e("|绑定的设备的  MacAddress:" + device.getMacAddress());
                L.e("|uid:" + uid);
                L.e("|token:" + token);
                L.e("|getMacAddress:" + device.getMacAddress());
                L.e("|getProductKey:" + device.getProductKey());
                L.e("|getProductScret:" + Constant.SECOND_PS);
                L.e("<------------------------毕设02_门控----------------------------->" );
                L.e("                                                                " );
                GizWifiSDK.sharedInstance().bindRemoteDevice(uid,token, device.getMacAddress(),device.getProductKey(),Constant.SECOND_PS);

            }
            else if(device.getProductKey().equals(Constant.THIRD_PK))
            {
                L.e("                                                                " );
                L.e("<----------------------毕设03_灯及状态------------------------------->" );
                L.e("|绑定的设备的  MacAddress:" + device.getMacAddress());
                L.e("|uid:" + uid);
                L.e("|token:" + token);
                L.e("|getMacAddress:" + device.getMacAddress());
                L.e("|getProductKey:" + device.getProductKey());
                L.e("|getProductScret:" + Constant.THIRD_PS);
                L.e("<------------------------毕设03_灯及状态----------------------------->" );
                L.e("                                                                " );
                GizWifiSDK.sharedInstance().bindRemoteDevice(uid,token, device.getMacAddress(),device.getProductKey(),Constant.THIRD_PS);

            }
            else if(device.getProductKey().equals(Constant.FORTH_PK))
            {
                L.e("                                                                " );
                L.e("<----------------------未设置------------------------------->" );
                L.e("|绑定的设备的  MacAddress:" + device.getMacAddress());
                L.e("|uid:" + uid);
                L.e("|token:" + token);
                L.e("|getMacAddress:" + device.getMacAddress());
                L.e("|getProductKey:" + device.getProductKey());
                L.e("|getProductScret:" + Constant.FORTH_PS);
                L.e("<------------------------未设置----------------------------->" );
                L.e("                                                                " );
                GizWifiSDK.sharedInstance().bindRemoteDevice(uid,token, device.getMacAddress(),device.getProductKey(),Constant.FORTH_PS);

            }
            else if(device.getProductKey().equals(Constant.FIFTH_PK))
            {
                L.e("                                                                " );
                L.e("<----------------------未设置------------------------------->" );
                L.e("|绑定的设备的  MacAddress:" + device.getMacAddress());
                L.e("|uid:" + uid);
                L.e("|token:" + token);
                L.e("|getMacAddress:" + device.getMacAddress());
                L.e("|getProductKey:" + device.getProductKey());
                L.e("|getProductScret:" + Constant.FIFTH_PS);
                L.e("<------------------------未设置----------------------------->" );
                L.e("                                                                " );
                GizWifiSDK.sharedInstance().bindRemoteDevice(uid,token, device.getMacAddress(),device.getProductKey(),Constant.FIFTH_PS);

            }
        }
    }

    /*
     * 实行设备信息修改的监听，此处监听了别名的修改
     */
    private GizWifiDeviceListener mGizwitDeviceListener = new GizWifiDeviceListener() {
        @Override
        public void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
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

        /**
         * 功能：订阅设备回调函数
         * @param result
         * @param device
         * @param isSubscribed
         */
        @Override
        public void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
            super.didSetSubscribe(result, device, isSubscribed);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                Toast.makeText(MainActivity.this, device.getProductName() +"订阅成功" , Toast.LENGTH_SHORT).show();

                //把订阅成功的对象放进initent传给控制界面,并跳转到另一个界面
                Intent intent =new Intent();
                intent.putExtra("yuan_device01",device);
                switch (device.getProductKey()) {
                    //毕设01_七彩灯
                    case Constant.FIRST_PK:
                        intent.setClass(MainActivity.this, ColorSeekBarActivity.class);
                        startActivity(intent);
                        break;
                    //毕设02_门控
                    case Constant.SECOND_PK:
                        //智能灯控制实验
                        intent.setClass(MainActivity.this, GateControlActivity.class);
                        startActivity(intent);
                        break;
                    //毕设03_灯及状态
                    case Constant.THIRD_PK:
                        intent.setClass(MainActivity.this, MainDeviceControlActivity.class);
                        startActivity(intent);
                        break;
                    //毕设04_定时插座
                    case Constant.FORTH_PK:
                        //智能灯控制实验
                        intent.setClass(MainActivity.this, TimingSocketActivity.class);
                        startActivity(intent);
                        break;
                    //微信宠物屋
                    case Constant.FIFTH_PK:
                        //intent.setClass(MainActivity.this, MainDeviceControlActivity.class);
                        //startActivity(intent);
                        break;
                    default:
                        break;

                }


            }
        }
    };
    /**
     * 返回按键的监听
     */
    private boolean mIsExit;
    /**双击返回键退出*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                //this.finish();
                MyApplication.getInstance().exit();
            } else {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

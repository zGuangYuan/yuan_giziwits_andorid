package com.yuan_giziwits_andorid.LOCK;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yuan_giziwits_andorid.Quit.MyApplication;
import com.yuan_giziwits_andorid.R;
import com.yuan_giziwits_andorid.Utils.SharePreferenceUtils;

public class PaswSettingActivity extends AppCompatActivity {

    private String old_paw;
    private String new_paw;

    /** 当前的设备 */
    private EditText mEt_origin_paw;
    private EditText mEt_new_paw;

    private Button mBtn_sure_rectify;
    private Button mBtn_cancle_rectify;

    //顶层框
    private QMUITopBar DeviceControltopBar;

    private QMUITipDialog mqmuiTipDialog;
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
        setContentView(R.layout.activity_pasw_setting);

        initView();


    }


    private void initView() {

        /*控件实例化*/
        //topbar控件
        DeviceControltopBar = findViewById(R.id.Enter_pawRec_topBar_ID);
        mEt_origin_paw = findViewById(R.id.et_origin_pasw_ID2);
        mEt_new_paw = findViewById(R.id.et_new_pasw_ID2);

        mBtn_sure_rectify = findViewById(R.id.sure_rectify_paw_ID2);
        mBtn_cancle_rectify = findViewById(R.id.cancle_rectify_paw_ID2);

        /*设置控制界面的topbar*/
        DeviceControltopBar.setTitle("用户密码修改");
        DeviceControltopBar.addLeftImageButton(R.mipmap.ic_back, R.id.topBar_right_add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtn_cancle_rectify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不做任何的修改
                finish();
            }
        });

        mBtn_sure_rectify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先拿到真正的密码先
                String real_pasw = SharePreferenceUtils.getString(PaswSettingActivity.this,"enter_pasw",null);
                //按确认键之后,拿到编辑框的内容
                old_paw = mEt_origin_paw.getText().toString().intern(); //且去空格
                new_paw = mEt_new_paw.getText().toString().intern();
                //新的旧的密码全不为空则进入下一步
                if(!old_paw.isEmpty() && !new_paw.isEmpty() ){
                    if(real_pasw.equals(old_paw)){
                        if(new_paw.length() > 6 || new_paw.length() < 2){
                            Toast.makeText(PaswSettingActivity.this,"输入的新密码长度不合适",Toast.LENGTH_SHORT).show();
                        }else{

                            //修改密码
                            // SharePreferenceUtils.putString(LockActivity.this,"m_pasw",new_paw);
                            //使用QMUI的弹窗再次提醒用户是否需要修改
                            sureRectityDialog();

                        }


                    }else{
                        Toast.makeText(PaswSettingActivity.this,"原密码输入错误",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(PaswSettingActivity.this,"!请检查你的输入!",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    /**
     * 确认修改密码函数
     */
    private void sureRectityDialog() {
        new QMUIDialog.MessageDialogBuilder(PaswSettingActivity.this)
                .setTitle("确认修改")
                .setMessage("确定修改密码为：" + new_paw)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        //直接退出
                        finish();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //修改密码
                        SharePreferenceUtils.putString(PaswSettingActivity.this,"enter_pasw",new_paw);

                        //清空编辑框
                        mEt_origin_paw.setText("");
                        mEt_new_paw.setText("");
                        dialog.dismiss();
                        //提示语，提升用户体验
                        mqmuiTipDialog = new QMUITipDialog.Builder(PaswSettingActivity.this)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                .setTipWord("修改成功")
                                .create();
                        mqmuiTipDialog.show();
                        //1.5s后自动退出
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mqmuiTipDialog.dismiss();
                                finish();
                                //startActivity(new Intent(LockActivity.this,MainDeviceControlActivity.class));
                            }
                        },1500);
                    }
                }).show();
    }

}

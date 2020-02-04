package com.yuan_giziwits_andorid.DevicrControl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.yuan_giziwits_andorid.R;


import java.util.Timer;
import java.util.TimerTask;


public class LockActivity extends AppCompatActivity {


    /** 当前的设备 */
    private GizWifiDevice device;
    private EditText ed_passwd;
    private Button sure_Button;
    private Button cancle_Button;
    private String mpassward="abc";

    String softssid, uid, token;
    /**
     * 判断用户登录状态 0：未登录 1：实名用户登录 2：匿名用户登录 3：匿名用户登录中 4：匿名用户登录中断
     */
    public static int loginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        //initDevice();
        //setActionBar(true, true, device.getProductName());
        initview();

    }
    private void initview(){
        //获取控件对象
        ed_passwd = (EditText) findViewById(R.id.ED_Passward_ID);
        sure_Button =(Button) findViewById(R.id.BT_sure_ID);
        cancle_Button= (Button) findViewById(R.id.BT_cancle_ID);

        sure_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mpassward.indexOf("open")!=-1)
                {
                    //新建一个Intent对象
                    Intent intent = new Intent();
                    //生成一个Bundle对象
                    Bundle bundle1 =new Bundle();
                    //放置数据
                    bundle1.putString("result",mpassward);
                    bundle1.putInt("data",6666);
                    intent.putExtras(bundle1);
                    //返回Intent对象
                    setResult(Activity.RESULT_OK,intent);
                    //关闭当前的Activity
                    finish();

                }
                else if(mpassward.indexOf("abc")!=-1)
                {
                    ToastUtil.showMsg(LockActivity.this,"！请输入正确的密码！");

                }
                else
                {
                    ToastUtil.showMsg(LockActivity.this,"密码错误，请重新输入");

                }

            }
        });

        cancle_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(LockActivity.this,"开锁失败",Toast.LENGTH_LONG).show();
                //新建一个Intent对象
                Intent intent = new Intent();
                //生成一个Bundle对象
                Bundle bundle1 =new Bundle();
                //放置数据
                bundle1.putString("result","fail");
                intent.putExtras(bundle1);
                //返回Intent对象
                setResult(Activity.RESULT_OK,intent);
                //关闭当前的Activity
                finish();

            }
        });
        ed_passwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                mpassward = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //mpassward = s.toString();
            }
        });

    }
    private void initDevice() {
        Intent intent = getIntent();
        device = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
        Log.i("Apptest", device.getDid());

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}


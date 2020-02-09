package com.yuan_giziwits_andorid.DevicrControl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.yuan_giziwits_andorid.Quit.MyApplication;
import com.yuan_giziwits_andorid.R;


import java.util.concurrent.ConcurrentHashMap;

public class RGBLightActivity extends AppCompatActivity {

    /** 当前的设备 */
    private GizWifiDevice device;
    //确认取消按钮对象
    private Button btn_colorsel_sure;
    private Button btn_colorsel_cancle;

    //灯图标
    private CheckBox cb_RedLight;
    private CheckBox cb_GreenLight;
    private CheckBox cb_BlueLight;
    private CheckBox cb_PinkLight;
    private CheckBox cb_YellowLight;
    private CheckBox cb_WhiteLight;

    //颜色显示的Textview
    private TextView tv_show_color;


    //设置全局变量
    private ConcurrentHashMap<String, Object> map;

    //颜色标识数组
    private int color_nun=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 添加Activity到堆栈，退出用
        MyApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_rgblight);
        initview();
    }
    private void initview() {
        //获取控件对象  补全快捷键 ：ALT + ENTER
        btn_colorsel_sure = (Button) findViewById(R.id.color_sel_sure_ID);
        btn_colorsel_cancle= (Button) findViewById(R.id.color_sel_cancle_ID);

        cb_RedLight= (CheckBox) findViewById(R.id.RedLight_ID);
        cb_GreenLight= (CheckBox) findViewById(R.id.GreenLight_ID);
        cb_BlueLight= (CheckBox) findViewById(R.id.BlueLight_ID);
        cb_PinkLight= (CheckBox) findViewById(R.id.PinkLight_ID);
        cb_YellowLight= (CheckBox) findViewById(R.id.YellowLight_ID);
        cb_WhiteLight= (CheckBox) findViewById(R.id.WhiteLight_ID);


        tv_show_color = (TextView) findViewById(R.id.show_color_ID);

        //确认按钮监听
        btn_colorsel_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (color_nun)
                {
                    case 0:
                        ToastUtil.showMsg(RGBLightActivity.this,"！未选中任何颜色！");
                        break;
                    case 1:
                        ToastUtil.showMsg(RGBLightActivity.this,"设置为-->红色");
                        break;
                    case 2:
                        ToastUtil.showMsg(RGBLightActivity.this,"设置为-->绿色");
                        break;
                    case 3:
                        ToastUtil.showMsg(RGBLightActivity.this,"设置为-->蓝色");
                        break;
                    case 4:
                        ToastUtil.showMsg(RGBLightActivity.this,"设置为-->粉色");
                        break;
                    case 5:
                        ToastUtil.showMsg(RGBLightActivity.this,"设置为-->黄色");
                        break;
                    case 6:
                        ToastUtil.showMsg(RGBLightActivity.this,"设置为-->白色");
                        break;

                }


            }
        });
        btn_colorsel_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //新建一个Intent对象
                Intent intent = new Intent();
                //生成一个Bundle对象
                Bundle bundle1 =new Bundle();
                //放置数据
                bundle1.putString("result","not_color");
                intent.putExtras(bundle1);
                //返回Intent对象
                setResult(Activity.RESULT_OK,intent);
                //关闭当前的Activity
                finish();
            }
        });
        cb_RedLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_RedLight.isChecked())
                {
                    cb_GreenLight.setChecked(false);
                    cb_BlueLight.setChecked(false);
                    cb_PinkLight.setChecked(false);
                    cb_YellowLight.setChecked(false);
                    cb_WhiteLight.setChecked(false);
                    //颜色标志
                    color_nun = 1;
                    //文本显示
                    tv_show_color.setText("   红色  ");
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,true);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：开");
                }else
                {
                    color_nun = 0;
                    //文本显示
                    tv_show_color.setText("        ");
//
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,false);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：关");
                }
            }
        });
        cb_GreenLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_GreenLight.isChecked())
                {
                    cb_RedLight.setChecked(false);
                    cb_BlueLight.setChecked(false);
                    cb_PinkLight.setChecked(false);
                    cb_YellowLight.setChecked(false);
                    cb_WhiteLight.setChecked(false);
                    //文本显示
                    tv_show_color.setText("   绿色  ");
                    color_nun = 2;
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,true);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：开");
                }else
                {
                    color_nun =0;
                    //文本显示
                    tv_show_color.setText("        ");
//
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,false);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：关");
                }
            }
        });
        cb_BlueLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_BlueLight.isChecked())
                {
                    cb_RedLight.setChecked(false);
                    cb_GreenLight.setChecked(false);
                    cb_PinkLight.setChecked(false);
                    cb_YellowLight.setChecked(false);
                    cb_WhiteLight.setChecked(false);
                    //文本显示
                    tv_show_color.setText("   蓝色  ");

                    color_nun = 3;
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,true);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：开");
                }else
                {
                    color_nun =0;
                    //文本显示
                    tv_show_color.setText("        ");
//
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,false);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：关");
                }
            }
        });
        cb_PinkLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_PinkLight.isChecked())
                {
                    cb_RedLight.setChecked(false);
                    cb_GreenLight.setChecked(false);
                    cb_BlueLight.setChecked(false);
                    cb_YellowLight.setChecked(false);
                    cb_WhiteLight.setChecked(false);
                    //文本显示
                    tv_show_color.setText("   粉色  ");
                    color_nun =4;
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,true);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：开");
                }else
                {
                    color_nun =0;
                    //文本显示
                    tv_show_color.setText("        ");
//
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,false);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：关");
                }
            }
        });
        cb_YellowLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_YellowLight.isChecked())
                {
                    cb_RedLight.setChecked(false);
                    cb_GreenLight.setChecked(false);
                    cb_BlueLight.setChecked(false);
                    cb_PinkLight.setChecked(false);
                    cb_WhiteLight.setChecked(false);
                    //文本显示
                    tv_show_color.setText("   黄色  ");
                    color_nun =5;
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,true);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：开");
                }else
                {
                    color_nun =0;
                    //文本显示
                    tv_show_color.setText("        ");
//
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,false);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：关");
                }
            }
        });
        cb_WhiteLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_WhiteLight.isChecked())
                {
                    cb_RedLight.setChecked(false);
                    cb_BlueLight.setChecked(false);
                    cb_PinkLight.setChecked(false);
                    cb_YellowLight.setChecked(false);
                    cb_GreenLight.setChecked(false);
                    //文本显示
                    tv_show_color.setText("   白色  ");
                    color_nun =6;
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,true);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：开");
                }else
                {
//
                    color_nun =0;
                    //文本显示
                    tv_show_color.setText("        ");
//                    map=new ConcurrentHashMap<>();
//                    map.put(RED_OnOff,false);
//                    device.write(map,0);
//                    tv_RED.setText("大厅灯开关：关");
                }
            }
        });





    }

}

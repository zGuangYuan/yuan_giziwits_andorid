package com.yuan_giziwits_andorid.Utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static Toast mToast;
    public static void showMsg(Context context, String msg){
        //如果mToast为空，则创建一个Toast
        if (mToast == null){
            //返回一个Toast对象
            mToast = Toast.makeText(context,msg, Toast.LENGTH_LONG);
        }else{  //否则直接显示当前的Toast
            mToast.setText(msg);
        }
        mToast.show();
    }
}


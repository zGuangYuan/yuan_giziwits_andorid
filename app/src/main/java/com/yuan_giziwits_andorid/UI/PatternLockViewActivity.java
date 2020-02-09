package com.yuan_giziwits_andorid.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.andrognito.rxpatternlockview.RxPatternLockView;
import com.andrognito.rxpatternlockview.events.PatternLockCompleteEvent;
import com.andrognito.rxpatternlockview.events.PatternLockCompoundEvent;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yuan_giziwits_andorid.LOCK.PaswSettingActivity;
import com.yuan_giziwits_andorid.LOCK.WelcomeLockActivity;
import com.yuan_giziwits_andorid.Quit.MyApplication;
import com.yuan_giziwits_andorid.R;
import com.yuan_giziwits_andorid.Utils.SharePreferenceUtils;

import java.util.List;

import io.reactivex.functions.Consumer;

public class PatternLockViewActivity extends AppCompatActivity {

    //头像
    private ImageView mimageView;
    //弹窗确认函数
    private QMUITipDialog mqmuiTipDialog;


    private PatternLockView mPatternLockView;
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 添加Activity到堆栈
        MyApplication.getInstance().addActivity(this);
        //设置为全屏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_pattern_lock_view);


//        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);       // 设置当前视图更多
//        mPatternLockView.setInStealthMode(true);                                     // 将模式设置为隐藏模式（隐藏模式绘图）
//        mPatternLockView.setTactileFeedbackEnabled(true);                            //绘制图案时启用振动反馈
//        mPatternLockView.setInputEnabled(false);                                     //完全禁用模式锁定视图中的任何输入
//        mPatternLockView.setDotCount(3);
//        mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
//        mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
//        mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
//        mPatternLockView.setAspectRatioEnabled(true);
//        mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
//        mPatternLockView.setNormalStateColor(ResourceUtils.getColor(this, R.color.white));
//        mPatternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.primary));
//        mPatternLockView.setWrongStateColor(ResourceUtils.getColor(this, R.color.pomegranate));
//        mPatternLockView.setDotAnimationDuration(150);
//        mPatternLockView.setPathEndAnimationDuration(100);


        mimageView = findViewById(R.id.profile_image1);

        mimageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //重新设置进入软件的密码
                new QMUIDialog.MessageDialogBuilder(PatternLockViewActivity.this)
                        .setTitle("确认重置")
                        .setMessage("您确定重置密码为:\n 01258 ?")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                                //直接退出
                                //finish();
                                return;
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                //修改密码
                                SharePreferenceUtils.putString(PatternLockViewActivity.this,"m_pasw","01258");

                                dialog.dismiss();
                                //提示语，提升用户体验
                                mqmuiTipDialog = new QMUITipDialog.Builder(PatternLockViewActivity.this)
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                        .setTipWord("修改成功")
                                        .create();
                                mqmuiTipDialog.show();
                                //1.5s后自动退出
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mqmuiTipDialog.dismiss();
                                        //startActivity(new Intent(LockActivity.this,MainDeviceControlActivity.class));
                                    }
                                },1500);
                            }
                        }).show();
                return true;
            }
        });



        //获取控件对象
        mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);
        // n*n大小   3*3
        mPatternLockView.setDotCount(3);
        //没有点击时点的大小
        mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
        //点击时点的大小
        mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
        //更改路径距离
        mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
        mPatternLockView.setAspectRatioEnabled(true);
        mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        mPatternLockView.setDotAnimationDuration(150);
        mPatternLockView.setPathEndAnimationDuration(100);
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(true);
        mPatternLockView.setInputEnabled(true);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);



        RxPatternLockView.patternComplete(mPatternLockView)
                .subscribe(new Consumer<PatternLockCompleteEvent>() {
                    @Override
                    public void accept(PatternLockCompleteEvent patternLockCompleteEvent) throws Exception {
                        Log.d(getClass().getName(), "Complete: " + patternLockCompleteEvent.getPattern().toString());
                    }
                });

        RxPatternLockView.patternChanges(mPatternLockView)
                .subscribe(new Consumer<PatternLockCompoundEvent>() {
                    @Override
                    public void accept(PatternLockCompoundEvent event) throws Exception {
                        if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_STARTED) {
                            Log.d(getClass().getName(), "Pattern drawing started");
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_PROGRESS) {
                            Log.d(getClass().getName(), "Pattern progress: " +
                                    PatternLockUtils.patternToString(mPatternLockView, event.getPattern()));
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_COMPLETE) {
                            Log.d(getClass().getName(), "Pattern complete: " +
                                    PatternLockUtils.patternToString(mPatternLockView, event.getPattern()));
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_CLEARED) {
                            Log.d(getClass().getName(), "Pattern has been cleared");
                        }
                    }
                });

    }

    //设置监听器
    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(mPatternLockView, pattern));
            //通过本地读取密码
            String paswd = SharePreferenceUtils.getString(PatternLockViewActivity.this,"m_pasw",null);
            String patternToString = PatternLockUtils.patternToString(mPatternLockView, pattern);
            if(!TextUtils.isEmpty(patternToString)){
                if(patternToString.equals(paswd)){
                    //判断为正确
                    mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                    Toast.makeText(PatternLockViewActivity.this,"您绘制的密码是："+patternToString+"\n"+"密码正确，开锁成功",Toast.LENGTH_SHORT).show();

                }else {

                    mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    Toast.makeText(PatternLockViewActivity.this,"您绘制的密码是："+patternToString+"\n"+"密码错误，请重新绘制", Toast.LENGTH_SHORT).show();
                }
            }
            //3s后清除图案
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPatternLockView.clearPattern();
                }
            },1000);
        }
        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };
}

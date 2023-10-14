package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.service.MyService;
import com.example.myapplication.service.OnProgressListener;
import com.example.myapplication.service.TestService;
import com.example.myapplication.utils.ImgUtils;
import com.example.myapplication.utils.ToastUtils;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 界面1: app主界面
 */
public class MainActivity extends AppCompatActivity {

    private MyService myService;
    private TextView state;

    private Intent intent;
    private MsgReceiver msgReceiver;
    Toast toast = null;

    private static Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("test","MainActivity create");
        intent = new Intent(this,MyService.class);
        //注册广播接收器
        if(msgReceiver == null){
            Log.d("test", "广播接收器为空，注册广播接收器");
            msgReceiver = new MsgReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("RECEIVER");
            registerReceiver(msgReceiver,intentFilter);
        }

        Map<String,String> dataMap = new HashMap<>();//保存页面数据
        //加载opencv
        if(OpenCVLoader.initDebug()){
            Log.d("OpenCV","加载成功");
        }else{
            Log.d("OpenCV","加载失败");
        }

        //脚本启动状态
        state = findViewById(R.id.state);
        //文本输入框
        EditText tpjnum = findViewById(R.id.tpjnum);
        EditText victorynum = findViewById(R.id.victorynum);
        EditText failnum = findViewById(R.id.failnum);
        EditText testTxt = findViewById(R.id.testTxt);

        //按钮
        Button start = findViewById(R.id.start);
        Button stop = findViewById(R.id.stop);
        Button test = findViewById(R.id.test);
        Button jump = findViewById(R.id.jump);

        //单选框
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton onlyJjtpRadio = findViewById(R.id.radio);
        RadioButton onlyTansuoRadio = findViewById(R.id.radio2);
        RadioButton bothRadio = findViewById(R.id.radio3);
        bothRadio.setChecked(true);//默认选择

        //按钮点击事件

        //服务启动
        start.setOnClickListener(v->{

            if(tpjnum.getText().toString().matches("\\d+")
                && victorynum.getText().toString().matches("\\d+")
                &&failnum.getText().toString().matches("\\d+")){

                intent.putExtra("tpjnum",Integer.parseInt(tpjnum.getText().toString()));
                intent.putExtra("victorynum",Integer.parseInt(victorynum.getText().toString()));
                intent.putExtra("failnum",Integer.parseInt(failnum.getText().toString()));
                intent.putExtra("onlyTansuo",onlyTansuoRadio.isChecked());
                intent.putExtra("onlyJjtp",onlyJjtpRadio.isChecked());
                intent.putExtra("bothRadio",bothRadio.isChecked());
                startService(intent);
            }else{
                Toast.makeText(this,"请输入正整数！",Toast.LENGTH_SHORT).show();

            }

        });

        //服务停止
        stop.setOnClickListener(v->{

            stopService(intent);

            Intent intent2 = new Intent(this, TestService.class);
            stopService(intent2);
        });

        //测试
        test.setOnClickListener(v->{

            new Thread(()->{
                if(null != myService ){
                    Log.d("test","1" + myService.getStartThread());
                    //state.setText("运行中");
                }else {
                    Log.d("test","2");
                    //state.setText("停止");
                }
            }).start();

//            intent.putExtra("testTxt",testTxt.getText().toString());
//            startService(intent);

        });

        //跳转页面
        jump.setOnClickListener(v->{
            Intent intent2 = new Intent(this,MainActivity2.class);
            startActivity(intent2);
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d("test","onSaveInstanceState");
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onStart() {
        Log.d("test","MainActivity start");

        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("test","MainActivity stop");
        //unregisterReceiver(msgReceiver);
        super.onStop();
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //返回一个绑定服务的实例，可直接调用其公开的方法
            myService = ((MyService.Mybinder) iBinder).getService();
            //注册回调接口(可接收service进度的变化)
            myService.setOnProgressListener(new OnProgressListener() {
                @Override
                public void onProgress(int progress) {
                    Log.d("test", "设置进度"+ progress);
                    //无法更改ui
                    //String str = "设置进度"+ progress;
                    //Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
                    //progressBar.setProgress(progress);
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    /**
     * 广播接收器,接收脚本运行状态反馈
     */
    public class MsgReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("state");
            boolean startFlag = intent.getBooleanExtra("startFlag", false);
            //可以更改ui
           if(null != toast){//覆盖
               toast.cancel();
           }

           if(startFlag){
               //ToastUtils.showMessage(context,str);
               state.setText("运行中");
               toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
               toast.setGravity(Gravity.CENTER,0,720);
               toast.show();
           }else {
               //ToastUtils.showMessage(context,"脚本停止");
               state.setText("停止");
               toast = Toast.makeText(context, "脚本停止", Toast.LENGTH_SHORT);
               toast.setGravity(Gravity.CENTER,0,720);
               toast.show();
           }
        }
    }


    @Override
    protected void onDestroy() {
        //unbindService(conn);
        //注销广播
        unregisterReceiver(msgReceiver);
        Log.d("test","MainActivity destory");
        super.onDestroy();
    }
}
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.constant.FunctionConstant;
import com.example.myapplication.dao.MatchResult;
import com.example.myapplication.service.MyService;
import com.example.myapplication.service.OnProgressListener;
import com.example.myapplication.service.TestService;
import com.example.myapplication.utils.ImgUtils;
import com.example.myapplication.utils.ToastUtils;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.reflect.KFunction;

/**
 * 界面1: app主界面
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private MyService myService;

    private Intent intent;
    private MsgReceiver msgReceiver;
    Toast toast = null;

    //全局变量
    private TextView state;
    private TextView funcTv;
    private String function;

    EditText tpjnum,victorynum,failnum,testTxt;

    final int[] spinnerFlag = {0};

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

        //加载opencv
        if(OpenCVLoader.initDebug()){
            Log.d("OpenCV","加载成功");
        }else{
            Log.d("OpenCV","加载失败");
        }
        //脚本运行状态
        state = findViewById(R.id.state);
        funcTv = findViewById(R.id.funcTv);
        //文本输入框
         tpjnum = findViewById(R.id.tpjnum);
         victorynum = findViewById(R.id.victorynum);
         failnum = findViewById(R.id.failnum);
         testTxt = findViewById(R.id.testTxt);

        //按钮
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.test).setOnClickListener(this);
        findViewById(R.id.jump).setOnClickListener(this);

        //下拉框
        ((Spinner)findViewById(R.id.spinner)).setOnItemSelectedListener(this);
        ((Spinner)findViewById(R.id.spinner2)).setOnItemSelectedListener(this);

        ((Spinner)findViewById(R.id.spinner)).setSelection(2);//默认选择探索-突破循环
        function = FunctionConstant.TANSUO_JJTP;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.start:{
                if(tpjnum.getText().toString().matches("\\d+")
                        && victorynum.getText().toString().matches("\\d+")
                        &&failnum.getText().toString().matches("\\d+")){

                    view.setEnabled(false);
                    intent.putExtra("tpjnum",Integer.parseInt(tpjnum.getText().toString()));
                    intent.putExtra("victorynum",Integer.parseInt(victorynum.getText().toString()));
                    intent.putExtra("failnum",Integer.parseInt(failnum.getText().toString()));
                    intent.putExtra("function",function);
                    startService(intent);
                }else{
                    Toast.makeText(this,"请输入正整数！",Toast.LENGTH_SHORT).show();

                }
            }
            break;

            case R.id.stop:{
                findViewById(R.id.start).setEnabled(true);
                stopService(intent);
                Intent intent2 = new Intent(this, TestService.class);
                stopService(intent2);
            }
            break;
            case R.id.test:{



               // Mat target = Imgcodecs.imread("/mnt/shared/Pictures/yys/"+ "tpj0" +".png");
                //Imgproc.threshold(target,target,100,255,Imgproc.THRESH_BINARY);

               // Imgproc.findContours(target,Imgproc.drawContours(),);
                Intent intent = new Intent(this,TestService.class);
                intent.putExtra("testTxt",testTxt.getText().toString());
                startService(intent);

                //ImgUtils.swipe(401,659,600,659,200);
//                ImgUtils.getScreen();
//                MatchResult img = ImgUtils.findImg("baigui/test");
//                if(img.getSimilarity()>0.9){
//                    Toast.makeText(this,img.getX() +","+ img.getY(),Toast.LENGTH_SHORT).show();
//
//                }
//                //401,659
//                ImgUtils.tap(img.getX(),img.getY());


            }
            break;
            case R.id.jump:{
                Intent intent2 = new Intent(this,MainActivity2.class);
                startActivity(intent2);
            }
            break;

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(spinnerFlag[0] < 2){//初始化每个spinner会监听一次，需要跳过
            spinnerFlag[0] = spinnerFlag[0] +1;
            return;
        }
        switch (adapterView.getId()){
            case R.id.spinner:{
                String[] arr = getResources().getStringArray(R.array.function);
                function = arr[i];
                funcTv.setText(function);
            }break;
            case R.id.spinner2:{
                String[] arr = getResources().getStringArray(R.array.function2);
                function = arr[i];
                funcTv.setText(function);
            }break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(MainActivity.this, "/??", Toast.LENGTH_SHORT).show();
    }


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

    @Override
    protected void onDestroy() {
        //unbindService(conn);
        //注销广播
        unregisterReceiver(msgReceiver);
        Log.d("test","MainActivity destory");
        super.onDestroy();
    }
}
package com.example.myapplication.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.MainActivity;
import com.example.myapplication.constant.FunctionConstant;
import com.example.myapplication.constant.IconStrConstant;
import com.example.myapplication.dao.MatchResult;
import com.example.myapplication.utils.FunctionExecutor;
import com.example.myapplication.utils.ImgUtils;
import com.example.myapplication.utils.LogUtils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 主要功能：探索 + 结界突破
 */
//探索+突破
public class MyService extends Service {
    //stopself()停止自身服务

    private String Tag = "服务";

    /**
     * startFlag控制线程的结束，数组地址不可改变，在每一个耗时操作或者循环时都需要考虑这个值。
     * 以确保能在服务关闭时，线程也及时停止。
     */
    final int[] startFlag = {0};
    Intent intent2 = new Intent("RECEIVER");


    public MyService() {
    }

    //服务创建时调用
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("服务", "服务创建！");

    }

    private void logToScreen(String str){
        Log.d(Tag, str);
        intent2.putExtra("state", str);
        sendBroadcast(intent2);
    }

    //服务启动时调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //判断启动状态
        if(startFlag[0] == 1){
            Log.d("服务", "重复启动服务！");
            logToScreen("重复启动服务！");
            return super.onStartCommand(intent, flags, startId);
        }else {
            intent2.putExtra("startFlag",true);
            intent2.putExtra("state","服务启动！");
            sendBroadcast(intent2);
            Log.d("服务", "服务启动！");
            LogUtils.saveLog(this,"test服务启动！");
        }
        //获取配置参数
        Bundle config = intent.getExtras();
        FunctionExecutor executor = new FunctionExecutor();
        executor.init(this,startFlag,config,intent2);

        //启动线程，执行脚本
        new Thread(()->{
            try{
                Thread.sleep(2000);
                switch (config.getString("function")){
                    case FunctionConstant.TANSUO:{
                        logToScreen("启动脚本，探索困28");
                        LogUtils.saveLog(this,"启动脚本，探索困28");
                        executor.tansuo();
                    }break;
                    case FunctionConstant.JJTP:{
                        logToScreen("启动脚本，结界突破");
                        LogUtils.saveLog(this,"启动脚本，结界突破");
                        executor.jjtp();
                    }break;
                    case FunctionConstant.TANSUO_JJTP:{
                        logToScreen("启动脚本，结界突破-困28循环");
                        LogUtils.saveLog(this,"启动脚本，结界突破-困28循环");
                        executor.jjtp();
                    }break;
                    case  FunctionConstant.BAIGUI:{
                        logToScreen("启动脚本，百鬼夜行");
                        executor.baigui();
                    }break;
                    case FunctionConstant.YEYUANHUO:{
                        logToScreen("启动脚本，业原火");
                        executor.yeYuanHuo();
                    }break;
                    case FunctionConstant.HUNTU:{
                        logToScreen("启动脚本，魂土");
                        executor.hunTu();
                    }break;
                    default:{
                        logToScreen("该功能尚在开发！");
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }).start();
        startFlag[0] = 1;
        return super.onStartCommand(intent, flags, startId);
    }

    //服务销毁时调用
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("服务", "服务销毁！");
        intent2.putExtra("startFlag",false);
        sendBroadcast(intent2);
        LogUtils.saveLog(this,"脚本停止！");
        startFlag[0] = 0;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
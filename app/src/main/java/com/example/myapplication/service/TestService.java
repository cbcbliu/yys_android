package com.example.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.myapplication.dao.MatchResult;
import com.example.myapplication.utils.ImgUtils;

public class TestService extends Service {
    public TestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    Thread t;

    final int[] startFlag = {0};

    String Tag = "Test服务";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Tag, "服务创建！");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(startFlag[0] == 1){
            Log.d(Tag, "重复启动服务！");
            return super.onStartCommand(intent, flags, startId);
        }else {
            Log.d(Tag, "服务启动！");
        }
        String testTxt = intent.getStringExtra("testTxt");

        t = new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            while(startFlag[0] == 1){

                ImgUtils.getScreen();
                MatchResult img = ImgUtils.findImg(testTxt);
                //ImgUtils.tap(img.getX(),img.getY()-100);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
               }

        });
        t.start();
        startFlag[0] = 1;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Tag, "服务销毁！");
        startFlag[0] = 0;
    }



}
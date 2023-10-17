package com.example.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.myapplication.dao.MatchResult;
import com.example.myapplication.utils.ImgUtils;


//不好用且意义不大，暂时放弃 --2023年10月7日21点13分
public class TeamKun28Service extends Service {
    public TeamKun28Service() {
    }

    Thread t;

    Boolean startThread = false;

    String Tag = "组队探索困28";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Tag, "服务创建！");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(startThread){
            Log.d(Tag, "重复启动服务！");
            return super.onStartCommand(intent, flags, startId);
        }else {
            Log.d(Tag, "服务启动！");
        }

        t = new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            boolean isCaptain = intent.getBooleanExtra("ismaster", false);

            // FuncUtils.jjtp();
            //不好用且意义不大，暂时放弃
            //doService(isCaptain);



        });
        t.start();
        startThread = true;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Tag, "服务销毁！");
        startThread = false;
    }


    private  void doService(boolean isCaptain) throws InterruptedException {

        while(startThread){
            if(isCaptain){
                captain();
            }else{
                normal();
            }

        }
    }

    private void normal() throws InterruptedException { //组队成员

        while (startThread){
            Thread.sleep(1000);

            ImgUtils.getScreen();
            MatchResult teamqr = ImgUtils.findImg("teamup/qr");//接受邀请按钮
            MatchResult zlp = ImgUtils.findImg("zlp");//战利品
            MatchResult shengli = ImgUtils.findImg("shengli");//战斗胜利
            if(teamqr.getSimilarity()>0.9){
                ImgUtils.tap(teamqr.getX(),teamqr.getY());
                continue;
            }
            if(zlp.getSimilarity()>0.9){
                ImgUtils.tap(zlp.getX(),zlp.getY());
                continue;
            }
            if(shengli.getSimilarity()>0.9){
                ImgUtils.tap(shengli.getX(),shengli.getY());
                continue;
            }

            ImgUtils.tap(730,550);//探索点击空白处
            Log.d(Tag, "点击空白处");
        }


    }

    private  void captain()  { //组队队长

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ImgUtils.getScreen();
        MatchResult boss = ImgUtils.findImg("boss");
        if(boss.getSimilarity()>0.9){
            ImgUtils.tap(boss.getX(),boss.getY());
        }
        MatchResult normal = ImgUtils.findImg("normal");
        if(normal.getSimilarity()>0.8){
            ImgUtils.tap(normal.getX(),normal.getY());
        }
        MatchResult zlp = ImgUtils.findImg("zlp");
        MatchResult shengli = ImgUtils.findImg("shengli");
        //MatchResult kun28 = ImgUtils.findImg("kun28");
        //MatchResult tansuo = ImgUtils.findImg("tansuo");
        MatchResult queren = ImgUtils.findImg("queren");
        MatchResult teamtz = ImgUtils.findImg("teamtz");

        if(zlp.getSimilarity()>0.9){
            ImgUtils.tap(zlp.getX(),zlp.getY());
        }
        if(shengli.getSimilarity()>0.9){
            ImgUtils.tap(shengli.getX(),shengli.getY());
        }
        if(queren.getSimilarity()>0.9){
            ImgUtils.tap(queren.getX(),queren.getY());
        }
        if(teamtz.getSimilarity()>0.9){
            ImgUtils.tap(teamtz.getX(),teamtz.getY());
        }
//        if(kun28.getSimilarity()>0.9){
//            ImgUtils.tap(kun28.getX(),kun28.getY());
//        }
//        if(tansuo.getSimilarity()>0.9){
//            ImgUtils.tap(tansuo.getX(),tansuo.getY());
//        }
        ImgUtils.tap(730,550);//探索点击空白处

    }
}
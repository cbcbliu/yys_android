package com.example.myapplication.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.constant.IconStrConstant;
import com.example.myapplication.dao.MatchResult;
import com.example.myapplication.utils.FuncUtils;
import com.example.myapplication.utils.ImgUtils;
import com.example.myapplication.utils.LogUtils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 主要功能：探索 + 结界突破
 */
//探索+突破
public class MyService extends Service {
    //stopself()停止自身服务

    private Thread t;

    private int YYS_STATE = 0;//0:探索突破循环,1:探索,2:突破

    /**
     * 布尔值startThread控制线程的结束，在每一个耗时操作或者循环时都需要考虑这个值。
     * 以确保能在服务关闭时，线程也及时停止。
     */
    private Boolean startThread = false;

    public Boolean getStartThread(){
        return this.startThread;
    }

    private String Tag = "服务";
    private int tpjnum,victorynum,failnum;

    private int tansuoTime,jjtpTime = 0;

    int progress = 0;

    /**
     * 回调接口
     */
    private OnProgressListener onProgressListener;

    /**
     * 注册回调接口的方法，供外部调用
     * @param onProgressListener
     */
    public void setOnProgressListener(OnProgressListener onProgressListener){
        this.onProgressListener = onProgressListener;
    }

    public int getProgress(){
        return progress;
    }

    Intent intent2 = new Intent("RECEIVER");
    Intent intentLog = new Intent("RECEIVER2");

    /**
     * 模拟下载任务，每秒钟更新一次
     */
    public void startDownLoad() {
        new Thread(() -> {
            while (progress < 100) {
                progress += 5;

                ///进度发生变化通知调用方
                //回调方式通知
//                if (onProgressListener != null) {
//                    onProgressListener.onProgress(progress);
//                }
                //广播方式
                //发送Action为RECEIVER的广播
                intent2.putExtra("progress", progress);
                sendBroadcast(intent2);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new Mybinder();
    }

    public class Mybinder extends Binder{
        /**
         * 获取当前service的实例
         * @return
         */
        public MyService getService(){
            return MyService.this;
        }
    }

    //服务创建时调用
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("服务", "服务创建！");

    }

    private void logToScreen(String str){
        intent2.putExtra("state", str);
        sendBroadcast(intent2);
    }

    //服务启动时调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(startThread){
            Log.d("服务", "重复启动服务！");
            logToScreen("重复启动服务！");
            return super.onStartCommand(intent, flags, startId);
        }else {
            intent2.putExtra("startFlag",true);
            sendBroadcast(intent2);
            Log.d("服务", "服务启动！");
            saveLog(LogUtils.generateLog("test服务启动！"));
        }
        tpjnum =  intent.getIntExtra("tpjnum",0);
        victorynum = intent.getIntExtra("victorynum",0);
        failnum = intent.getIntExtra("failnum",0);
        boolean onlyTansuo = intent.getBooleanExtra("onlyTansuo",false);
        boolean onlyJjtp = intent.getBooleanExtra("onlyJjtp",false);
        boolean both = intent.getBooleanExtra("bothRadio",false);

        if (onlyTansuo) {
            YYS_STATE = 1;
            logToScreen("启动脚本，探索困28");
            Log.d(Tag, "启动脚本，探索困28");
            saveLog(LogUtils.generateLog("启动脚本，探索困28"));
        } else if (onlyJjtp) {
            YYS_STATE = 2;
            logToScreen("启动脚本，结界突破");
            Log.d(Tag, "启动脚本，结界突破");
            saveLog(LogUtils.generateLog("启动脚本，结界突破"));
        }else if(both){
            YYS_STATE = 0;
            logToScreen("启动脚本，结界突破-困28循环");
            Log.d(Tag, "启动脚本，结界突破-困28循环");
            saveLog(LogUtils.generateLog("启动脚本，结界突破-困28循环"));

        }
        t = new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Log.d(Tag,tpjnum + ","+victorynum+","+failnum);
            try {
                doService();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
        t.start();
        startThread = true;

        return super.onStartCommand(intent, flags, startId);
    }

    //服务销毁时调用
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("服务", "服务销毁！");
        intent2.putExtra("startFlag",false);
        sendBroadcast(intent2);
        saveLog(LogUtils.generateLog("脚本停止！"));
        startThread = false;

    }

    private void doService() throws InterruptedException {

        while(startThread) { //此循环为最外层循环,周期同本次服务周期
            if(YYS_STATE == 1){//仅探索
                jump();
            }
            /**
             *结界数量共九个，绝对坐标分别为
             * ---------------------------------
             * (308,208),(639+x,208),(972+x,208)
             * (308,344),(639+x,344),(972+x,344)
             * (308,480),(639+x,480),(972+x,480)
             * 横跨：332,垂直：136
             * ----------------------------------
             * 结界的状态：未攻打 --> 战胜(战神后处于不可点击状态)/战败(可继续攻打)
             * 分别设置状态码0,1,2
             */
            Thread.sleep(1000);
            logToScreen("脚本运行中...突破卷:"+tpjnum + ",探索次数:"+tansuoTime+",突破次数"+jjtpTime);
            saveLog(LogUtils.generateLog("脚本运行中...突破卷:"+tpjnum + ",探索次数:"+tansuoTime+",突破次数"+jjtpTime));
            ImgUtils.getScreen();
            MatchResult shengli = ImgUtils.findImg("shengli");//打完3/6/9个后的奖励
            MatchResult tpj0 = ImgUtils.findImg("tpj0");//突破卷为0
            MatchResult jjexit = ImgUtils.findImg("jjexit");
            MatchResult jjtp = ImgUtils.findImg("jjtp");
            MatchResult reject = ImgUtils.findImg("reject");//悬赏拒绝

            //可能干扰的存在，悬赏之类
            if (reject.getSimilarity() > 0.9) {
                ImgUtils.tap(reject.getX(), reject.getY());
            }
            if (shengli.getSimilarity() > 0.9) {
                ImgUtils.tap(shengli.getX(), shengli.getY());
            }

            if(tpj0.getSimilarity() > 0.99){
                tpjnum = 0;
                if(jjexit.getSimilarity()>0.9){
                    Log.d(Tag, "突破卷为0");
                    ImgUtils.tap(jjexit.getX(), jjexit.getY());
                }
                if(YYS_STATE == 0){//探索突破循环
                    Log.d(Tag, "前往探索...");
                    jump();
                }
                continue;
            }

            if(tpjnum < 10){//突破卷不够，前往探索
                if(jjexit.getSimilarity()>0.9){
                    Log.d(Tag, "突破卷不足");
                    ImgUtils.tap(jjexit.getX(), jjexit.getY());
                }
                if(YYS_STATE == 0){//探索突破循环
                    Log.d(Tag, "前往探索...");
                    jump();
                }
                continue;
            }else{//突破卷足够，前往突破
                if(jjtp.getSimilarity()>0.9){
                    ImgUtils.tap(jjtp.getX(), jjtp.getY());
                }
            }

            while ((victorynum + failnum) < 9) { //此循环位于结界突破界面,包含了结界突破战斗界面

                if (!startThread) {
                    break;//服务停止时，线程也需要及时结束
                }
                Thread.sleep(1000);
                logToScreen("结界突破中...突破卷:"+tpjnum + ",探索次数:"+tansuoTime+",突破次数"+jjtpTime);
                Log.d(Tag, "结界突破中... 突破胜利次数:"+ jjtpTime);
                saveLog(LogUtils.generateLog("结界突破中...突破卷:"+tpjnum + ",探索次数:"+tansuoTime+",突破次数"+jjtpTime));

                ImgUtils.getScreen();
                shengli = ImgUtils.findImg("shengli");//打完3/6/9个后的奖励
                tpj0 = ImgUtils.findImg("tpj0");//突破卷为0
                //jjexit = ImgUtils.findImg("jjexit");
                MatchResult jjpanel = ImgUtils.findImg("jjpanel");//判断是否在结界突破界面
                reject = ImgUtils.findImg("reject");//悬赏拒绝
                if (reject.getSimilarity() > 0.9) {
                    ImgUtils.tap(reject.getX(), reject.getY());
                }
                //突破卷为0
                if(tpj0.getSimilarity() > 0.99){
                    break;
                }

                if (shengli.getSimilarity() > 0.9) {
                    ImgUtils.tap(shengli.getX(), shengli.getY());
                }

                if (jjpanel.getSimilarity() > 0.9) {
                    Log.d(Tag, "位于结界突破面板，准备进攻...");
                    ImgUtils.tap(308 + (((victorynum + failnum) % 3) * 332), 208 + ((victorynum + failnum) / 3) * 136);
                    Thread.sleep(800);
                    ImgUtils.getScreen();
                }
                MatchResult jing = ImgUtils.findImg("jing");//进攻按钮
                //进入战斗界面
                if (jing.getSimilarity() > 0.9) {
                    ImgUtils.tap(jing.getX(), jing.getY());
                    Log.d(Tag, "进入结界突破战斗！");
                    //Thread.sleep(1000);//切换页面需等待
                    boolean backDone = false;
                    int failcount = 0;//主动退出失败累计
                    while (startThread) { //此循环位于结界突破战斗界面
                        Thread.sleep(1000);
                        logToScreen("结界突破战斗中...突破卷:"+tpjnum + ",探索次数:"+tansuoTime+",突破次数"+jjtpTime);
                        Log.d(Tag, "结界突破战斗中... 突破胜利次数:"+ jjtpTime);
                        saveLog(LogUtils.generateLog("结界突破战斗中...突破卷:"+tpjnum + ",探索次数:"+tansuoTime+",突破次数"+jjtpTime));

                        ImgUtils.getScreen();
                        MatchResult again = ImgUtils.findImg("again");//战斗失败
                        shengli = ImgUtils.findImg("shengli");//战斗胜利
                        MatchResult exit = ImgUtils.findImg("exit");//退出
                        MatchResult confirm = ImgUtils.findImg("confirm");//退出确认

                        //特殊情况
                        reject = ImgUtils.findImg("reject");//悬赏拒绝
                        if (reject.getSimilarity() > 0.9) {
                            ImgUtils.tap(reject.getX(), reject.getY());
                        }
                        jjexit = ImgUtils.findImg("jjexit");//结界突破面板退出按钮,说明不在战斗界面,需终止循环
                        if(jjexit.getSimilarity() > 0.9){
                            Log.d(Tag,"界面异常,终止循环！");
                            break;
                        }

                        if ((failnum + victorynum) == 8 && !backDone && (failnum + failcount) < 3) {
                            //最后一个结界 且 未输满三次
                            if (exit.getSimilarity() > 0.9) {
                                ImgUtils.tap(exit.getX(), exit.getY());
                                Thread.sleep(600);
                                ImgUtils.getScreen();
                                confirm = ImgUtils.findImg("confirm");//退出确认
                            }
                            if (confirm.getSimilarity() > 0.9) {
                                ImgUtils.tap(confirm.getX(), confirm.getY());
                                backDone = true;
                            }
                        }
                        if (shengli.getSimilarity() > 0.9) {
                            ImgUtils.tap(shengli.getX(), shengli.getY());
                            victorynum++;
                            jjtpTime++;//突破胜利次数累计
                            Log.d(Tag, "结界突破战斗胜利！");
                            break;
                        } else if (again.getSimilarity() > 0.9) {

                            if (failnum + failcount >= 3) {
                                //已经输满三次,不考虑主动退
                                ImgUtils.tap(again.getX(), again.getY() - 100);
                                failnum++;
                                Log.d(Tag, "结界突破战斗失败！");
                                break;
                                //已经输满三次,不考虑主动退
//                                if(failcount>0 && (failnum + failcount)<4){//输的三次可能都是主动战败，所以最后一个有可能是打得过的，需要多打一次
//                                    ImgUtils.tap(again.getX(), again.getY());
//                                    Thread.sleep(600);
//                                    ImgUtils.getScreen();
//                                    confirm = ImgUtils.findImg("confirm");//重新挑战确认
//                                    ImgUtils.tap(confirm.getX(), confirm.getY());
//                                    failcount++;//特殊情况多打一次
//                                }else{
//                                    ImgUtils.tap(again.getX(), again.getY() - 100);
//                                    failnum++;
//                                    Log.d(Tag, "结界突破战斗失败！");
//                                    break;
//                                }
                            }

                            if ((failnum + victorynum) == 8) {
                                //最后一个结界,需主动退输满3次
                                ImgUtils.tap(again.getX(), again.getY());
                                Thread.sleep(800);
                                ImgUtils.getScreen();
                                confirm = ImgUtils.findImg("confirm");//重新挑战确认
                                ImgUtils.tap(confirm.getX(), confirm.getY());
                                backDone = false;//重置退出标志,继续战斗循环
                                failcount++;

                            } else {
                                ImgUtils.tap(again.getX(), again.getY() - 100);//点击后退出战斗
                                failnum++;
                                Log.d(Tag, "结界突破战斗失败！");
                                break;
                            }

                        }

                    }
                }
            }


            //打完九个,刷新
            if ((victorynum + failnum) >= 9 && victorynum < 9) {//打完九个且胜利次数小于9,需手动刷新,且考虑时间是否达到5分钟
                ImgUtils.getScreen();
                MatchResult flush = ImgUtils.findImg("flush");//进攻按钮flsuhconfirm
                MatchResult flsuhconfirm = ImgUtils.findImg("flsuhconfirm");
                if (flush.getSimilarity() > 0.9) {
                    ImgUtils.tap(flush.getX(), flush.getY());
                    Thread.sleep(800);
                    ImgUtils.getScreen();
                    flsuhconfirm = ImgUtils.findImg("flsuhconfirm");
                }
                if (flsuhconfirm.getSimilarity() > 0.9) {
                    ImgUtils.tap(flsuhconfirm.getX(), flsuhconfirm.getY());
                    victorynum = 0;
                    failnum=0;

                }
            }else if((victorynum + failnum) >= 9){
                victorynum = 0;
                failnum=0;
            }
        }
    }

    private void jump(){

        //特殊情况
        //MatchResult jjpanel = ImgUtils.findImg("jjpanel");
        MatchResult jjexit = ImgUtils.findImg("jjexit");//判断是否在结界突破界面
        if(jjexit.getSimilarity()>0.9){
            ImgUtils.tap(jjexit.getX(),jjexit.getY());
        }
        try {
            tansuo();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //困28探索
    public  void tansuo() throws InterruptedException {

        boolean flag = false;//结束标识
        int exceptionTime = 0;//异常次数
        while (startThread){
            logToScreen("探索中...突破卷:"+tpjnum + ",探索次数:"+tansuoTime+",突破次数"+jjtpTime);
            Log.d(Tag, "探索中...突破卷数量为：" + tpjnum + ",探索次数:"+ tansuoTime);
            saveLog(LogUtils.generateLog("探索中...突破卷:"+tpjnum + ",探索次数:"+tansuoTime+",突破次数"+jjtpTime));

            Thread.sleep(1000);
            ImgUtils.getScreen();

            MatchResult reject = ImgUtils.findImg("reject");//悬赏拒绝
            if (reject.getSimilarity() > 0.9) {
                ImgUtils.tap(reject.getX(), reject.getY());
            }

            if(exceptionTime >= 10){//累计十次退出探索重进避免死循环
                MatchResult exit = ImgUtils.findImg("tansuo/exit");
                if(exit.getSimilarity()>0.95){
                    ImgUtils.tap(exit.getX(),exit.getY(),"tansuo/exit");
                    Thread.sleep(800);
                    ImgUtils.getScreen();
                }
                MatchResult exitconfirm = ImgUtils.findImg("tansuo/exitconfirm");
                if(exitconfirm.getSimilarity()>0.95){
                    ImgUtils.tap(exitconfirm.getX(),exitconfirm.getY(),"tansuo/exitconfirm");
                    exceptionTime = 0;
                }
                //continue;
            }

            //MatchResult baox = ImgUtils.findImg("tansuo/baox");//暗图标宝箱
            MatchResult baox2 = ImgUtils.findImg("tansuo/baox2");//图标宝箱
            MatchResult exit = ImgUtils.findImg("jjexit");//红叉按钮通用

            MatchResult boss = ImgUtils.findImg("boss");
            if(boss.getSimilarity()>0.9){
                ImgUtils.tap(boss.getX(),boss.getY(),"boss");
                exceptionTime = 0;
                continue;
            }
            MatchResult normal = ImgUtils.findImg("normal");
            if(normal.getSimilarity()>0.9){
                ImgUtils.tap(normal.getX(),normal.getY(),"normal");
                exceptionTime = 0;
                continue;
            }
            MatchResult zlp = ImgUtils.findImg("zlp");
            MatchResult shengli = ImgUtils.findImg("shengli");
            MatchResult kun28 = ImgUtils.findImg("kun28");
            MatchResult tansuo = ImgUtils.findImg("tansuo");

            if(zlp.getSimilarity()>0.98){
                ImgUtils.tap(zlp.getX(),zlp.getY(),"zlp");
                Thread.sleep(800);
                ImgUtils.tap(730,550);//避免卡住循环
                exceptionTime = 0;
                continue;
            }
            if(shengli.getSimilarity()>0.9){
                Thread.sleep(500);
                ImgUtils.getScreen();
                MatchResult tpj = ImgUtils.findImg("tpj");
                if(tpj.getSimilarity()>0.9){
                    tpjnum++;
                    if(tpjnum >= 10 && YYS_STATE == 0){
                        Log.d(Tag, "突破卷数量已足够，准备结束探索" );
                        flag = true;
                    }
                }
                ImgUtils.tap(shengli.getX(),shengli.getY(),"shengli");
                exceptionTime = 0;
                tansuoTime++;
                continue;
            }
            if(kun28.getSimilarity()>0.9){
                if(baox2.getSimilarity()>0.9){ //点击宝箱
                    ImgUtils.tap(baox2.getX(),baox2.getY(),"baoxiang");
                    continue;
                }
                if(flag){
                    Log.d(Tag, "探索结束" );
                    break;
                }
                ImgUtils.tap(kun28.getX(),kun28.getY(),"kun28");
                exceptionTime = 0;
                continue;
            }
            if(tansuo.getSimilarity()>0.9){
                if(baox2.getSimilarity()>0.9 && exit.getSimilarity()>0.9){ //点击宝箱
                    ImgUtils.tap(exit.getX(), exit.getY(), "exit");
                    Thread.sleep(800);
                    ImgUtils.tap(baox2.getX(),baox2.getY(),"baoxiang");
                    continue;
                }
                if(flag){
                    if(exit.getSimilarity()>0.9){
                        ImgUtils.tap(exit.getX(),exit.getY(),"exit");
                        Log.d(Tag, "探索结束" );
                        break;
                    }
                }
                ImgUtils.tap(tansuo.getX(),tansuo.getY(),"tansuo");
                exceptionTime = 0;
                continue;
            }
            exceptionTime++;//一次循环什么也没点,累计十次则退出探索重进避免死循环
            ImgUtils.tap(730,550);//探索点击空白处
            Log.d(Tag, "点击空白处,累计"+ exceptionTime + "次" );
        }


    }


    public void saveLog(String data){
        Log.d("save", "save:");
        //byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        FileOutputStream out = null;
        try {
            out = openFileOutput("logData", Context.MODE_APPEND);
            out.write(data.getBytes(StandardCharsets.UTF_8));
            //writer = new BufferedWriter(new OutputStreamWriter(out));
            //writer.write(Arrays.toString(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

}
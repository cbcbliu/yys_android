package com.example.myapplication.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.constant.FunctionConstant;
import com.example.myapplication.constant.IconStrConstant;
import com.example.myapplication.dao.MatchResult;

/**
 * 方法执行者
 */
public class FunctionExecutor {

    String Tag = "服务";

    private Service service;
    private int[] startFlag;
    private Bundle config;
    private Intent intent;

    //
    private int victorynum,failnum,tpjnum;
    private int time = 0,jjtpTime = 0;//一般脚本为：某功能-结界突破循环，time为选择功能的运行次数，jjtptime为结界突破次数

    public FunctionExecutor() {

    }

    public void  init(Service service,int[] flag,Bundle config,Intent intent){
        this.tpjnum =  config.getInt("tpjnum",0);
        this.victorynum = config.getInt("victorynum",0);
        this.failnum = config.getInt("failnum",0);
        this.service = service;
        this.startFlag = flag;
        this.config = config;
        this.intent = intent;
    }

    //百鬼夜行
    public void baigui() throws InterruptedException {
        int time = 0;
        while (startFlag[0] == 1){

            Thread.sleep(1000);
            ImgUtils.getScreen();
            MatchResult dou = ImgUtils.findImg("baigui/dou");//豆
            MatchResult qiyue = ImgUtils.findImg("baigui/qiyue");//百鬼结束后点击契约书退出该页面
            MatchResult jinru = ImgUtils.findImg("baigui/jinru");//百鬼入口
            MatchResult kaishi = ImgUtils.findImg("baigui/kaishi");//开始
            MatchResult reject = ImgUtils.findImg("reject");//悬赏拒绝
            if (reject.getSimilarity() > 0.9) {
                ImgUtils.tap(reject.getX(), reject.getY());
            }
            if(jinru.getSimilarity()>0.9){
                ImgUtils.tap(168,612);//点加号邀请好友
                Thread.sleep(1000);
                ImgUtils.tap(502,254);//邀请好友
                Thread.sleep(1000);
                ImgUtils.tap(jinru.getX(), jinru.getY());
                Thread.sleep(200);
                ImgUtils.tap(jinru.getX(), jinru.getY());
                time++;
            }else if(kaishi.getSimilarity()>0.9){
                ImgUtils.tap(1000,450);//砸豆子以及选第三个up
                Thread.sleep(1000);
                ImgUtils.tap(kaishi.getX(), kaishi.getY());
                Thread.sleep(800);
                ImgUtils.swipe(401,659,600,659,200);
            } else if(qiyue.getSimilarity()>0.9) {
                ImgUtils.tap(qiyue.getX(), qiyue.getY());
            }else if(dou.getSimilarity()>0.77 ){//冰冻
                int i = 1;
                if(dou.getSimilarity()>0.9){
                    i=5;
                }
                while (i>0){
                    Thread.sleep(400);
                    ImgUtils.tap(1000,450);
                    i--;
                }

            }
            logToScreen("已砸百鬼"+time+"次");
        }

    }


    /**
     * 业原火
     * @throws InterruptedException
     */
    public void yeYuanHuo() throws InterruptedException {

        while (startFlag[0] == 1){
            Thread.sleep(1000);
            ImgUtils.getScreen();
            MatchResult tz = ImgUtils.findImg(IconStrConstant.YEYUANHUO_TZ);//挑战
            MatchResult victory = ImgUtils.findImg(IconStrConstant.SCENE_VICTORY);//挑战
            MatchResult jjtp = ImgUtils.findImg(IconStrConstant.SCENE_TANSUO);//结界突破入口，判断是否在探索地图
            MatchResult yyh = ImgUtils.findImg(IconStrConstant.YEYUANHUO_YYH);//业原火入口

            if(jjtp.isExist()){
                ImgUtils.tap(179,670);//御魂入口
                Thread.sleep(1000);
            } else if (yyh.isExist()) {
                yyh.tap();//业原火入口
                Thread.sleep(500);
            } else if(tz.isExist()){
                tz.tap();
                time++;
            } else if (victory.isExist()) {
                Thread.sleep(500);//结算界面战利品弹出来需要时间
                ImgUtils.getScreen();
                MatchResult tpj = ImgUtils.findImg(IconStrConstant.TUPOJUAN);//突破卷掉落
                if(tpj.isExist()){
                    tpjnum++;
                    logToScreen("掉落突破卷");
                    if(tpjnum>=10){
                        Thread.sleep(500);
                        logToScreen("突破卷足够,前往突破");
                        jjtp();
                    }
                }
                victory.tap();
            }
            logToScreen("已挑战业原火"+time+"次,突破卷数量:"+tpjnum);
        }
    }

    public void hunTu() throws InterruptedException {
        int time = 0,tpjNum = 0;
        while (startFlag[0] == 1){
            Thread.sleep(1000);
            ImgUtils.getScreen();
            MatchResult tz = ImgUtils.findImg(IconStrConstant.HUNTU_TZ);//挑战
            MatchResult victory = ImgUtils.findImg(IconStrConstant.SCENE_VICTORY);//胜利结算
            MatchResult victory2 = ImgUtils.findImg(IconStrConstant.VICTORY2);//胜利结算2
            if(tz.isExist()){
                tz.tap();
                time++;
            } else if (victory2.isExist()) {
                victory2.tap();
            } else if (victory.isExist()) {
                Thread.sleep(500);//结算界面战利品弹出来需要时间
                ImgUtils.getScreen();
                MatchResult tpj = ImgUtils.findImg(IconStrConstant.TUPOJUAN);//突破卷掉落
                if(tpj.isExist()){
                    tpjNum++;
                    logToScreen("掉落突破卷");
                }
                victory.tap();
            }
            logToScreen("已挑战魂土"+time+"次,共计掉落突破卷"+tpjNum);
        }
    }

    private void logToScreen(String str){
        intent.putExtra("state", str);
        service.sendBroadcast(intent);
    }


    //困28探索
    public void tansuo() throws InterruptedException {

        int tpjnum =  config.getInt("tpjnum");
        int exceptionTime = 0;//异常次数
        String function = config.getString("function");
        boolean flag = false;//结束标识
        while (startFlag[0] == 1){

            Thread.sleep(1000);
            logToScreen("探索中...突破卷:"+tpjnum + ",探索次数:"+time + ",突破次数:"+jjtpTime);
            LogUtils.saveLog(service,"探索中...突破卷:"+tpjnum + ",探索次数:"+time+ ",突破次数:"+jjtpTime);
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
                Thread.sleep(500);//结算界面战利品弹出来需要时间
                ImgUtils.getScreen();
                MatchResult tpj = ImgUtils.findImg("tpj");
                if(tpj.getSimilarity()>0.9){
                    tpjnum++;
                    if(tpjnum >= 10 && !function.equals(FunctionConstant.TANSUO)){
                        //当脚本功能为探索突破循环时，需要退出
                        Log.d(Tag, "突破卷数量已足够，准备结束探索" );
                        flag = true;
                    }
                }
                ImgUtils.tap(shengli.getX(),shengli.getY(),"shengli");
                exceptionTime = 0;
                time++;
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


    //结界突破
    public void jjtp() throws InterruptedException {

        int exitTime = config.getInt("exitTime",3);//结界突破退出次数
        String function = config.getString("function");
        String loopFunc = "";//与结界突破循环的功能
        boolean isLoopFunc = true;
        if(function.equals(FunctionConstant.TANSUO_JJTP)){
            loopFunc = FunctionConstant.TANSUO;
        } else if (function.equals(FunctionConstant.JJTP)) {
            isLoopFunc = false;
        }else {
            loopFunc = function;
        }


        while(startFlag[0] == 1) { //最外层循环
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
            //tpjnum--;
            logToScreen("结界突破中...突破卷:"+tpjnum +",突破次数"+jjtpTime+(isLoopFunc?(","+loopFunc+"次数:"+time):"" ));
            LogUtils.saveLog(service,"结界突破中...突破卷:"+tpjnum + ",突破次数"+jjtpTime+(isLoopFunc?(","+loopFunc+"次数:"+time):"" ));
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

            if(tpj0.getSimilarity() > 0.99){//突破卷为0，前往探索
                tpjnum = 0;
                if(jjexit.getSimilarity()>0.9){
                    Log.d(Tag, "突破卷为0");
                    logToScreen("突破卷为0");
                    ImgUtils.tap(jjexit.getX(), jjexit.getY());
                }
                if(!function.equals(FunctionConstant.JJTP)){//探索突破循环
                    Thread.sleep(500);
                    Log.d(Tag, "结束突破...");
                    logToScreen("突破卷不足，结束突破");
                    jump();
                }
                continue;
            }

            if(tpjnum < 10){//突破卷不够，前往探索
                if(jjexit.getSimilarity()>0.9){
                    Log.d(Tag, "突破卷不足");
                    logToScreen("突破卷不足");
                    ImgUtils.tap(jjexit.getX(), jjexit.getY());
                }
                if(!function.equals(FunctionConstant.JJTP)){//探索突破循环
                    Thread.sleep(500);
                    Log.d(Tag, "结束突破...");
                    logToScreen("突破卷不足，结束突破");
                    jump();
                }
                continue;
            }else{//突破卷足够，前往突破
                if(jjtp.getSimilarity()>0.9){
                    ImgUtils.tap(jjtp.getX(), jjtp.getY());
                }
            }

            while ((victorynum + failnum) < 9) { //此循环位于结界突破界面,包含了结界突破战斗界面

                if (startFlag[0] == 0) {
                    break;//服务停止时，线程也需要及时结束
                }
                Thread.sleep(1000);
                logToScreen("结界突破中...突破卷:"+tpjnum + ",突破次数"+jjtpTime+(isLoopFunc?(","+loopFunc+"次数:"+time):"" ));
                LogUtils.saveLog(service,"结界突破中...突破卷:"+tpjnum + ",突破次数"+jjtpTime+(isLoopFunc?(","+loopFunc+"次数:"+time):"" ));

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
                }else{//进入结界突破
                    jjtp = ImgUtils.findImg("jjtp");
                    if(jjtp.getSimilarity()>0.9){
                        ImgUtils.tap(jjtp.getX(), jjtp.getY());
                    }
                }
                MatchResult jing = ImgUtils.findImg("jing");//进攻按钮
                //进入战斗界面
                if (jing.getSimilarity() > 0.9) {
                    ImgUtils.tap(jing.getX(), jing.getY());
                    Log.d(Tag, "进入结界突破战斗！");
                    boolean backDone = false;//退出标志
                    int failcount = 0;//主动退出失败累计
                    while (startFlag[0] == 1) { //此循环位于结界突破战斗界面
                        Thread.sleep(1000);
                        logToScreen("结界突破战斗中...突破卷:"+tpjnum + ",突破次数"+jjtpTime+(isLoopFunc?(","+loopFunc+"次数:"+time):"" ));
                        LogUtils.saveLog(service,"结界突破战斗中...突破卷:"+tpjnum +",突破次数"+jjtpTime+(isLoopFunc?(","+loopFunc+"次数:"+time):"" ));

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

                        if ((failnum + victorynum) == 8 && !backDone && (failnum + failcount) < exitTime) {
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
                            //胜利
                            ImgUtils.tap(shengli.getX(), shengli.getY());
                            victorynum++;
                            jjtpTime++;//突破胜利次数累计
                            logToScreen("结界突破战斗胜利！");
                            break;
                        } else if (again.getSimilarity() > 0.9) {
                            //失败
                            if (failnum + failcount >= exitTime) {
                                //已经输满三次,不考虑主动退
                                ImgUtils.tap(again.getX(), again.getY() - 100);
                                failnum++;
                                logToScreen("结界突破战斗失败！");
                                break;

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
                                logToScreen("未输满三次");

                            } else {
                                ImgUtils.tap(again.getX(), again.getY() - 100);//点击后退出战斗
                                failnum++;
                                logToScreen("结界突破战斗失败！");
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

    private void jump() throws InterruptedException {

        //特殊情况
        MatchResult jjexit = ImgUtils.findImg("jjexit");//判断是否在结界突破界面
        if(jjexit.getSimilarity()>0.9){
            ImgUtils.tap(jjexit.getX(),jjexit.getY());
        }
        switch (config.getString("function")){
            case FunctionConstant.TANSUO_JJTP:{
                tansuo();
            }break;
            case FunctionConstant.YEYUANHUO:{
                yeYuanHuo();
            }
        }

    }

    //测试
    public void testfind(String findname){

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ImgUtils.getScreen();
            ImgUtils.findImg(findname);

    }

}

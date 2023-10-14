package com.example.myapplication.utils;

import android.util.Log;

import com.example.myapplication.dao.MatchResult;

public class FuncUtils {

    //百鬼夜行
    public static void baigui( ) throws InterruptedException {

        ImgUtils.getScreen();
        MatchResult dou = ImgUtils.findImg("baigui/dou");//豆
        MatchResult qiyue = ImgUtils.findImg("baigui/qiyue");//百鬼结束后点击契约书退出该页面
        MatchResult jinru = ImgUtils.findImg("baigui/jinru");//百鬼入口
        MatchResult kaishi = ImgUtils.findImg("baigui/kaishi");//开始

        if(jinru.getSimilarity()>0.9){
            ImgUtils.tap(jinru.getX(), jinru.getY());
        }else if(kaishi.getSimilarity()>0.9){
            ImgUtils.tap(1000,500);//砸豆子以及选第三个up
            Thread.sleep(1000);
            ImgUtils.tap(kaishi.getX(), kaishi.getY());
        } else if(qiyue.getSimilarity()>0.9) {
            ImgUtils.tap(qiyue.getX(), qiyue.getY());
        }else if(dou.getSimilarity()>0.77 ){
            int i = 10;
            while (i>0){
                Thread.sleep(200);
                ImgUtils.tap(1000,500);
                i--;
            }

        }


    }

    //困28探索
    public static void tansuo()  {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ImgUtils.getScreen();
            MatchResult normal = ImgUtils.findImg("normal");
            if(normal.getSimilarity()>0.8){
                ImgUtils.tap(normal.getX(),normal.getY());
            }
            MatchResult boss = ImgUtils.findImg("boss");
            if(boss.getSimilarity()>0.9){
                ImgUtils.tap(boss.getX(),boss.getY());
            }

            MatchResult zlp = ImgUtils.findImg("zlp");
            MatchResult shengli = ImgUtils.findImg("shengli");
            MatchResult kun28 = ImgUtils.findImg("kun28");
            MatchResult tansuo = ImgUtils.findImg("tansuo");

            if(zlp.getSimilarity()>0.9){
                ImgUtils.tap(zlp.getX(),zlp.getY());
            }
            if(shengli.getSimilarity()>0.9){
                ImgUtils.tap(shengli.getX(),shengli.getY());
            }
            if(kun28.getSimilarity()>0.9){
                ImgUtils.tap(kun28.getX(),kun28.getY());
            }
            if(tansuo.getSimilarity()>0.9){
                ImgUtils.tap(tansuo.getX(),tansuo.getY());
            }
            ImgUtils.tap(730,550);//探索点击空白处

    }


    //结界突破
    public static void jjtp(){
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
        int i=0;
        while (i<9){

            ImgUtils.tap(308+((i% 3) * 332),208 + (i / 3) * 136);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ImgUtils.tap(308,208);
            i++;
        }



    }

    //测试
    public static void testfind(String findname){

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ImgUtils.getScreen();
            ImgUtils.findImg(findname);

    }

}

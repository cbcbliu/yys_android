package com.example.myapplication.utils;

import com.example.myapplication.constant.IconStrConstant;

import java.io.DataOutputStream;

public class AdbUtils {

    public static void reboot(){
        runAdb("reboot");//重启模拟器
    }

    public static void endOnmyoji(){
        runAdb("am force-stop com.netease.onmyoji.wyzymnqsd_cps");//关闭阴阳师应用
    }

    public static void rebootOnyoji(){
        endOnmyoji();//c重启阴阳师应用
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ImgUtils.findImg(IconStrConstant.YYS_ICON).tap();
    }


    private static void runAdb(String str){
        try{
            Process sh = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(sh.getOutputStream());
            os.writeBytes(str);
            os.flush();
            os.close();
            sh.waitFor();
            sh.destroy();
        }catch (Exception e){
            //Log.e(e.getMessage());
        }
    }

}

package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.myapplication.constant.IconStrConstant;
import com.example.myapplication.dao.MatchResult;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class ImgUtils {
    //截屏
    public static void getScreen() {
        //Log.d("截图", "截图开始");
        String mSavedPath = Environment.getExternalStorageDirectory() + File.separator + "yys/screenshot.png";
        //Log.d("截图存储路径", mSavedPath);
        long start = System.currentTimeMillis();
        try{
            Process sh = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(sh.getOutputStream());
            os.writeBytes("screencap -p "+ mSavedPath );
            os.flush();
            os.close();
            sh.waitFor();
            sh.destroy();
        }catch (Exception e){
            Log.e("截图","截图异常"+e.getMessage());
        }
        Log.d("截图", "截图时间："+ (System.currentTimeMillis() - start));
    }

    public static MatchResult findImg(String findimg){
       // Bitmap screencap = BitmapFactory.decodeFile("/storage/emulated/0/yys/screenshot.png");
       // Bitmap target = BitmapFactory.decodeFile("/storage/emulated/0/yys/jjtp.png");
        //getScreen();
//        if(findimg.equals("normal") || findimg.equals("boss")){
//            getScreen();
//        }
        long start = System.currentTimeMillis();
        Mat screencap = Imgcodecs.imread("/storage/emulated/0/yys/screenshot.png");
        Mat target = Imgcodecs.imread("/storage/emulated/0/yys/"+ findimg +".png");

        Mat outputImg = new Mat(screencap.rows(),screencap.cols(),screencap.type());
        Imgproc.matchTemplate(target,screencap,outputImg,Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult res =  Core.minMaxLoc(outputImg);
        Double similarity =  res.maxVal;//匹配度
        Point matchLoc = res.maxLoc;//坐标
        int x = (int)matchLoc.x;
        int y = (int)matchLoc.y;

        if(null != target){
            x += target.cols()/2;
            y += target.rows()/2;
        }
        Log.d("目标匹配"+findimg,"分辨率："+target.cols() + "x"+target.rows()+",匹配度:"+ similarity + ",坐标:X:"+x+",Y:"+y +",匹配时间:"+(System.currentTimeMillis() - start));
        return new MatchResult(x,y,similarity);
    }

    /**
     * 点击
     * @param x
     * @param y
     */
    public static void  tap(int x ,int y)  {

        try{
            Process sh = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(sh.getOutputStream());
            os.writeBytes("input tap "+x+" "+y );
            os.flush();
            os.close();
            sh.waitFor();
            sh.destroy();
        }catch (Exception e){
            //Log.e(e.getMessage());
        }
        Log.d("点击", "点击："+x+","+y);
    }

    //需要log出所点击名字
    public static void  tap(int x ,int y,String name){
        try{
            Process sh = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(sh.getOutputStream());
            os.writeBytes("input tap "+x+" "+y );
            os.flush();
            os.close();
            sh.waitFor();
            sh.destroy();
        }catch (Exception e){
            //Log.e(e.getMessage());
        }
        Log.d("点击", "点击"+ name +"："+x+","+y);
    }

    /**
     * 判断所在场景
     */
    public static MatchResult DetermineScene(){
        long start = System.currentTimeMillis();
        //截屏
        getScreen();
        String sence = "";

        //困28内的怪物会移动，所以得优先处理
        MatchResult normal = findImg("normal");
        if(normal.getSimilarity()>0.8){
            Log.d("判断所在场景", "时间："+ (System.currentTimeMillis() - start));
            normal.setScene(IconStrConstant.SCENE_KUN28);
            return normal;
        }
        MatchResult boss = findImg("boss");
        if(boss.getSimilarity()>0.9){
            Log.d("判断所在场景", "时间："+ (System.currentTimeMillis() - start));
            boss.setScene(IconStrConstant.SCENE_KUN28);
            return boss;
        }


        MatchResult zlp = findImg("zlp");
        MatchResult shengli = findImg("shengli");
        MatchResult kun28 = findImg("kun28");
        MatchResult tansuo = findImg("tansuo");

        if(zlp.getSimilarity()>0.9){
            Log.d("判断所在场景", "时间："+ (System.currentTimeMillis() - start));
            zlp.setScene(IconStrConstant.SCENE_KUN28);
            return zlp;
        }
        if(shengli.getSimilarity()>0.9){
            Log.d("判断所在场景", "时间："+ (System.currentTimeMillis() - start));
            shengli.setScene(IconStrConstant.SCENE_VICTORY);
            return shengli;
        }
        if(kun28.getSimilarity()>0.9){
            Log.d("判断所在场景", "时间："+ (System.currentTimeMillis() - start));
            kun28.setScene(IconStrConstant.SCENE_TANSUO);
            return kun28;
        }
        if(tansuo.getSimilarity()>0.9){
            Log.d("判断所在场景", "时间："+ (System.currentTimeMillis() - start));
            tansuo.setScene(IconStrConstant.SCENE_BEFORE_KUN28);
            return tansuo;
        }

        Log.d("判断所在场景", "时间："+ (System.currentTimeMillis() - start));

        return null;
    }
}

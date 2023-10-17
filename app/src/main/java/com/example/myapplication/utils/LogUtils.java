package com.example.myapplication.utils;

import android.app.Service;
import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {


    public static String generateLog(String logStr){

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String format = simpleDateFormat.format(date);

        return format + "：" + logStr + "\n";

    }

    public static void saveLog(Service service,String data){
        Log.d("save", "save:");
        //byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        String log = generateLog(data);
        FileOutputStream out = null;
        try {
            out = service.openFileOutput("logData", Context.MODE_APPEND);
            out.write(log.getBytes(StandardCharsets.UTF_8));
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

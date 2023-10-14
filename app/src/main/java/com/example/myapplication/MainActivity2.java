package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.service.MyService;
import com.example.myapplication.service.TeamKun28Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 界面2,日志
 */
public class MainActivity2 extends AppCompatActivity {

    Boolean flushFlag = true;
    TextView logTxt;
    private BroadcastReceiver receiver;

    int rows = 0;//日志最新行

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.d("test", "MainActivity2 create");
        Button back = findViewById(R.id.back);
        Button clear = findViewById(R.id.clear);
        Button flush = findViewById(R.id.flush);
        logTxt = findViewById(R.id.tv1);
        initLog();
        int[] i ={0};


        back.setOnClickListener(v -> {
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtras(getIntent().getExtras());
//            startActivity(intent);//这个方法会重新启动原来的页面(即调用oncreate方法)
            finish();
        });

        clear.setOnClickListener(v -> {
            logTxt.setText("");
            clearLog();
        });

        flush.setOnClickListener(v -> {
            flushLog();
//            handler.post(()->{
//                logTxt.setText("fdsfsadfsadf\nfwefwefwef");
//
//                });
        });

    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    private void initLog(){
        Log.d("test", "initLog");
        rows = 0;
        try {
            FileInputStream input = openFileInput("logData");
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = br.readLine()) != null) {
                Log.d("test", "reading" + (rows++));
                logTxt.append(line + "\n");
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void flushLog(){
        Log.d("test", "flushLog");
        int i = 0;
        try {
            FileInputStream input = openFileInput("logData");
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = br.readLine()) != null) {
                Log.d("test", "reading" + (i++));
                if(i>rows){
                    rows = i;
                    logTxt.append(line + "\n");
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onDestroy() {
        Log.d("test", "MainActivity2 onDestroy");
        flushFlag = false;

        super.onDestroy();
    }

    private void clearLog() {
        FileOutputStream out = null;
        try {
            out = openFileOutput("logData", Context.MODE_PRIVATE);
            out.write("".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}

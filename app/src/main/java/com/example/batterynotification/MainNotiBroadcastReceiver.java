package com.example.batterynotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class MainNotiBroadcastReceiver extends BroadcastReceiver {
    private long start_time, end_time;
    private String appName;
    @Override
    public void onReceive(Context context, Intent intent){
        start_time = intent.getLongExtra("time", 0);
        end_time = System.currentTimeMillis();
        appName = intent.getStringExtra("processname");
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/simulation.txt";
        try{
            File file = new File(file_path);
            if(!file.exists()) file.createNewFile();
            FileWriter writer = new FileWriter(file_path, true);
            long duration = (end_time - start_time) / 1000;
            writer.write(appName + " " + Long.toString(duration) + "\n");
            writer.flush();
            writer.close();
        } catch (Exception e){
            Log.e("mytag", e.toString());
            e.printStackTrace();
        }
    }
}

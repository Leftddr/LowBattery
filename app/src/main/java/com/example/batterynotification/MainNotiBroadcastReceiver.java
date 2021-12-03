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
        String folder_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.batterynotification/";
        String file_path = folder_path + "simulation.txt";
        Log.e("file", file_path);
        try{
            File folder = new File(folder_path);
            File file = new File(file_path);
            if(!folder.exists()) folder.mkdirs();
            if(!file.exists()) file.createNewFile();
            FileWriter writer = new FileWriter(file_path, true);
            long duration = (end_time - start_time) / 1000;
            writer.write(appName + " " + Long.toString(duration) + "\n");
            writer.flush();
            writer.close();
            Log.e("file write", "success");
        } catch (Exception e){
            Log.e("mainNoti error", e.toString());
            e.printStackTrace();
        }
    }
}

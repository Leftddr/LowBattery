package com.example.batterynotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
/*
    battery, memory usage, cpu usage, process, service
*/
public class MainActivity extends AppCompatActivity {
    protected BatteryReceiver batterReceiver;
    protected LockBatteryReceiver lockBatteryReceiver;
    protected ColoredBatteryReceiver coloredBatteryReceiver;
    protected PopupReceiver popupReceiver;
    ////////////////////////////////////////////////////////////
    protected Intent batteryintent = new Intent("com.example.lowbattery");
    ////////////////////////////////////////////////////////////
    protected Intent lockbatteryintent = new Intent("com.example.locklowbattery");
    protected Intent coloredbatteryintent = new Intent("com.example.coloredlowbattery");
    protected Intent popupintent = new Intent("com.example.popupbattery");
    protected TypedArray typedArray;
    protected FrameLayout frameLayout;
    protected final int process_threshold = 10;
    protected List<ActivityManager.RunningServiceInfo> rs;
    protected List<ActivityManager.RunningAppProcessInfo> rp;
    protected int totalMemory, totalCpu, memoryUsed;
    protected double cpuUsed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        batterReceiver = new BatteryReceiver(this, "배터리 부족", "핸드폰의 배터리 잔량이 20% 남음");
        IntentFilter intentFilter1 = new IntentFilter("com.example.lowbattery");
        registerReceiver(batterReceiver, intentFilter1);
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        lockBatteryReceiver = new LockBatteryReceiver(this, "dfsfsfs", "You need to charge your battey");
        IntentFilter intentFilter2 = new IntentFilter("com.example.locklowbattery");
        registerReceiver(lockBatteryReceiver, intentFilter2);

        coloredBatteryReceiver = new ColoredBatteryReceiver(this, "coloredLowBattery", "You need to charge your battey");
        IntentFilter intentFilter3 = new IntentFilter("com.example.coloredlowbattery");
        registerReceiver(coloredBatteryReceiver, intentFilter3);

        popupReceiver = new PopupReceiver(this, "배터리 부족", "예상사용 시간은 20분입니다.");
        IntentFilter intentFilter4 = new IntentFilter("com.example.popupbattery");
        registerReceiver(popupReceiver, intentFilter4);

        typedArray = getResources().obtainTypedArray(R.array.border);
        frameLayout = findViewById(R.id.main);

        Intent intentBattery = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = intentBattery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intentBattery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
        int batteryLeft = (int)batteryPct * 100;

        //if(batteryLeft >= 10) sendBroadcast(batteryintent);
        //if(batteryLeft >= 20) sendBroadcast(lockbatteryintent);
        //sendBroadcast(coloredbatteryintent);
        //sendBroadcast(popupintent);
        //if(batteryLeft >= 100) setEdgeEffect();
        int retval = serviceList();
        getCpuMemoryUsage();
    }

    public void setEdgeEffect(){
        /*
        for(int i = 0 ; i < typedArray.length() ; i++){
            LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(getApplicationContext(), typedArray.getResourceId(i, -1));
            GradientDrawable gradientDrawable = (GradientDrawable) drawable.findDrawableByLayerId(R.id.border1);
            gradientDrawable.setColor(Color.BLACK);
        }
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                int level = 0, cri = 1;
                for(int i = 0 ; i < 100 ; i++){
                    String num = Integer.toString(level);
                    if(level >= 10) {level = 9; cri *= -1;}
                    if(level < 0) {level = 0; cri *= -1;}
                    frameLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), typedArray.getResourceId(level, -1)));
                    level += cri;
                    try{
                        Thread.sleep(50);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public int serviceList(){
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        rs = am.getRunningServices(1000);
        rp = am.getRunningAppProcesses();
        return rs.size() + rp.size();
    }

    public void getCpuMemoryUsage(){
        Runtime runtime = Runtime.getRuntime();
        Process process;
        String cmd = "top -n 1";
        memoryUsed = 0;
        cpuUsed = 0.0;

        try {
            process = runtime.exec(cmd);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    String segs[] = line.trim().split("[ ]+");
                    printStats(segs);
                    for(int j = 0 ; j < segs.length ; j++) {
                        if(j > 0 && segs[j - 1].equals("Mem:")) {
                            totalMemory = Integer.parseInt(segs[j].substring(0, segs[j].length() - 1));
                        }
                        if(segs[j].length() >= 7 && segs[j].contains("%cpu")){
                            totalCpu = Integer.parseInt(segs[j].substring(0, segs[j].length() - 4));
                        }
                    }
                    for(int i = 0 ; i < rs.size() ; i++) {
                        if (segs[0].equalsIgnoreCase(Integer.toString(rs.get(i).pid))) {
                            String memoryused = segs[5].substring(0, segs[5].length() - 1);
                            memoryUsed += (Float.parseFloat(memoryused) * 1024);
                            cpuUsed += (Float.parseFloat(segs[8]));
                            Log.e("mytag", Integer.toString(memoryUsed));
                            break;
                        }
                    }
                    for(int i = 0 ; i < rp.size() ; i++){
                        if (segs[0].equalsIgnoreCase(Integer.toString(rp.get(i).pid))) {
                            String memoryused = segs[5].substring(0, segs[5].length() - 1);
                            memoryUsed += (Float.parseFloat(memoryused) * 1024);
                            cpuUsed += (Float.parseFloat(segs[8]));
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("mytag", e.toString());
            }

        } catch (Exception e){
            e.printStackTrace();
            Log.e("mytag", e.toString());
        }
    }

    public void printStats(String segs[]){
        String strs = "";
        for(String str : segs) strs += str + " ";
        Log.e("Stats", strs);
    }
}
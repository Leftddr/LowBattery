package com.example.batterynotification;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/*
    battery, memory usage, cpu usage, process, service
*/
public class MainActivity extends AppCompatActivity {
    protected BatteryReceiver batterReceiver;
    protected LockBatteryReceiver lockBatteryReceiver;
    protected ColoredBatteryReceiver coloredBatteryReceiver;
    protected MainNotiBroadcastReceiver mainNotiBroadcastReceiver;
    protected PopupReceiver popupReceiver;
    ////////////////////////////////////////////////////////////
    protected Intent batteryintent = new Intent("com.example.lowbattery");
    ////////////////////////////////////////////////////////////
    protected Intent lockbatteryintent = new Intent("com.example.locklowbattery");
    protected Intent coloredbatteryintent = new Intent("com.example.coloredlowbattery");
    protected Intent popupintent = new Intent("com.example.popupbattery");
    protected Intent mainIntent = new Intent("android.intent.action.Battery");
    protected TypedArray typedArray;
    protected FrameLayout frameLayout;
    protected final int process_threshold = 10;
    protected double totalMemory, totalCpu, memoryUsed;
    protected double cpuUsed;
    private Button logButton;
    private TextView textView;
    String total = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logButton = (Button)findViewById(R.id.mainbutton);
        textView = (TextView)findViewById(R.id.maintext);
        logButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        String log = readLog();
                        textView.setText(log);
                    }
                }
        );
        getPermissions();
        /////////////////////////////////////////////////////////////////////s///////////////////////////////////////
        mainNotiBroadcastReceiver = new MainNotiBroadcastReceiver();
        IntentFilter intentFilter0 = new IntentFilter("android.intent.action.Battery");
        registerReceiver(mainNotiBroadcastReceiver, intentFilter0);
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
        //frameLayout = findViewById(R.id.main);

        requestUsageStatsPermission();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Intent intentBattery = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                        int level = intentBattery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        int scale = intentBattery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                        float batteryPct = level / (float) scale;
                        int batteryLeft = (int)(batteryPct * 100);
                        Log.e("err", String.valueOf(batteryLeft));
                        if(batteryLeft > 30) {
                            Thread.sleep(1000);
                            continue;
                        }
                        ArrayList<Pair> runnings = getTask();
                        int min_ = Math.min(runnings.size(), 3);
                        ArrayList<Pair> ps = new ArrayList<>();
                        double total_time = 0.0;
                        int percent[] = new int[min_];
                        for(int i = 0 ; i < min_ ; i++) total_time += runnings.get(i).totalTimeForeGround;
                        Log.e("meeeee", String.valueOf(total_time));
                        for(int i = 0 ; i < min_ ; i++) {
                            double num = Double.valueOf((runnings.get(i).totalTimeForeGround) / total_time) * 100;
                            percent[i] = (int) Math.round(num);
                        }
                        coloredbatteryintent.putExtra("count", min_);
                        String process = "process";
                        for(int i = 0 ; i < min_ ; i++) {
                            String newProcessName = process + Integer.toString(i);
                            String processName = "";
                            processName = runnings.get(i).processName;
                            // intent에 processName을 넘기기만 하면 된다. => com.example.batterynotification.
                            coloredbatteryintent.putExtra(newProcessName, processName);
                            coloredbatteryintent.putExtra(newProcessName + "percent", percent[i]);
                        }
                        long now = System.currentTimeMillis();
                        coloredbatteryintent.putExtra("time", now);
                        sendBroadcast(coloredbatteryintent);
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }).start();
    }

    void getPermissions(){
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            }
            return;
        }
    }

    String readLog(){
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/simulation.txt";
        File file = new File(file_path);
        if(!file.exists()) return "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) buffer.append(line + "\n");
            reader.close();
            return buffer.toString();
        } catch (Exception e){
            Log.e("error", e.toString());
            e.printStackTrace();
            return "";
        }
    }

    private ArrayList<Pair> getTask() {
        ArrayList<Pair> currentApp = new ArrayList<>();
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager)this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  0, time);
            // appList안에 있는 stats을 읽어오면서 각종 필요한 함수를 사용한다.
            // 함수는 보시면 의미 이해 하실 겁니다.
            for(UsageStats stats : appList){
                Pair pair = new Pair(stats.getPackageName(), stats.getLastTimeStamp(), stats.getLastTimeUsed(), stats.getTotalTimeInForeground());
                currentApp.add(pair);
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
        }
        Collections.sort(currentApp);
        return currentApp;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    void requestUsageStatsPermission() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }
}
package com.example.batterynotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    protected BatteryReceiver batterReceiver;
    protected LockBatteryReceiver lockBatteryReceiver;
    protected ColoredBatteryReceiver coloredBatteryReceiver;
    protected Intent batteryintent = new Intent("com.example.lowbattery");
    protected Intent lockbatteryintent = new Intent("com.example.locklowbattery");
    protected Intent coloredbatteryintent = new Intent("com.example.coloredlowbattery");
    protected TypedArray typedArray;
    protected FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batterReceiver = new BatteryReceiver(this);
        IntentFilter intentFilter1 = new IntentFilter("com.example.lowbattery");
        registerReceiver(batterReceiver, intentFilter1);

        lockBatteryReceiver = new LockBatteryReceiver(this);
        IntentFilter intentFilter2 = new IntentFilter("com.example.locklowbattery");
        registerReceiver(lockBatteryReceiver, intentFilter2);

        coloredBatteryReceiver = new ColoredBatteryReceiver(this);
        IntentFilter intentFilter3 = new IntentFilter("com.example.coloredlowbattery");
        registerReceiver(lockBatteryReceiver, intentFilter3);

        typedArray = getResources().obtainTypedArray(R.array.border);
        frameLayout = findViewById(R.id.main);

        Intent intentBattery = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = intentBattery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intentBattery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
        int batteryLeft = (int)batteryPct * 100;

        //if(batteryLeft >= 100) sendBroadcast(batteryintent);
        //if(batteryLeft >= 100) sendBroadcast(lockbatteryintent);
        if(batteryLeft >= 100) sendBroadcast(coloredbatteryintent);
        //if(batteryLeft >= 100) setEdgeEffect();
    }

    public void setEdgeEffect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int level = 0, cri = 1;
                for(int i = 0 ; i < 50 ; i++){
                    String num = Integer.toString(level);
                    Log.e("mytag", num);
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
}
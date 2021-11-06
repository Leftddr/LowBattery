package com.example.batterynotification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ColoredBatteryReceiver extends BroadcastReceiver {
    private static final String channelId = "coloredbatterychannel";
    private static final String channelName = "coloredbatterychannel";
    private static final int id = 1;
    private RemoteViews notificationLayout;
    private RemoteViews notificationLayoutExpanded;
    private Context mContext;
    private static String title;
    private static String content;
    protected List<ActivityManager.RunningServiceInfo> rs;
    protected List<ActivityManager.RunningAppProcessInfo> rp;
    protected int totalMemory, totalCpu, memoryUsed;
    protected double cpuUsed;
    private String GROUP_KEY_WORK_EMAIL = "com.example.notigroup";

    public ColoredBatteryReceiver(Context context, String title, String content){
        super();
        mContext = context;
        notificationLayout = new RemoteViews(mContext.getPackageName(), R.layout.notification_small);
        notificationLayoutExpanded = new RemoteViews(mContext.getPackageName(), R.layout.notification_small);
        this.title = title;
        this.content = content;
    }
    public void createNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        for(int i = 0 ; i < 3 ; i++) {
            Notification newMessageNotification = new NotificationCompat.Builder(mContext, channelId)
                    .setContentTitle(title)
                    .setOnlyAlertOnce(true)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.kak)
                    .setColorized(true)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .build();
            notificationManager.notify(i + 1, newMessageNotification);
        }

        Notification summaryNotification =
                new NotificationCompat.Builder(mContext, channelId)
                        .setContentTitle("Summary")
                        //set content text to support devices running API level < 24
                        .setContentText("Two new messages")
                        .setSmallIcon(R.drawable.kakao)
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("Alex Faarborg  Check this out")
                                .addLine("Jeff Chang    Launch Party")
                                .setBigContentTitle("2 new messages")
                                .setSummaryText("janedoe@example.com"))
                        //specify which group this notification belongs to
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();

        notificationManager.notify(0, summaryNotification);
    }
    @Override
    public void onReceive(Context context, Intent intent){
        serviceList();
        getCpuMemoryUsage();
        createNotification();
    }

    public int serviceList(){
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
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
                    for(int j = 0 ; j < segs.length ; j++) {
                        if(j > 0 && segs[j - 1].equals("Mem:")) {
                            totalMemory = Integer.parseInt(segs[j].substring(0, segs[j].length() - 1));
                        }
                        if(segs[j].length() >= 7 && segs[j].contains("%cpu")){
                            totalCpu = Integer.parseInt(segs[j].substring(0, segs[j].length() - 4));
                        }
                    }
                    //rs : running service
                    for(int i = 0 ; i < rs.size() ; i++) {
                        if (segs[0].equalsIgnoreCase(Integer.toString(rs.get(i).pid))) {
                            String memoryused = segs[5].substring(0, segs[5].length() - 1);
                            memoryUsed += (Float.parseFloat(memoryused) * 1024);
                            cpuUsed += (Float.parseFloat(segs[8]));
                            break;
                        }
                    }
                    //rs : running process
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

package com.example.batterynotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class BatteryReceiver extends BroadcastReceiver {
    private static final String channelId = "batterychannel";
    private static final String channelName = "batterychannel";
    private static final int id = 1;
    private Context mContext;
    private static String title;
    private static String content;
    public BatteryReceiver(Context context, String title, String content){
        super();
        mContext = context;
        this.title = title;
        this.content = content;
    }
    public void createNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.color.transparent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }
    @Override
    public void onReceive(Context context, Intent intent){
        createNotification();
        //Log.e("mytag", "jho");
    }
}

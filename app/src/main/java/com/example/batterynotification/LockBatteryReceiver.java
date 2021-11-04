package com.example.batterynotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class LockBatteryReceiver extends BroadcastReceiver {
    private static final String channelId = "lockbatterychannel";
    private static final String channelName = "lockbatterychannel";
    private static final int id = 1;
    private Context mContext;
    public LockBatteryReceiver(Context context){
        super();
        mContext = context;
    }
    public void createNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentTitle("This is example notification")
                .setContentText("This is example content")
                .setSmallIcon(android.R.drawable.sym_def_app_icon);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }
    @Override
    public void onReceive(Context context, Intent intent){
        createNotification();
        //Log.e("mytag", "jho");
    }
}

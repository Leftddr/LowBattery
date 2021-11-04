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
    public BatteryReceiver(Context context){
        super();
        mContext = context;
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
                .setContentTitle("This is example notification")
                .setContentText("This is example content")
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
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

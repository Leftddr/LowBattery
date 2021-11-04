package com.example.batterynotification;

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
import androidx.core.content.ContextCompat;

public class ColoredBatteryReceiver extends BroadcastReceiver {
    private static final String channelId = "coloredbatterychannel";
    private static final String channelName = "coloredbatterychannel";
    private static final int id = 1;
    private RemoteViews notificationLayout;
    private RemoteViews notificationLayoutExpanded;
    private Context mContext;
    public ColoredBatteryReceiver(Context context){
        super();
        mContext = context;
        notificationLayout = new RemoteViews(mContext.getPackageName(), R.layout.notification_small);
        notificationLayoutExpanded = new RemoteViews(mContext.getPackageName(), R.layout.notification_small);
    }
    public void createNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentTitle("This is example notification")
                .setOnlyAlertOnce(true)
                .setContentText("This is example content")
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setColor(ContextCompat.getColor(mContext, R.color.design_default_color_error))
                .setColorized(true)
                .setStyle(new androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded);


        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }
    @Override
    public void onReceive(Context context, Intent intent){
        createNotification();
    }
}

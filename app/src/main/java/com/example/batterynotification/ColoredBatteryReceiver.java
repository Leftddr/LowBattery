package com.example.batterynotification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import java.util.ArrayList;
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
    protected int totalMemory, totalCpu, memoryUsed;
    protected double cpuUsed;
    private String GROUP_KEY_WORK_EMAIL = "com.example.notigroup";
    private int count;
    private RemoteViews contentView;
    private long now;
    ArrayList<String> processNames = new ArrayList<>();
    ArrayList<Drawable> Icons = new ArrayList<>();
    ArrayList<Integer> percents = new ArrayList<>();

    public ColoredBatteryReceiver(Context context, String title, String content){
        super();
        mContext = context;
        notificationLayout = new RemoteViews(mContext.getPackageName(), R.layout.notification_small);
        notificationLayoutExpanded = new RemoteViews(mContext.getPackageName(), R.layout.notification_small);
        this.title = title;
        this.content = content;
    }

    public Bitmap drawableToBitmap(Drawable drawable){
        if(drawable == null) return null;
        Bitmap bitmap = null;
        if(drawable instanceof BitmapDrawable){
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) return bitmapDrawable.getBitmap();
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void createNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        contentView = new RemoteViews(mContext.getPackageName(), R.layout.battery);
        for(int i = 0 ; i < count ; i++) {
            Bitmap bitmap;
            if((bitmap = drawableToBitmap(Icons.get(i))) != null)
                contentView.setImageViewBitmap(R.id.batteryimage, bitmap);
            else
                contentView.setImageViewResource(R.id.batteryimage, R.drawable.kakao);
            contentView.setTextViewText(R.id.batterytext, processNames.get(i));
//            contentView.setTextViewText(R.id.percent, String.valueOf(percents.get(i)) + "%");
            // time reduction
            contentView.setTextViewText(R.id.percent, String.valueOf("- " + percents.get(i)) + " min");
            // time addition
//            contentView.setTextViewText(R.id.percent, String.valueOf("+ " + percents.get(i)) + " min");
            Intent notiIntent = new Intent("android.intent.action.Battery");
            notiIntent.putExtra("time", now);
            notiIntent.putExtra("processname", processNames.get(i));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    0,
                    notiIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            contentView.setOnClickPendingIntent(R.id.batterybutton, pendingIntent);

            Notification newMessageNotification = new NotificationCompat.Builder(mContext, channelId)
                    .setSmallIcon(R.drawable.kakao)
                    .setContent(contentView)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setOnlyAlertOnce(true)
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
                                .addLine("memory * cpu")
                                .addLine("종료를 원하시면 notification을 touch하세요")
                                .setBigContentTitle("배터리 소모 앱")
                                .setSummaryText("배터리 소모 앱들 정리"))
                        //specify which group this notification belongs to
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();

        notificationManager.notify(0, summaryNotification);
    }
    @Override
    public void onReceive(Context context, Intent intent){
        setting(context, intent);
        createNotification();
    }

    String getAppName (String processName){
        String[] result = processName.split("\\.");
        return result[result.length-1];
    }

    public void setting(Context context, Intent intent){
        count = intent.getIntExtra("count", 0);
        now = intent.getLongExtra("time", 0);
        String process = "process";
        for (int i = 0 ; i < count ; i++) {
               String newProcessName = process + Integer.toString(i);
               String processName = intent.getStringExtra(newProcessName);
               processNames.add(processName);
               percents.add(intent.getIntExtra(newProcessName + "percent", 0));
               try {
                   Drawable icon = context.getPackageManager().getApplicationIcon(processName);
                   Icons.add(icon);
               } catch (Exception e){
                   Icons.add(null);
                   e.printStackTrace();
               }
        }
    }
}

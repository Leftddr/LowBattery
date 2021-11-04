package com.example.batterynotification;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PopupReceiver extends BroadcastReceiver {
    private Context mContext;
    private String title;
    private String content;
    public PopupReceiver(Context context, String title, String content){
        super();
        this.mContext = context;
        this.title = title;
        this.content = content;
    }

    public void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(this.title).setMessage(this.content);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onReceive(Context context, Intent intent){
        createDialog();
    }
}

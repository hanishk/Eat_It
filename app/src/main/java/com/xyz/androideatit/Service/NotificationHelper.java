package com.xyz.androideatit.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.xyz.androideatit.Common.Common;
import com.xyz.androideatit.Model.Request;
import com.xyz.androideatit.OrderStatus;
import com.xyz.androideatit.R;

public class NotificationHelper extends ContextWrapper {

    private static final String Channel_Id = "com.xyz.androideatit.Service.Hanish";
    private static final String Channel_Name = "Hanish Channel";
    private NotificationManager manager;
    ListenOrder listenOrder;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel hanishChannel = new NotificationChannel(Channel_Id, Channel_Name, NotificationManager.IMPORTANCE_DEFAULT);
        hanishChannel.enableLights(true);
        hanishChannel.enableVibration(true);
        hanishChannel.setLightColor(Color.RED);
        hanishChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(hanishChannel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    public Notification.Builder getHanishChannelNotification(String key, Request request) {
        Intent intent = new Intent(getBaseContext(), OrderStatus.class);
        intent.putExtra("userPhone", request.getPhone());
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Builder(getApplicationContext(), Channel_Id)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setTicker("Hanish")
                .setContentText("Order " + key + " is " + Common.convertCodeToStatus(request.getStatus()))
                .setContentTitle("Eat It")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
    }


}

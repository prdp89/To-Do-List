package com.example.dev.contentprovidermvp.service.local;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.m1040033.contentprovidermvp.R;
import com.example.dev.contentprovidermvp.view.MainActivity;

/**
 * Created by M1040033 on 8/11/2017.
 */

public class TodoNotificationService extends IntentService {
    public static final String TODOTEXT = "com.example.m1040033.contentprovidermvp.todonotificationservicetext";
    public static final String TODOUUID = "com.example.m1040033.contentprovidermvp.todonotificationserviceuuid";

    private String mTodoText;
    private String mTodoUUID;

    private Context mContext;

    public TodoNotificationService(){
        super("TodoNotificationService");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(Intent intent) {
        mTodoText = intent.getStringExtra(TODOTEXT);
        mTodoUUID = intent.getSerializableExtra(TODOUUID).toString();

        Log.d("Pardeep", "onHandleIntent called");

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent i = new Intent(this, MainActivity.class);

        i.putExtra(TodoNotificationService.TODOUUID, mTodoUUID);

         Intent deleteIntent = new Intent(this, DeleteNotificationService.class);
        deleteIntent.putExtra(TODOUUID, mTodoUUID);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(mTodoText)
                .setSmallIcon(R.drawable.ic_done_black_24dp)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDeleteIntent(PendingIntent.getService(this, mTodoUUID.hashCode(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentIntent(PendingIntent.getActivity(this, mTodoUUID.hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT))
                .build();

        manager.notify(100, notification);
    }
}


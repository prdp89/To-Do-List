package com.example.dev.contentprovidermvp.service.local;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.dev.contentprovidermvp.model.ToDo;
import com.example.dev.contentprovidermvp.service.Injection;
import com.example.dev.contentprovidermvp.service.ToDoDataSource;
import com.example.dev.contentprovidermvp.service.ToDoRepository;

//This class helps to delete notification from DB and UI list.
public class DeleteNotificationService extends IntentService {

    private ToDoRepository mRepository;

    // Broadcast action for filtering the intent broad casted to the receiver.
    public static final String BROADCAST_ACTION = "com.example.m1040033.contentprovidermvp.RESPONSE";

    public static final String RESPONSE_EXTRA = "RESPONSE";

    public static final int RESPONSE_SUCCESS = 1;

    public static final int RESPONSE_UNAUTHORIZED = 2;

    public DeleteNotificationService() {
        super("DeleteNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mRepository = Injection.provideTasksRepository(getApplicationContext());
        mRepository.deleteTask(new ToDo(null, null, intent.getSerializableExtra(TodoNotificationService.TODOUUID).toString()), new ToDoDataSource.InsertDataCallBack<Integer>() {
            @Override
            public void dataChanged(Integer isSuccess) {

                Intent intent = new Intent();
                intent.setAction(BROADCAST_ACTION);
                if (isSuccess != -1) {
                    intent.putExtra(RESPONSE_EXTRA, RESPONSE_SUCCESS);
                }
                else
                    intent.putExtra(RESPONSE_EXTRA, RESPONSE_UNAUTHORIZED);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });

    }
}

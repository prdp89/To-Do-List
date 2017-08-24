package com.example.dev.contentprovidermvp.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.m1040033.contentprovidermvp.R;

import com.example.dev.contentprovidermvp.customControl.RecyclerViewEmptySupport;
import com.example.dev.contentprovidermvp.model.ToDo;
import com.example.dev.contentprovidermvp.service.local.DeleteNotificationService;
import com.example.dev.contentprovidermvp.utils.ActivityUtils;

public class MainActivity extends AppCompatActivity {

    public static String TODOITEM = MainActivity.class.getSimpleName();
    private RecyclerViewEmptySupport mRecyclerView;

    boolean mIsReceiverRegistered = false;
    DeleteNotificationReceiver mReceiver = null;

    private static MainActivity mainActivityInstance;
    private ToDoListFragment tasksFragment;

    //to check if fragment is alive
    public static MainActivity getInstance() {
        return mainActivityInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityInstance = this;

        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tasksFragment =
                (ToDoListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = ToDoListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }
    }

    public class DeleteNotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int response = intent.getIntExtra(DeleteNotificationService.RESPONSE_EXTRA, 0);
            if (response == DeleteNotificationService.RESPONSE_SUCCESS)
                if (MainActivity.getInstance() != null)
                    MainActivity.getInstance().updateUI(intent);
        }
    }

    private void updateUI(final Intent intent) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                tasksFragment.updateUI(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check and initiate BroadCastReceiver
        if (!mIsReceiverRegistered) {
            if (mReceiver == null)
                mReceiver = new DeleteNotificationReceiver();
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, new IntentFilter(DeleteNotificationService.BROADCAST_ACTION));
            mIsReceiverRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister BroadCastReceiver
        if (mIsReceiverRegistered) {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);
            mReceiver = null;
            mIsReceiverRegistered = false;
        }
    }
}

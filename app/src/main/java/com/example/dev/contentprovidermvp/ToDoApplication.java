package com.example.dev.contentprovidermvp;

import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ToDoApplication extends MultiDexApplication {
    private static final int APP_TERMINATION_TIMEOUT = 1000;
    private static final String TAG = "ToDoApplication";

    //TODO remove this static once RH issue of NPE is solved
    private static Context sContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(ToDoApplication.this);
        MultiDex.install(getBaseContext());
        MultiDex.install(base);
    }

    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable exception) {

                StringWriter sw = new StringWriter();
                exception.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                Log.e(ToDoApplication.class.getSimpleName(), "  ---->  " + exceptionAsString);

                //save to SD card here....
            }
        });

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.w(TAG, "--->Application is exited!!!");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, APP_TERMINATION_TIMEOUT);
    }

    public static Context getAppContext() {
        return sContext;
    }
}

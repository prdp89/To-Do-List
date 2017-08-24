package com.example.dev.contentprovidermvp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

import com.example.m1040033.contentprovidermvp.R;
import com.example.dev.contentprovidermvp.adapter.ToDoListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class AppUtils {

        public static int getToolbarHeight(Context context) {
            final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                    new int[]{R.attr.actionBarSize});
            int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
            styledAttributes.recycle();

            return toolbarHeight;
        }

    public static void hideKeyboard(View view, Context context){

        InputMethodManager imm = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String formatDate(String formatString, Date dateToFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString);
        return simpleDateFormat.format(dateToFormat);
    }

    public static void animateFadeInFadeOut(View view, boolean isVisible) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(800);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        //fadeOut.setStartOffset(1000);
        fadeOut.setDuration(500);

        AnimationSet animation = new AnimationSet(false); //change to false
        if(isVisible)
            animation.addAnimation(fadeIn);
        else
            animation.addAnimation(fadeOut);
        view.setAnimation(animation);
    }

    public static String convertLongToDateString(long timeInMilliSeconds)
    {
        Date date = new Date(timeInMilliSeconds);
        return "Remind at : " + DateFormat.getDateTimeInstance().format(date);
    }

    private static AlarmManager getAlarmManager(Context context){
        return (AlarmManager) context.getSystemService(ALARM_SERVICE);
    }

    public static void createAlarm(Intent intent, int requestCode, long timeInMillis, Context context){
        AlarmManager am = getAlarmManager(context);
        PendingIntent pi = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
//      Log.d("Pardeep", "createAlarm "+requestCode+" time: "+timeInMillis+" PI "+pi.toString());
    }

    private static boolean doesPendingIntentExist(Intent i, int requestCode, Context context){
        PendingIntent pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_NO_CREATE);
        return pi!=null;
    }

    public static void deleteAlarm(Intent i, int requestCode, Context context)
    {
        if(doesPendingIntentExist(i, requestCode, context)){
            PendingIntent pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_NO_CREATE);
            pi.cancel();
            getAlarmManager(context).cancel(pi);
            Log.d("Pardeep", "PI Cancelled " + doesPendingIntentExist(i, requestCode, context));
        }
    }

    public static void showAlertDialog(String title, String message, Context context)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", null);

        final AlertDialog alert = dialog.create();
        alert.show();
    }
}

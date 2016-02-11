package com.example.massa.luxvilla.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by massa on 11/02/2016.
 */
public class notificationreciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent startServiceIntent = new Intent(context, notificationservice.class);
        PendingIntent pendingIntent=PendingIntent.getService(context,0,startServiceIntent,0);
        //context.startService(startServiceIntent);


        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 5);

        AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 300000, pendingIntent);

    }
}

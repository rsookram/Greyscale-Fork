package com.berenluth.grayscale;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

@TargetApi(Build.VERSION_CODES.N)
public class ToggleService extends TileService {

    int duration = UtilValues.DURATION;

    @Override
    public void onClick() {
        super.onClick();

        if (!Util.hasPermission(this)) {
            showDialog(Util.createTipsDialog(this));
            return;
        }

        int oldState = getQsTile().getState();
        if (oldState == Tile.STATE_ACTIVE) {
            setState(Tile.STATE_INACTIVE);
        } else {
            setState(Tile.STATE_ACTIVE);
        }

        SharedPreferences pref = getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE);

        //If the current state is different from the default state, it means that a timer is running
        boolean isTimerSet = (pref.getBoolean(UtilValues.DEFAULT_MODE, false) != Util.isGreyscaleEnable(this));

        Util.toggleGreyscale(this, oldState == Tile.STATE_INACTIVE);

        Intent i = new Intent(getApplicationContext(), TimerReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, getQsTile().getState(), i, PendingIntent.FLAG_CANCEL_CURRENT);
        int duration_code = pref.getInt(UtilValues.TOGGLE_DURATION, 1);
        duration = Util.codeToMinutes(duration_code);


        Calendar cal = Calendar.getInstance();
        cal.add(UtilValues.TIME_UNIT, duration);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

        if(isTimerSet){
            Toast.makeText(this, R.string.timer_deleted , Toast.LENGTH_SHORT).show();
            Log.d("Tile", "A timer was already running");
        } else {
            String message = String.format(getString(R.string.timer_activated),
                    Util.codeToTime(duration_code), getString(Util.codeToMinutesOrHours(duration_code)));
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Log.d("Tile", "Timer set for " + UtilValues.DURATION + " seconds");
        }
    }


    private void setState(int state) {
        Tile tile = getQsTile();
        tile.setState(state);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        boolean greyscaleEnable = Util.isGreyscaleEnable(this);
        setState(greyscaleEnable ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
    }
}

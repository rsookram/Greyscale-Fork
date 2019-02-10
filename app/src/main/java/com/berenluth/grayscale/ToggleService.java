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

        Util.toggleGreyscale(this, oldState == Tile.STATE_INACTIVE);

        Intent i = new Intent(getApplicationContext(), TimerReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, getQsTile().getState(), i, PendingIntent.FLAG_CANCEL_CURRENT);

        SharedPreferences pref = getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE);
        duration = Util.intToMinutes(pref.getInt(UtilValues.TOGGLE_DURATION, 1));

        Calendar cal = Calendar.getInstance();
        cal.add(UtilValues.TIME_UNIT, duration);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

        Toast.makeText(this, "Timer setted for " + duration + " minutes", Toast.LENGTH_SHORT).show();
        Log.d("Tile", "Timer setted for " + UtilValues.DURATION + " seconds");
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

package com.berenluth.grayscale;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.util.Calendar;
import java.util.Date;

public class Util {
    private static final String PERMISSION = "android.permission.WRITE_SECURE_SETTINGS";
    private static final String COMMAND    = "adb shell pm grant " + BuildConfig.APPLICATION_ID + " " + PERMISSION;
    private static final String SU_COMMAND = "pm grant " + BuildConfig.APPLICATION_ID + " " + PERMISSION ;

    private static final String DISPLAY_DALTONIZER_ENABLED = "accessibility_display_daltonizer_enabled";
    private static final String DISPLAY_DALTONIZER         = "accessibility_display_daltonizer";

    public static boolean hasPermission(Context context) {
        return context.checkCallingOrSelfPermission(PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }

    public static Dialog createTipsDialog(final Context context) {
        return new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle(R.string.tips_title)
                .setMessage(context.getString(R.string.tips, COMMAND))
                .setNegativeButton(R.string.tips_ok, null)
                .setPositiveButton(R.string.tips_copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipData clipData = ClipData.newPlainText(COMMAND, COMMAND);
                        ClipboardManager manager = (ClipboardManager) context.getSystemService(Service.CLIPBOARD_SERVICE);
                        manager.setPrimaryClip(clipData);
                        Toast.makeText(context, R.string.copy_done, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("root", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Process su = Runtime.getRuntime().exec("su");
                            DataOutputStream os = new DataOutputStream(su.getOutputStream());
                            os.writeBytes(SU_COMMAND + "\n");
                            os.writeBytes("exit\n");
                            os.close();
                            su.waitFor();
                            os.close();
                            su.destroy();
                            toggleGreyscale(context, !isGreyscaleEnable(context));
                        } catch (Exception e) {
                            Toast.makeText(context, R.string.root_failure, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                })
                .create();
    }

    public static boolean isGreyscaleEnable(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        return Secure.getInt(contentResolver, DISPLAY_DALTONIZER_ENABLED, 0) == 1
                && Secure.getInt(contentResolver, DISPLAY_DALTONIZER, 0) == 0;
    }

    public static void toggleGreyscale(Context context, boolean greyscale) {
        ContentResolver contentResolver = context.getContentResolver();
        Secure.putInt(contentResolver, DISPLAY_DALTONIZER_ENABLED, greyscale ? 1 : 0);
        Secure.putInt(contentResolver, DISPLAY_DALTONIZER, greyscale ? 0 : -1);
    }

    public static int codeToMinutes(int x){
        switch (x){
            case 0:
                return 15;
            case 1:
                return 30;
            case 2:
                return 60;
            case 3:
                return 2*60;
            case 4:
                return 4*60;
            case 5:
                return 8*60;
            default:
                return 30;
        }
    }

    public static int codeToTime(int x){
        switch (x){
            case 0:
                return 15;
            case 1:
                return 30;
            case 2:
                return 60;
            case 3:
                return 2;
            case 4:
                return 4;
            case 5:
                return 8;
            default:
                return 30;
        }
    }

    public static int codeToMinutesOrHours(int x){
        if( x < 3)
            return R.string.minutes;
        else
            return R.string.hours;
    }

    public static boolean isCurrentTimeInWindow(SharedPreferences prefs){
        int startHH = prefs.getInt(UtilValues.NIGHT_MODE_START_HH, 22);
        int startMM = prefs.getInt(UtilValues.NIGHT_MODE_START_MM, 0);
        int endHH = prefs.getInt(UtilValues.NIGHT_MODE_END_HH, 7);
        int endMM = prefs.getInt(UtilValues.NIGHT_MODE_END_MM, 0);

        return isCurrentTimeInWindow(startHH, startMM, endHH, endMM);
    }

    public static boolean isCurrentTimeInWindow(int startHH, int startMM, int endHH, int endMM){
        Calendar c = Calendar.getInstance();    //Current time

        Date currentDate = new Date(c.getTimeInMillis());
        Date startDate = new Date(c.getTimeInMillis()); //start date initialized from current time
        Date endDate = new Date(c.getTimeInMillis());   //end date initialized from current time

        //Set selected hours and minutes to the start date
        startDate.setHours(startHH);
        startDate.setMinutes(startMM);
        startDate.setSeconds(0);

        //Set selected hours and minutes to the end date
        endDate.setHours(endHH);
        endDate.setMinutes(endMM);
        endDate.setSeconds(0);

        Log.d("DATE TEST", "dates equal" + startDate.before(startDate));

        if( startDate.before(endDate)) {
            return (!currentDate.before(startDate)) && currentDate.before(endDate);
        } else {
            return !((!currentDate.before(endDate)) && currentDate.before(startDate));
        }
    }

    public static void setAlarmNightMode(boolean start, Context context){
        Log.d("Util", "setAlarmNightMode for start: " + start);
        SharedPreferences pref = context.getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE);

        int startHH = pref.getInt(UtilValues.NIGHT_MODE_START_HH, 22);
        int startMM = pref.getInt(UtilValues.NIGHT_MODE_START_MM, 0);
        int endHH = pref.getInt(UtilValues.NIGHT_MODE_END_HH, 7);
        int endMM = pref.getInt(UtilValues.NIGHT_MODE_END_MM, 0);

        //Setting an alarm that will start the night mode
        if(start){
            Intent i = new Intent(context, AlarmReceiver.class);
            i.setAction(UtilValues.ACTION_NIGHT_MODE_START);
            PendingIntent sender = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_CANCEL_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, startHH);
            cal.set(Calendar.MINUTE, startMM);
            cal.set(Calendar.SECOND, 0);

            //If the start is before now, it means that it's in the next day
            if( cal.before(Calendar.getInstance()) )
                cal.add(Calendar.DAY_OF_YEAR, 1);

            Log.d("Util", "alarmManager for night mode start for " + cal.toString());

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
        }

        //Setting an alarm that will stop the night mode
        else {
            Intent i = new Intent(context, AlarmReceiver.class);
            i.setAction(UtilValues.ACTION_NIGHT_MODE_END);
            PendingIntent sender = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_CANCEL_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, endHH);
            cal.set(Calendar.MINUTE, endMM);
            cal.set(Calendar.SECOND, 0);

            //If the end is before now, it means that it's in the next day
            if( cal.before(Calendar.getInstance()) )
                cal.add(Calendar.DAY_OF_YEAR, 1);

            Log.d("Util", "alarmManager for night mode start for " + cal.toString());

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
        }
    }
}

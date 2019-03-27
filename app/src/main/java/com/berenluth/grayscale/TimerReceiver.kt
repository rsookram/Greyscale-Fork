package com.berenluth.grayscale

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * To maintain the logic of the app, this class should -ONLY- set the mode
 * back to the default_mode value (written in preferences)
 */
class TimerReceiver : BroadcastReceiver(){
    val TAG = "TimerReceiver"

    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p0 != null && p1 != null && p1.action != null){
            Log.d(TAG, "Action::" + p1.action)

            val prefs = p0.getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
            val defaultMode = prefs.getBoolean(UtilValues.DEFAULT_MODE, false)
            val nightMode = prefs.getBoolean(UtilValues.NIGHT_MODE_ENABLED, false)
            val inTimeWindow = Util.isCurrentTimeInWindow(prefs)

            val action = p1.action.toLowerCase()

            /**Boot completed**/
            if(action == Intent.ACTION_BOOT_COMPLETED.toLowerCase()){
                Log.d(TAG, "Action::" + action)

                val correctState = defaultMode || (nightMode && inTimeWindow)
                if(Util.hasPermission(p0))
                    Util.toggleGreyscale(p0, correctState)

                if(nightMode){
                    if (inTimeWindow){
                        //TODO set alarm manager to the end
                        Util.setAlarmNightMode(false, p0)
                    } else {
                        //TODO set alarm manager to the start
                        Util.setAlarmNightMode(true, p0)
                    }
                }
            }

            /**Timer ended**/
            if(action == UtilValues.ACTION_TIMER_END.toLowerCase()){
                val currentMode = Util.isGreyscaleEnable(p0)
                Log.d(TAG, "Default mode: $defaultMode, current mode $currentMode")

                val correctState = defaultMode || (nightMode && inTimeWindow)

                if (correctState != currentMode) {
                    if(Util.hasPermission(p0))
                        Util.toggleGreyscale(p0, defaultMode)

                    Toast.makeText(p0, "Grayscale timer ended", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Timer ended, changing mode")
                } else {
                    Log.d(TAG, "Timer ended, we are already in the default mode")
                }
            }

            /** Night schedule start intent **/
            if(action == UtilValues.ACTION_NIGHT_MODE_START.toLowerCase()){

                if (nightMode){
                    if(inTimeWindow)
                        if(Util.hasPermission(p0)){
                            Util.toggleGreyscale(p0, true)
                        Util.setAlarmNightMode(false, p0) //Set the end alarm
                        Toast.makeText(p0, "Grayscale automatic schedule ON", Toast.LENGTH_SHORT).show()
                    }
                    //If i reach this point it means that the time window has been changed after this
                    //timer has been set, so i just set the start again in at the right time
                    else {
                        Util.setAlarmNightMode(true, p0)
                    }
                }
            }

            /** Night schedule end intent **/
            if(action == UtilValues.ACTION_NIGHT_MODE_END.toLowerCase()){
                if (nightMode){
                    //If the night schedule is ended
                    if(!inTimeWindow){
                        if(Util.hasPermission(p0))
                            Util.toggleGreyscale(p0, false)
                        Util.setAlarmNightMode(true, p0)
                    }
                    //If i reach this point it means that the time window has been changed after this
                    //Timer has been set, so i just set the end again to the updated end
                    else{
                        Util.setAlarmNightMode(false, p0)
                    }
                }
            }
        }
    }

    /*private fun setAlarmNightMode(start: Boolean, context: Context){
        val pref = context.getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE);

        val startHH = pref.getInt(UtilValues.NIGHT_MODE_START_HH, 22)
        val startMM = pref.getInt(UtilValues.NIGHT_MODE_START_MM, 0)
        val endHH = pref.getInt(UtilValues.NIGHT_MODE_END_HH, 7)
        val endMM = pref.getInt(UtilValues.NIGHT_MODE_END_MM, 0)

        //Setting an alarm that will start the night mode
        if(start){
            val i = Intent(context, TimerReceiver::class.java)
            i.action = UtilValues.ACTION_NIGHT_MODE_START
            val sender = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_CANCEL_CURRENT)

            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, startHH)
            cal.set(Calendar.MINUTE, startMM)
            cal.set(Calendar.SECOND, 0)

            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, sender)
        }

        //Setting an alarm that will stop the night mode
        else {
            val i = Intent(context, TimerReceiver::class.java)
            i.action = UtilValues.ACTION_NIGHT_MODE_END
            val sender = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_CANCEL_CURRENT)

            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, endHH)
            cal.set(Calendar.MINUTE, endMM)
            cal.set(Calendar.SECOND, 0)

            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, sender)
        }
    }*/


}
package io.github.rsookram.greyscale

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * To maintain the logic of the app, this class should -ONLY- set the mode
 * back to the defaultMode value (written in preferences)
 */
class AlarmReceiver : BroadcastReceiver() {
    val TAG = "AlarmReceiver"

    override fun onReceive(p0: Context?, p1: Intent?) {
        val intentAction = p1?.action
        if (p0 != null && p1 != null && intentAction != null) {
            Log.d(TAG, "Action::" + intentAction)

            val prefs = p0.getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
            val defaultMode = prefs.getBoolean(UtilValues.DEFAULT_MODE, false)
            val nightMode = prefs.getBoolean(UtilValues.NIGHT_MODE_ENABLED, false)
            val inTimeWindow = Util.isCurrentTimeInWindow(prefs)

            val correctState = defaultMode || (nightMode && inTimeWindow)

            val prefNightID = prefs.getInt(UtilValues.ALARM_NIGHT_MODE_ID, 0)
            val intentNightID = p1.getIntExtra(UtilValues.ALARM_NIGHT_MODE_ID, 0)
            Log.d(TAG, "Night id in the preferences=$prefNightID, id received=$intentNightID")

            val action = intentAction.toLowerCase()

            /**Boot completed**/
            if (action == Intent.ACTION_BOOT_COMPLETED.toLowerCase()) {
                Log.d(TAG, "Action::" + action)

                //TODO set alarm night id = 0 in the preferences, then set the alarm with id 0
                prefs.edit().putInt(UtilValues.ALARM_NIGHT_MODE_ID, 0).apply()

                if (Util.hasPermission(p0))
                    Util.toggleGreyscale(p0, correctState)

                if (nightMode) {
                    if (inTimeWindow) {
                        Util.setAlarmNightMode(false, p0)
                    } else {
                        Util.setAlarmNightMode(true, p0)
                    }
                }
            }

            /**Timer ended**/
            if (action == UtilValues.ACTION_TIMER_END.toLowerCase()) {
                val currentMode = Util.isGreyscaleEnable(p0)
                Log.d(TAG, "Default mode: $defaultMode, current mode $currentMode")

                if (correctState != currentMode) {
                    if (Util.hasPermission(p0))
                        Util.toggleGreyscale(p0, correctState)

                    Toast.makeText(p0, "Grayscale timer ended", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Timer ended, changing mode")
                } else {
                    Log.d(TAG, "Timer ended, we are already in the default mode")
                }
            }

            /** Night schedule start intent **/
            if (action == UtilValues.ACTION_NIGHT_MODE_START.toLowerCase()) {

                //TODO check id saved with id in the intent, if they're different, don't do anything
                if( prefNightID != intentNightID){
                    Log.d(TAG, "Old intent")
                }
                else if (nightMode) {
                    if (inTimeWindow)
                        if (Util.hasPermission(p0)) {
                            Util.toggleGreyscale(p0, correctState)
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
            if (action == UtilValues.ACTION_NIGHT_MODE_END.toLowerCase()) {

                //TODO check id saved with id in the intent, if they're different, don't do anything
                if( prefNightID != intentNightID){
                    Log.d(TAG, "Old intent")
                }
                else if (nightMode) {
                    //If the night schedule is ended
                    if (!inTimeWindow) {
                        if (Util.hasPermission(p0))
                            Util.toggleGreyscale(p0, correctState)
                        Util.setAlarmNightMode(true, p0)
                    }
                    //If i reach this point it means that the time window has been changed after this
                    //Timer has been set, so i just set the end again to the updated end
                    else {
                        Util.setAlarmNightMode(false, p0)
                    }
                }
            }
        }
    }
}

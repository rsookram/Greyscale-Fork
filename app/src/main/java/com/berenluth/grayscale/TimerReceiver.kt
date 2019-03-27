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
            val prefs = p0.getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
            val defaultMode = prefs.getBoolean(UtilValues.DEFAULT_MODE, false)
            val nightMode = prefs.getBoolean(UtilValues.NIGHT_MODE_ENABLED, false)
            val inTimeWindow = Util.isCurrentTimeInWindow(prefs)

            val action = p1.action.toLowerCase()

            /**Boot completed**/
            if(action == Intent.ACTION_BOOT_COMPLETED.toLowerCase()){
                Log.d(TAG, "Action::" + action)

                if(defaultMode) //default == ON
                    Util.toggleGreyscale(p0, true)  //Turn on grayscale
                else            //Default == OFF
                    if(nightMode){
                        if (inTimeWindow){
                            Util.toggleGreyscale(p0, true)
                            //TODO set alarm manager to the end
                        } else {
                            Util.toggleGreyscale(p0, false)
                            //TODO set alarm manager to the start
                        }
                    }
                    //Last case: no default on and no nightMode, turn grayscale off
                    else {
                        Util.toggleGreyscale(p0, false)
                    }
            }

            /**Timer ended**/
            if(action == UtilValues.ACTION_TIMER_END.toLowerCase()){
                Log.d(TAG, "Action::" + action)

                val currentMode = Util.isGreyscaleEnable(p0)
                Log.d(TAG, "Default mode: $defaultMode, current mode $currentMode")

                val correctState = defaultMode || (nightMode && inTimeWindow)

                if (correctState != currentMode) {
                    Util.toggleGreyscale(p0, defaultMode)

                    Toast.makeText(p0, "Grayscale timer ended", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Timer ended, changing mode")
                } else {
                    Log.d(TAG, "Timer ended, we are already in the default mode")
                }

            }

        }


    }

}
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
            val action = p1.action.toLowerCase()
            if(action == Intent.ACTION_BOOT_COMPLETED.toLowerCase()){
                //Boot completed
                Log.d(TAG, action)
                Util.toggleGreyscale(p0, !Util.isGreyscaleEnable(p0))
            }

            if(action == UtilValues.ACTION_TIMER_END.toLowerCase()){
                Log.d(TAG, action)

                val prefs = p0.getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
                val defaultMode = prefs.getBoolean(UtilValues.DEFAULT_MODE, false)

                val currentMode = Util.isGreyscaleEnable(p0)

                Log.d(TAG, "Default mode: $defaultMode, current mode $currentMode")
                if (defaultMode != currentMode) {
                    Util.toggleGreyscale(p0, defaultMode)

                    Toast.makeText(p0, "Bringing greyscale back to the default mode", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Timer ended, changing mode")
                } else {
                    Log.d(TAG, "Timer ended, we are already in the default mode")
                }
            }

        }


    }

}
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

        if (p0 != null) {
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
        } else {
            Log.d(TAG, "The context is null, impossible to switch mode")
        }

        /*Util.toggleGreyscale(p0, !Util.isGreyscaleEnable(p0))
        Toast.makeText(p0, "Timer ended, bringing grayscale back", Toast.LENGTH_SHORT).show()
        Log.d("TimerReceiver", "Hello, it is me! they called me!")*/
    }

}
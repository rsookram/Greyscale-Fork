package io.github.rsookram.greyscale

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity() {
    val TAG = "HomeActivity"

    var defaultMode: Boolean = false
    var nightMode: Boolean = false
    var inTimeWindow: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Read the user saved preference
        val prefs = this.getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
        defaultMode = prefs.getBoolean(UtilValues.DEFAULT_MODE, false)

        val mode = prefs.getInt(UtilValues.DARK_THEME, AppCompatDelegate.MODE_NIGHT_NO)

        AppCompatDelegate.setDefaultNightMode(mode)
        delegate.applyDayNight()

        Log.d("HomeActivity", "Default mode = $defaultMode")

        //Update the switch and other views with the correct default mode
        main_switch.isChecked = defaultMode
        animateUI(defaultMode)

        val end = prefs.getLong(UtilValues.TIMER_END, 0L)
        val timerMessage = String.format(getString(R.string.night_mode_running), getTimerEnd(end))

        //Call this function when the switch is pressed
        main_switch.setOnCheckedChangeListener { _, s ->
            run {
                if (Util.hasPermission(this)) {
                    defaultMode = s

                    val correctState = defaultMode || (nightMode && inTimeWindow)

                    Log.d("HomeActivity", "defaultMode=$defaultMode, nightMode=$nightMode, inTimeWindow=$inTimeWindow, correctState=$correctState")

                    Util.toggleGreyscale(this, correctState)
                    animateUI(s)

                    //Update user's preference with the new default mode
                    prefs.edit().putBoolean(UtilValues.DEFAULT_MODE, s).apply()
                    Log.d("HomeActivity", "Default_mode changed in: $s")
                }

                //Show dialog fragment if app doesn't have permissions
                else {
                    main_switch.isChecked = !s  //Bring the switch to the previous state

                    val dialog = Util.createTipsDialog(this)
                    dialog.setOnDismissListener {
                    }
                    dialog.show()
                }
            }
        }

        need_help.setOnClickListener { _ ->
            run {
                val browserIntent = Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.guide_website)))
                startActivity(browserIntent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = this.getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
        nightMode = prefs.getBoolean(UtilValues.NIGHT_MODE_ENABLED, false)
        inTimeWindow = Util.isCurrentTimeInWindow(prefs)

        if (!Util.hasPermission(this)) {
            need_help.visibility = View.VISIBLE
            need_help.animate().translationY(0f).duration = 300L
        }
    }

    fun animateUI(grey: Boolean) {
        val animationScale = 175L
        val scaleFactor = 0.8f

        if(grey) {
            switch_on.animate().scaleX(1f).scaleY(1f).duration = animationScale
            switch_off.animate().scaleX(scaleFactor).scaleY(scaleFactor).duration = animationScale

        }
        else {
            switch_off.animate().scaleX(1f).scaleY(1f).duration = animationScale
            switch_on.animate().scaleX(scaleFactor).scaleY(scaleFactor).duration = animationScale
        }
    }

    fun getTimerEnd(end: Long) : String{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = end

        val format1 = SimpleDateFormat("HH:mm")
        return format1.format(calendar.time)
    }
}

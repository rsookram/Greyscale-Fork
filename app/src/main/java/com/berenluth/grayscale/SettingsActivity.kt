package com.berenluth.grayscale

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_settings.*
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate


class SettingsActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    val TAG = "SettingsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        arrow_back.setOnClickListener { finish() }

        val pref = getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
        val x = pref.getInt(UtilValues.TOGGLE_DURATION, 1)
        seekBar.progress = x
        seekBar.setOnSeekBarChangeListener(this)

        onProgressChanged(null, x, false)

        button_user_guide.setOnClickListener { _ ->
            run {
                val browserIntent = Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.guide_website)))
                startActivity(browserIntent)
            }
        }

        //Set the current night mode state into the switch
        val currentNightModeState = pref.getBoolean(UtilValues.NIGHT_MODE_ENABLED, false)
        night_mode_switch.isChecked = currentNightModeState
        loadNightModeTime(pref)

        //Set the switch listener that change the night mode state
        night_mode_switch.setOnCheckedChangeListener { view, s ->
            pref.edit().putBoolean(UtilValues.NIGHT_MODE_ENABLED, s).apply()
            updateTimeSchedule()
            if (s && !Util.hasPermission(this)) {
                Log.d(TAG, "Night mode enabled but no permission")
                Snackbar.make(view, getString(R.string.night_mode_nopermission), Snackbar.LENGTH_LONG).show()
            }
        }
        night_mode_time_2.setOnClickListener { _ -> timeScheduleListener(night_mode_time_2) }
        night_mode_time_4.setOnClickListener { _ -> timeScheduleListener(night_mode_time_4) }

        button_share.setOnClickListener { _->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.share_app_text))
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, getString(R.string.choose_one)))
        }

        //button_donate.visibility = View.GONE
        button_donate.setOnClickListener { _ ->
            val url = getString(R.string.donate_link)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        loadTheme(pref)
        if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P)
            radio_theme_other.setText(getString(R.string.theme_system_default))
        else
            radio_theme_other.setText(getString(R.string.theme_set_by_battery))

    }

    private fun updateTimeSchedule() {
        val startTime = night_mode_time_2.text.split(":")
        val startHH = startTime[0].toInt()
        val startMM = startTime[1].toInt()
        val endTime = night_mode_time_4.text.split(":")
        val endHH = endTime[0].toInt()
        val endMM = endTime[1].toInt()

        val pref = getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt(UtilValues.NIGHT_MODE_START_HH, startHH)
        editor.putInt(UtilValues.NIGHT_MODE_START_MM, startMM)
        editor.putInt(UtilValues.NIGHT_MODE_END_HH, endHH)
        editor.putInt(UtilValues.NIGHT_MODE_END_MM, endMM)

        //New edit in night mode, increase id
        val nightID = pref.getInt(UtilValues.ALARM_NIGHT_MODE_ID, 0)
        editor.putInt(UtilValues.ALARM_NIGHT_MODE_ID, nightID+1)
        Log.d(TAG, "NightMode updated, id=$nightID")

        editor.apply()

        val defaultMode = pref.getBoolean(UtilValues.DEFAULT_MODE, false)
        val nightMode = night_mode_switch.isChecked
        val inTimeWindow = Util.isCurrentTimeInWindow(startHH, startMM, endHH, endMM)

        val correctState = defaultMode || (nightMode && inTimeWindow)

        if (nightMode) {
            Log.d(TAG, "Night mode enabled for " + startTime + " -> " + endTime)
            Log.d(TAG, "Current time in window: " + Util.isCurrentTimeInWindow(startHH, startMM, endHH, endMM))
            Util.setAlarmNightMode(!Util.isCurrentTimeInWindow(pref), applicationContext)
        }

        if (Util.hasPermission(baseContext))
                Util.toggleGreyscale(baseContext, correctState)
    }

    private fun updateTimerPreferences(view: View) {
        //Snackbar.make(view, getString(R.string.preferences_saved), Snackbar.LENGTH_SHORT).show()
        val pref = getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
        pref.edit().putInt(UtilValues.TOGGLE_DURATION, seekBar.progress).apply()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        if (p0 != null)
            updateTimerPreferences(p0 as View)
    }


    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        var unit = getString(R.string.minutes)
        if (p1 > 2)
            unit = getString(R.string.hours)
        val value = Util.codeToTime(p1)

        text_toggle_duration.text = String.format(getString(R.string.toggle_duration), value, unit)

    }

    private fun timeScheduleListener(textView: EditText) {
        val selectedTime = textView.text.split(":")
        Log.d(TAG, selectedTime.toString())
        val hh = selectedTime[0].toInt()
        val mm = selectedTime[1].toInt()

        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            //night_mode_time_2.setText( "" + hourOfDay + ":" + minute)
            val textString = hourOfDay.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')
            textView.setText(textString)
            updateTimeSchedule()
        }, hh, mm, true)

        timePickerDialog.show()
    }

    private fun loadNightModeTime(pref: SharedPreferences) {
        val startHH = pref.getInt(UtilValues.NIGHT_MODE_START_HH, 22)
        val startMM = pref.getInt(UtilValues.NIGHT_MODE_START_MM, 0)
        val endHH = pref.getInt(UtilValues.NIGHT_MODE_END_HH, 7)
        val endMM = pref.getInt(UtilValues.NIGHT_MODE_END_MM, 0)

        val startString = startHH.toString().padStart(2, '0') + ":" + startMM.toString().padStart(2, '0')
        val endString = endHH.toString().padStart(2, '0') + ":" + endMM.toString().padStart(2, '0')

        night_mode_time_2.setText(startString)
        night_mode_time_4.setText(endString)
    }

    private fun loadTheme(pref: SharedPreferences){
        //Theme function not working on android <= M, let's hide the section "theme"
        if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M)
            cardview_theme.visibility = View.GONE

        var mode = pref.getInt(UtilValues.DARK_THEME, AppCompatDelegate.MODE_NIGHT_NO)
        radio_theme_light.isChecked = mode == AppCompatDelegate.MODE_NIGHT_NO
        radio_theme_dark.isChecked = mode == AppCompatDelegate.MODE_NIGHT_YES
        radio_theme_other.isChecked = (mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                || (mode == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)

        //set listener to change the theme
        radio_group.setOnCheckedChangeListener { group, checkedId ->

            var mode = 0
            if(checkedId == radio_theme_light.id) mode = AppCompatDelegate.MODE_NIGHT_NO
            if(checkedId == radio_theme_dark.id) mode = AppCompatDelegate.MODE_NIGHT_YES
            if(checkedId == radio_theme_other.id){
                if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P){
                    mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                } else {
                    mode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
            AppCompatDelegate.setDefaultNightMode(mode)
            delegate.applyDayNight()
            pref.edit().putInt(UtilValues.DARK_THEME, mode).apply()
            Log.d(TAG, "theme mode $mode")
        }
    }
}

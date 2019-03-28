package com.berenluth.grayscale

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_settings.*
import android.widget.TimePicker
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.util.Log
import android.widget.EditText
import java.util.*


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
        editor.apply()

        if (night_mode_switch.isChecked) {
            Log.d(TAG, "Night mode enabled for " + startTime + " -> " + endTime)
            Log.d(TAG, "Current time in window: " + Util.isCurrentTimeInWindow(startHH, startMM, endHH, endMM))
            Util.setAlarmNightMode(!Util.isCurrentTimeInWindow(pref), applicationContext)

            if (Util.hasPermission(baseContext))
                Util.toggleGreyscale(baseContext, Util.isCurrentTimeInWindow(startHH, startMM, endHH, endMM))
        } else {
            if (Util.hasPermission(baseContext))
                Util.toggleGreyscale(baseContext, false)
        }

    }

    private fun updatePreferences(view: View) {
        Snackbar.make(view, getString(R.string.preferences_saved), Snackbar.LENGTH_SHORT).show()
        val pref = getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
        pref.edit().putInt(UtilValues.TOGGLE_DURATION, seekBar.progress).apply()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        if (p0 != null)
            updatePreferences(p0 as View)
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
}

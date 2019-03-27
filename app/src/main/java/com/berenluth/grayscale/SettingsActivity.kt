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
import android.util.Log
import android.widget.EditText
import java.util.*


class SettingsActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        arrow_back.setOnClickListener { finish() }
        //button_confirm.setOnClickListener { view -> updatePreferences(view) }


        //TODO set seekbar from preferences
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

        //Set the switch listener that change the night mode state
        night_mode_switch.setOnCheckedChangeListener { view, s ->
            pref.edit().putBoolean(UtilValues.NIGHT_MODE_ENABLED, s).apply()
            if (s) {
                updateTimeSchedule()

                if (!Util.hasPermission(this)){
                    Log.d("SettingsActivity", "Night mode enabled but no permission")
                    Snackbar.make(view, getString(R.string.night_mode_nopermission), Snackbar.LENGTH_LONG).show()
                }
            }
        }
        night_mode_time_2.setOnClickListener { _-> timeScheduleListener(night_mode_time_2) }
        night_mode_time_4.setOnClickListener { _-> timeScheduleListener(night_mode_time_4) }
    }

    private fun setNightModeAlarm(){



    }

    private fun updateTimeSchedule(){
        val startTime = night_mode_time_2.text.split(":")
        val startHH = startTime[0].toInt()
        val startMM = startTime[1].toInt()
        val endTime = night_mode_time_4.text.split(":")
        val endHH = endTime[0].toInt()
        val endMM = endTime[1].toInt()

        val pref = getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE).edit()
        pref.putInt(UtilValues.NIGHT_MODE_START_HH, startHH)
        pref.putInt(UtilValues.NIGHT_MODE_START_MM, startMM)
        pref.putInt(UtilValues.NIGHT_MODE_END_HH, endHH)
        pref.putInt(UtilValues.NIGHT_MODE_END_MM, endMM)

        pref.apply()

        if(night_mode_switch.isChecked){
            Log.d("SettingsActivity", "Night mode enabled for " + startTime + " -> " + endTime)

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
        if(p0 != null)
            updatePreferences(p0 as View)
    }


    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        var unit = getString(R.string.minutes)
        if(p1 > 2)
            unit = getString(R.string.hours)
        var value = Util.codeToTime(p1)

        text_toggle_duration.text = String.format(getString(R.string.toggle_duration), value, unit)

    }

    private fun timeScheduleListener(textView: EditText){
        val selectedTime = textView.text.split(":")
        Log.d("Settings", selectedTime.toString())
        val hh = selectedTime[0].toInt()
        val mm = selectedTime[1].toInt()

        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            //night_mode_time_2.setText( "" + hourOfDay + ":" + minute)
            val textString = hourOfDay.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')
            textView.setText(textString)
            updateTimeSchedule()
        },hh,mm,true)

        timePickerDialog.show()
    }
}

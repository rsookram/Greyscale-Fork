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

        night_mode_time_2.setOnClickListener { _-> timeScheduleListener(night_mode_time_2) }
        night_mode_time_4.setOnClickListener { _-> timeScheduleListener(night_mode_time_4) }

        /*night_mode_time_2.setOnClickListener { _ ->

            run {
                val c = Calendar.getInstance()
                val selectedTime = night_mode_time_2.text.split(":")
                Log.d("Settings", selectedTime.toString())
                val hh = selectedTime[0].toInt()
                val mm = selectedTime[1].toInt()

                val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    //night_mode_time_2.setText( "" + hourOfDay + ":" + minute)
                    updateTimeSchedule(night_mode_time_2, hourOfDay, minute)
                },hh,mm,true)
                timePickerDialog.show()
            }*/


    }

    private fun updateTimeSchedule(){
        var selectedTime = night_mode_time_2.text.split(":")
        val startHH = selectedTime[0].toInt()
        val startMM = selectedTime[1].toInt()
        selectedTime = night_mode_time_4.text.split(":")
        val endHH = selectedTime[0].toInt()
        val endMM = selectedTime[1].toInt()


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

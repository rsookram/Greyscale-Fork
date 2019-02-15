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

class SettingsActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        arrow_back.setOnClickListener { finish() }
        button_confirm.setOnClickListener { view -> updatePreferences(view) }


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
    }

    private fun updatePreferences(view: View) {
        Snackbar.make(view, getString(R.string.preferences_saved), Snackbar.LENGTH_SHORT).show()
        val pref = getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
        pref.edit().putInt(UtilValues.TOGGLE_DURATION, seekBar.progress).apply()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }


    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        var unit = getString(R.string.minutes)
        if(p1 > 2)
            unit = getString(R.string.hours)
        var value = Util.codeToTime(p1)

        text_toggle_duration.text = String.format(getString(R.string.toggle_duration), value, unit)

    }
}

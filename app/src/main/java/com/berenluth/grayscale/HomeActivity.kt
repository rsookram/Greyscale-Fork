package com.berenluth.grayscale

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity() {

    var default_mode: Boolean = false
    lateinit var snackbar : Snackbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Read the user saved preference
        val prefs = this.getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
        default_mode = prefs.getBoolean(UtilValues.DEFAULT_MODE, false)

        Log.d("HomeActivity", "Default mode = $default_mode")

        //Update the switch and other views with the correct default mode
        main_switch.isChecked = default_mode
        animateUI(default_mode)

        val end = prefs.getLong(UtilValues.TIMER_END, 0L)
        val timerMessage = String.format(getString(R.string.timer_is_running), getTimerEnd(end))

        snackbar = Snackbar.make(main_switch, timerMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.stop) { _ -> run{
                    Util.toggleGreyscale(this, default_mode)
                }}

        //Call this function when the switch is pressed
        main_switch.setOnCheckedChangeListener { _, s ->
            run {
                if (Util.hasPermission(this)) {
                    Util.toggleGreyscale(this, s)
                    animateUI(s)

                    //Update user's preference with the new default mode
                    prefs.edit().putBoolean(UtilValues.DEFAULT_MODE, s).apply()
                    default_mode = s
                    Log.d("HomeActivity", "Default_mode changed in: $s")

                    if(snackbar.isShown) {
                        snackbar.dismiss()
                        Snackbar.make(main_switch, R.string.timer_auto_stopped, Snackbar.LENGTH_SHORT).show()
                    }
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

        settings_button.setOnClickListener { _ ->
            run {
                val i = Intent(this, SettingsActivity::class.java)
                startActivity(i)
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
        if( Util.hasPermission(this) && default_mode != Util.isGreyscaleEnable(this) ){
                snackbar.show()
        }


        if (!Util.hasPermission(this)){
            need_help.visibility = View.VISIBLE
            need_help.animate().translationY(0f).duration = 300L
        }
    }

    fun animateUI(gray: Boolean) {
        val animationScale = 175L
        val scaleFactor = 0.8f

        if(gray) {
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

package com.berenluth.grayscale

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*


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

        snackbar = Snackbar.make(main_switch, R.string.timer_is_running, Snackbar.LENGTH_INDEFINITE)
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
    }

    override fun onResume() {
        super.onResume()
        if( default_mode != Util.isGreyscaleEnable(this) ){
                snackbar.show()
        }
    }

    fun animateUI(gray: Boolean) {
        val animationScale = 175L
        if(gray) {
            switch_on.animate().scaleX(1f).scaleY(1f).duration = animationScale
            switch_off.animate().scaleX(0.7f).scaleY(0.7f).duration = animationScale

        }
        else {
            switch_off.animate().scaleX(1f).scaleY(1f).duration = animationScale
            switch_on.animate().scaleX(0.7f).scaleY(0.7f).duration = animationScale
        }
    }
}

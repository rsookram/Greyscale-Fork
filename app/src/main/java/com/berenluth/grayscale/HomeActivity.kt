package com.berenluth.grayscale

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    var default_mode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (Util.hasPermission(this)) {
            //Read the user saved preference
            val prefs = this.getSharedPreferences(UtilValues.GENERAL_PREFERENCES, Context.MODE_PRIVATE)
            default_mode = prefs.getBoolean(UtilValues.DEFAULT_MODE, false)

            //Update the switch and other views with the correct default mode
            main_switch.isChecked = default_mode
            animateUI(default_mode)

            //Call this function when the switch is pressed
            main_switch.setOnCheckedChangeListener { _, s ->
                run {
                    Util.toggleGreyscale(this, s)
                    animateUI(s)

                    //Update user's preference with the new default mode
                    prefs.edit().putBoolean(UtilValues.DEFAULT_MODE, s).apply()
                    Log.d("HomeActivity", "Default_mode changed in: $s")
                }
            }

        }

        //Show dialog fragment if app doesn't have permissions
        else {
            val dialog = Util.createTipsDialog(this)
            dialog.setOnDismissListener {
                finish()
            }
            dialog.show()
        }
    }


    fun animateUI(gray: Boolean) {
        val ANIMATION_SCALE = 175L
        if(gray) {
            //icon_colors.visibility = View.INVISIBLE
            //icon_colors.animate().alphaBy(100f).alpha(0f).duration = ANIMATION_SCALE
            switch_on.animate().scaleX(1f).scaleY(1f).duration = ANIMATION_SCALE
            switch_off.animate().scaleX(0.7f).scaleY(0.7f).duration = ANIMATION_SCALE

        }
        else {
            //icon_colors.visibility = View.VISIBLE
            switch_off.animate().scaleX(1f).scaleY(1f).duration = ANIMATION_SCALE
            switch_on.animate().scaleX(0.7f).scaleY(0.7f).duration = ANIMATION_SCALE
        }
    }
}

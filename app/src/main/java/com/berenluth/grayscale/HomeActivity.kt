package com.berenluth.grayscale

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import android.widget.CompoundButton



class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

    }

    fun setUI(gray: Boolean) {
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

    override fun onResume() {
        super.onResume()
        if (Util.hasPermission(this)) {

            main_switch.isChecked = Util.isGreyscaleEnable(this)
            setUI(Util.isGreyscaleEnable(this))

            main_switch.setOnCheckedChangeListener { _, s ->
                run {
                    Util.toggleGreyscale(this, s)
                    setUI(s)
                }
            }

            //finish()
        } else {
            val dialog = Util.createTipsDialog(this)
            dialog.setOnDismissListener {
                //finish()
            }
            dialog.show()
        }

    }
}

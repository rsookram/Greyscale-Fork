package com.berenluth.grayscale

import java.util.*

class UtilValues{
    companion object {

        //App settings
        const val DURATION = 10     //Only for tests
        const val TIME_UNIT = Calendar.SECOND   //

        /** Generic preferences values... **/
        const val GENERAL_PREFERENCES = "general_preferences"   //Boolean
        const val DEFAULT_MODE = "default_mode"
        const val TOGGLE_DURATION = "toggle_duration"

        /** Timer values **/
        const val TIMER_END = "timer_end"
        const val ACTION_TIMER_END = "action_timer_end"

        /** Night mode values**/
        const val NIGHT_MODE_ENABLED = "night_mode_enabled"     //Boolean
        const val ACTION_NIGHT_MODE_START = "action_night_start"
        const val ACTION_NIGHT_MODE_END = "action_night_end"

        const val NIGHT_MODE_START_HH = "night_mode_start_hh"   //Int between 0 and 23
        const val NIGHT_MODE_START_MM = "night_mode_start_mm"   //Int between 0 and 59

        const val NIGHT_MODE_END_HH = "night_mode_end_hh"       //int between 0 and 23
        const val NIGHT_MODE_END_MM = "night_mode_end_mm"       //Int between 0 and 59
    }
}
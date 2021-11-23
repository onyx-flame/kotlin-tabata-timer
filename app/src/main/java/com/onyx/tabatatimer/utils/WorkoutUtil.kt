package com.onyx.tabatatimer.utils

import android.content.Context
import android.graphics.Color
import com.onyx.tabatatimer.R
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.models.WorkoutPhase
import com.zeugmasolutions.localehelper.LocaleHelper.setLocale
import com.zeugmasolutions.localehelper.Locales

class WorkoutUtil {
    companion object {

        fun getWorkoutStepsCount(workout: Workout): Int {
            return 1 + (2 * workout.cycles - 1) * workout.sets + (workout.sets - 1) + 1
        }

        fun getWorkoutTime(workout: Workout): Int {
            var time = workout.prepareTime
            for (j in 0 until workout.sets) {
                for (k in 0 until workout.cycles-1) {
                    time += workout.workTime
                    time += workout.restTime
                }
                time += workout.workTime
                if (j < workout.sets - 1) {
                    time += workout.restBetweenSetsTime
                }
            }
            time += workout.coolDownTime
            return time
        }

        fun getContrastYIQ(color: Int): Int {
            val yiq = (Color.red(color) * 299 + Color.green(color) * 587 + Color.blue(color) * 114) / 1000
            return if (yiq >= 128) {
                Color.rgb(0, 0,0)
            } else {
                Color.rgb(255, 255, 255)
            }
        }

    }
}
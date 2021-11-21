package com.onyx.tabatatimer.utils

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.onyx.tabatatimer.R
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.models.WorkoutPhase

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

        fun getWorkoutDetails(workout: Workout, context: Context): List<WorkoutPhase> {
            var phaseList = mutableListOf<WorkoutPhase>()
            val stepsCount = getWorkoutStepsCount(workout)
            phaseList.add(
                WorkoutPhase(
                    1,
                    workout.color,
                    context.resources.getString(R.string.prepare_phase_title),
                    workout.prepareDescription.toString()
                )
            )
            var currentStepIndex = 2
            for (j in 0 until workout.sets) {
                for (k in 0 until workout.cycles-1) {
                    phaseList.add(
                        WorkoutPhase(
                            currentStepIndex++,
                            workout.color,context.resources.getString(R.string.work_phase_title),
                            workout.workDescription.toString()
                        )
                    )
                    phaseList.add(
                        WorkoutPhase(
                            currentStepIndex++,
                            workout.color,
                            context.resources.getString(R.string.rest_phase_title),
                            workout.restDescription.toString()
                        )
                    )
                }
                phaseList.add(
                    WorkoutPhase(
                        currentStepIndex++,
                        workout.color,
                        context.resources.getString(R.string.work_phase_title),
                        workout.workDescription.toString()
                    )
                )
                phaseList.add(
                    WorkoutPhase(
                        currentStepIndex++,
                        workout.color,
                        context.resources.getString(R.string.rest_between_sets_phase_title),
                        workout.restBetweenSetsDescription.toString()
                    )
                )
            }
            phaseList[stepsCount - 1] =
                WorkoutPhase(
                    stepsCount,
                    workout.color,
                    context.resources.getString(R.string.cooldown_phase_title),
                    workout.coolDownDescription.toString()
                )
            Log.d("TTT",phaseList.toString())
            return phaseList
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
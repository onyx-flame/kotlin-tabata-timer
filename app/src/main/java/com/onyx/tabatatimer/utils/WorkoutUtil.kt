package com.onyx.tabatatimer.utils

import android.util.Log
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
                    time += workout.cyclesRestTime
                }
            }
            time += workout.coolDownTime
            return time
        }

        fun getWorkoutDetails(workout: Workout): List<WorkoutPhase> {
            var phaseList = mutableListOf<WorkoutPhase>()
            val stepsCount = getWorkoutStepsCount(workout)
            phaseList.add(WorkoutPhase(1, "Prepare", workout.prepareDescription.toString()))
            var currentStepIndex = 2
            for (j in 0 until workout.sets) {
                for (k in 0 until workout.cycles-1) {
                    phaseList.add(WorkoutPhase(currentStepIndex++, "Work", workout.workDescription.toString()))
                    phaseList.add(WorkoutPhase(currentStepIndex++, "Rest", workout.restDescription.toString()))
                }
                phaseList.add(WorkoutPhase(currentStepIndex++, "Work", workout.workDescription.toString()))
                phaseList.add(WorkoutPhase(currentStepIndex++, "Cycle Rest", workout.cyclesRestDescription.toString()))
            }
            phaseList[stepsCount - 1] = (WorkoutPhase(stepsCount, "CoolDown", workout.coolDownDescription.toString()))
            Log.d("TTT",phaseList.toString())
            return phaseList
        }

    }
}
package com.onyx.tabatatimer.utils

import com.onyx.tabatatimer.models.Workout

class WorkoutUtil {
    companion object {

        fun getWorkoutStepsCount(workout: Workout): Int {
            return 1 + (2 * workout.cycles - 1) * workout.sets + (workout.sets - 1) + 1
        }

        fun getWorkoutDetails(workout: Workout): Pair<MutableMap<Int, String>, Int> {
            var timerMap = mutableMapOf<Int, String>()
            var time = 0
            val stepsCount = WorkoutUtil.getWorkoutStepsCount(workout)
            timerMap[1] = "Prepare"
            var currentStepIndex = 2
            for (j in 0 until workout.sets) {
                for (k in 0 until workout.cycles-1) {
                    timerMap[currentStepIndex++] = "Work"
                    time += workout.workTime
                    timerMap[currentStepIndex++] = "Rest"
                    time += workout.restTime
                }
                timerMap[currentStepIndex++] = "Work"
                time += workout.workTime
                timerMap[currentStepIndex++] = "Cycle Rest"
                time += workout.cyclesRestTime
            }
            timerMap[stepsCount] = "CoolDown"
            time += workout.coolDownTime
            return Pair(timerMap, time)
        }

    }
}
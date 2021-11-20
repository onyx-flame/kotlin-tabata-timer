package com.onyx.tabatatimer.utils

import android.util.Log
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.models.WorkoutPhase

class WorkoutUtil {
    companion object {

        fun getWorkoutStepsCount(workout: Workout): Int {
            return 1 + (2 * workout.cycles - 1) * workout.sets + (workout.sets - 1) + 1
        }

        fun getWorkoutDetails(workout: Workout): Pair<List<WorkoutPhase>, Int> {
            var phaseList = mutableListOf<WorkoutPhase>()
            var time = 0
            val stepsCount = WorkoutUtil.getWorkoutStepsCount(workout)
            phaseList.add(WorkoutPhase(1, "Prepare", workout.prepareDescription.toString()))
            var currentStepIndex = 2
            for (j in 0 until workout.sets) {
                for (k in 0 until workout.cycles-1) {
                    phaseList.add(WorkoutPhase(currentStepIndex++, "Work", workout.workDescription.toString()))
                    time += workout.workTime
                    phaseList.add(WorkoutPhase(currentStepIndex++, "Rest", workout.restDescription.toString()))
                    time += workout.restTime
                }
                phaseList.add(WorkoutPhase(currentStepIndex++, "Work", workout.workDescription.toString()))
                time += workout.workTime
                phaseList.add(WorkoutPhase(currentStepIndex++, "Cycle Rest", workout.cyclesRestDescription.toString()))
                time += workout.cyclesRestTime
            }
            phaseList[stepsCount - 1] = (WorkoutPhase(stepsCount, "CoolDown", workout.coolDownDescription.toString()))
            time += workout.coolDownTime
            Log.d("TTT",phaseList.toString())
            return Pair(phaseList, time)
        }

    }
}
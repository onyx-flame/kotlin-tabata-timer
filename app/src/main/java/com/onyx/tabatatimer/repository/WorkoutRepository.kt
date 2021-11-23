package com.onyx.tabatatimer.repository

import com.onyx.tabatatimer.db.WorkoutDatabase
import com.onyx.tabatatimer.models.Workout

class WorkoutRepository(private val db: WorkoutDatabase) {

    suspend fun addWorkout(workout: Workout) = db.getWorkoutDao().addWorkout(workout)
    suspend fun updateWorkout(workout: Workout) = db.getWorkoutDao().updateWorkout(workout)
    suspend fun deleteWorkout(workout: Workout) = db.getWorkoutDao().deleteWorkout(workout)
    fun getWorkouts() = db.getWorkoutDao().getWorkouts()
    fun searchWorkouts(query: String) = db.getWorkoutDao().searchWorkouts(query)
    suspend fun deleteAllWorkouts() = db.getWorkoutDao().deleteAllWorkouts()

}
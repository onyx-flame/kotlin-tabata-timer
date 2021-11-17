package com.onyx.tabatatimer.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.onyx.tabatatimer.models.Workout

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWorkout(workout: Workout)

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Query("SELECT * FROM workouts ORDER BY id")
    fun getWorkouts(): LiveData<List<Workout>>

}
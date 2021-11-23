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

    @Query("SELECT * FROM workouts WHERE instr(lower(title), lower(:query)) > 0 ORDER BY id")
    fun searchWorkouts(query: String): LiveData<List<Workout>>

    @Query("DELETE FROM workouts")
    suspend fun deleteAllWorkouts()

}
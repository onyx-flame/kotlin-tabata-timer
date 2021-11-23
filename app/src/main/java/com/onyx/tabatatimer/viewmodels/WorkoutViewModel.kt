package com.onyx.tabatatimer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.repository.WorkoutRepository
import kotlinx.coroutines.launch

class WorkoutViewModel(
    app: Application,
    private val workoutRepository: WorkoutRepository
): AndroidViewModel(app) {

    fun addWorkout(workout: Workout) = viewModelScope.launch {
        workoutRepository.addWorkout(workout)
    }

    fun updateWorkout(workout: Workout) = viewModelScope.launch {
        workoutRepository.updateWorkout(workout)
    }

    fun deleteWorkout(workout: Workout) = viewModelScope.launch {
        workoutRepository.deleteWorkout(workout)
    }

    fun getWorkouts() = workoutRepository.getWorkouts()

    fun searchWorkouts(query: String) = workoutRepository.searchWorkouts(query)

    fun deleteAllWorkouts() = viewModelScope.launch {
        workoutRepository.deleteAllWorkouts()
    }

}
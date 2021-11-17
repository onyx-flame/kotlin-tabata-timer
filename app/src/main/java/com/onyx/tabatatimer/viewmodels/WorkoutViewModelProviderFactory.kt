package com.onyx.tabatatimer.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onyx.tabatatimer.repository.WorkoutRepository

class WorkoutViewModelProviderFactory(
    val app: Application,
    private val workoutRepository: WorkoutRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WorkoutViewModel(app, workoutRepository) as T
    }

}
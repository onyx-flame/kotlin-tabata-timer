package com.onyx.tabatatimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.onyx.tabatatimer.databinding.ActivityMainBinding
import com.onyx.tabatatimer.db.WorkoutDatabase
import com.onyx.tabatatimer.repository.WorkoutRepository
import com.onyx.tabatatimer.viewmodels.WorkoutViewModel
import com.onyx.tabatatimer.viewmodels.WorkoutViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var workoutViewModel: WorkoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setUpViewModel()
    }

    private fun setUpViewModel() {

        val workoutRepository = WorkoutRepository(
            WorkoutDatabase(this)
        )

        val viewModelProviderFactory =
            WorkoutViewModelProviderFactory(
                application,
                workoutRepository
            )

        workoutViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        ).get(WorkoutViewModel::class.java)

    }
}
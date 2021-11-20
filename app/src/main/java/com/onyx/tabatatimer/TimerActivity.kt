package com.onyx.tabatatimer

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.onyx.tabatatimer.adapter.WorkoutPhaseAdapter
import com.onyx.tabatatimer.databinding.ActivityTimerBinding
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.models.WorkoutPhase
import com.onyx.tabatatimer.service.TimerService
import com.onyx.tabatatimer.utils.Constants
import com.onyx.tabatatimer.utils.TimerEvent
import com.onyx.tabatatimer.utils.WorkoutUtil
import java.util.*
import kotlin.math.roundToInt

class TimerActivity : AppCompatActivity() {

    private var isTimerRunning = false
    private lateinit var binding: ActivityTimerBinding
    private val args: TimerActivityArgs by navArgs<TimerActivityArgs>()
    private lateinit var workout: Workout
    private lateinit var timerMap: List<WorkoutPhase>
    private lateinit var workoutPhaseAdapter: WorkoutPhaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workout = if (args.workout == null) {
            TimerService.workout
        } else {
            args.workout!!
        }
        binding.tvPhase.text = workout.title

        binding.fabStartPause.setOnClickListener {
            if (TimerService.isServiceStopped)
                sendCommandToService(Constants.ACTION_START_SERVICE)
            else
            togglePlayPause()
        }

        binding.fabNext.setOnClickListener {
            sendCommandToService(Constants.ACTION_NEXT_STEP_TIMER)
        }

        binding.fabPrevious.setOnClickListener {
            sendCommandToService(Constants.ACTION_PREVIOUS_STEP_TIMER)
        }

        binding.fabStop.setOnClickListener {
            if (!TimerService.isServiceStopped)
            sendCommandToService(Constants.ACTION_STOP_SERVICE)
        }



        setObservers()
        if (TimerService.isServiceStopped) {
            timerMap = WorkoutUtil.getWorkoutDetails(workout).first
            sendCommandToService(Constants.ACTION_START_SERVICE)
        } else {
            timerMap = TimerService.timerMap
        }
        setUpRecyclerView()

    }

    private fun togglePlayPause() {
        if (isTimerRunning) {
            sendCommandToService(Constants.ACTION_START_PAUSE_TIMER)
        }
    }

    private fun setObservers() {
        TimerService.timerEvent.observe(this, Observer {
            when (it) {
                is TimerEvent.START -> {
                    isTimerRunning = true
                    binding.fabStartPause.setImageResource(R.drawable.ic_pause)
                    Unit
                }
                is TimerEvent.END -> {
                    isTimerRunning = false
                    binding.fabStartPause.setImageResource(R.drawable.ic_play)
                    Unit
                }
            }
        })

        TimerService.timerInMillis.observe(this, Observer {
            val currentMillis = (it/1000f).roundToInt()
            Log.d("TTT", "CURRENT: $currentMillis")
            binding.apply {
                tvTimer.text = currentMillis.toString()
                cpi.progress = currentMillis
            }
        })
        TimerService.currentPhaseTitle.observe(this, Observer {
            binding.tvPhase.text = it
        })
        TimerService.currentPhaseTime.observe(this, Observer {
            Log.d("TTT", "MAX: ${binding.cpi.max}")
            binding.cpi.max = it
        })
        TimerService.currentStageNumber.observe(this, Observer {
            if (it != -1) {
                binding.tvStage.text = "$it/${WorkoutUtil.getWorkoutStepsCount(workout)}"
                binding.recyclerView.smoothScrollToPosition(it - 1)
            }
        })
    }

    private fun sendCommandToService(action: String) {
        startService(
            Intent(this, TimerService::class.java).apply {
                this.action = action
                if (action == Constants.ACTION_START_SERVICE) {
                    this.putExtra("workout", workout)
                }
            }
        )
    }

    override fun onBackPressed() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
        sendCommandToService(Constants.ACTION_STOP_SERVICE)
        //super.onBackPressed()
    }

    private fun setUpRecyclerView() {
        workoutPhaseAdapter = WorkoutPhaseAdapter()
        val spanCount =
            if (resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT) {
                2
            } else {
                1
            }
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                spanCount,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = workoutPhaseAdapter
        }
        workoutPhaseAdapter.differ.submitList(timerMap)
    }


}
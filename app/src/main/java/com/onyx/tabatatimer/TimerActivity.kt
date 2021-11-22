package com.onyx.tabatatimer

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelper.setLocale
import com.zeugmasolutions.localehelper.Locales
import java.util.*
import kotlin.math.roundToInt

class TimerActivity : LocaleAwareCompatActivity() {

    private var isTimerRunning = false
    private lateinit var binding: ActivityTimerBinding
    private val args: TimerActivityArgs by navArgs()
    private lateinit var workout: Workout
    private lateinit var timerMap: List<WorkoutPhase>
    private lateinit var workoutPhaseAdapter: WorkoutPhaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*val sharedPreferencess = getSharedPreferences("com.onyx.tabatatimer_preferences", Context.MODE_PRIVATE)
        when (sharedPreferencess.getString("language", "en")) {
            "en" -> {
                setLocale(applicationContext, Locales.English)
            }
            "ru" -> {
                setLocale(applicationContext, Locales.Russian)
            }
        }*/

        workout = if (args.workout == null) {
            TimerService.workout
        } else {
            args.workout!!
        }

        binding.apply {
            clRoot.setBackgroundColor(workout.color)
            val elementsColor = WorkoutUtil.getContrastYIQ(workout.color)
            ivExit.setColorFilter(elementsColor)
            tvPhase.setTextColor(elementsColor)
            ivPlay.setColorFilter(elementsColor)
            ivPause.setColorFilter(elementsColor)
            tvTimer.setTextColor(elementsColor)
            ivPrevious.setColorFilter(elementsColor)
            tvStage.setTextColor(elementsColor)
            ivNext.setColorFilter(elementsColor)
            cpi.setIndicatorColor(elementsColor)
            cpi.setBackgroundColor(workout.color)
            tvPhase.text = workout.title
            ivPlay.setOnClickListener {
                if (!TimerService.isServiceStopped) {
                    togglePlayPause()
                }
            }
            ivPause.setOnClickListener {
                if (!TimerService.isServiceStopped) {
                    togglePlayPause()
                }
            }
            ivExit.setOnClickListener {
                exitWorkout()
            }
            ivNext.setOnClickListener {
                sendCommandToService(Constants.ACTION_NEXT_STEP_TIMER)
            }
            ivPrevious.setOnClickListener {
                sendCommandToService(Constants.ACTION_PREVIOUS_STEP_TIMER)
            }
        }

        setObservers()
        if (TimerService.isServiceStopped) {
            timerMap = getWorkoutDetails(workout)
            Log.d("TTT11", timerMap.toString())
            sendCommandToService(Constants.ACTION_START_SERVICE)
            TimerService.isTimerRunning = true
        } else {
            timerMap = getWorkoutDetails(workout)

        }
        setUpRecyclerView()
        if (!TimerService.isTimerRunning) {
            binding.ivPlay.visibility = View.VISIBLE
            binding.ivPause.visibility = View.GONE
        } else {
            binding.ivPlay.visibility = View.GONE
            binding.ivPause.visibility = View.VISIBLE
        }

    }

    private fun togglePlayPause() {
        if (!TimerService.isServiceStopped) {
            sendCommandToService(Constants.ACTION_START_PAUSE_TIMER)
            if (binding.ivPlay.visibility == View.GONE) {
                binding.ivPlay.visibility = View.VISIBLE
                binding.ivPause.visibility = View.GONE
            } else {
                binding.ivPlay.visibility = View.GONE
                binding.ivPause.visibility = View.VISIBLE
            }
        }
    }

    private fun setObservers() {
        TimerService.timerEvent.observe(this, Observer {
            when (it) {
                is TimerEvent.START -> {
                    isTimerRunning = true
                    //binding.ivStartPause.setImageResource(R.drawable.ic_pause)
                    Unit
                }
                is TimerEvent.END -> {
                    isTimerRunning = false
                    //binding.ivStartPause.setImageResource(R.drawable.ic_play)
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
        exitWorkout()
        //super.onBackPressed()
    }

    private fun exitWorkout() {
        AlertDialog.Builder(this).apply {
            setTitle(resources.getString(R.string.exit_workout_alert_dialog_title))
            setMessage(resources.getString(R.string.exit_workout_alert_dialog_message))
            setPositiveButton(resources.getString(R.string.exit_workout_alert_dialog_positive_button)) { _,_ ->
                val intent = Intent(this@TimerActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
                if (!TimerService.isServiceStopped) {
                    sendCommandToService(Constants.ACTION_STOP_SERVICE)
                }
            }
            setNegativeButton(resources.getString(R.string.exit_workout_alert_dialog_negative_button), null)
        }.create().show()
    }

    private fun setUpRecyclerView() {
        workoutPhaseAdapter = WorkoutPhaseAdapter()
        val spanCount = 1
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

    private fun getWorkoutDetails(workout: Workout): List<WorkoutPhase> {
        var phaseList = mutableListOf<WorkoutPhase>()
        val stepsCount = WorkoutUtil.getWorkoutStepsCount(workout)
        phaseList.add(
            WorkoutPhase(
                1,
                workout.color,
                resources.getString(R.string.prepare_phase_title),
                workout.prepareDescription.toString()
            )
        )
        var currentStepIndex = 2
        for (j in 0 until workout.sets) {
            for (k in 0 until workout.cycles-1) {
                phaseList.add(
                    WorkoutPhase(
                        currentStepIndex++,
                        workout.color,resources.getString(R.string.work_phase_title),
                        workout.workDescription.toString()
                    )
                )
                phaseList.add(
                    WorkoutPhase(
                        currentStepIndex++,
                        workout.color,
                        resources.getString(R.string.rest_phase_title),
                        workout.restDescription.toString()
                    )
                )
            }
            phaseList.add(
                WorkoutPhase(
                    currentStepIndex++,
                    workout.color,
                    resources.getString(R.string.work_phase_title),
                    workout.workDescription.toString()
                )
            )
            phaseList.add(
                WorkoutPhase(
                    currentStepIndex++,
                    workout.color,
                    resources.getString(R.string.rest_between_sets_phase_title),
                    workout.restBetweenSetsDescription.toString()
                )
            )
        }
        phaseList[stepsCount - 1] =
            WorkoutPhase(
                stepsCount,
                workout.color,
                resources.getString(R.string.cooldown_phase_title),
                workout.coolDownDescription.toString()
            )
        Log.d("TTT1",phaseList.toString())
        return phaseList
    }

}
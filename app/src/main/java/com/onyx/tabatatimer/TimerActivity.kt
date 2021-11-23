package com.onyx.tabatatimer

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.navigation.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.onyx.tabatatimer.adapter.WorkoutPhaseAdapter
import com.onyx.tabatatimer.databinding.ActivityTimerBinding
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.models.WorkoutPhase
import com.onyx.tabatatimer.service.TimerService
import com.onyx.tabatatimer.utils.Constants
import com.onyx.tabatatimer.utils.Constants.CONTEXT_NAME
import com.onyx.tabatatimer.utils.TimerEvent
import com.onyx.tabatatimer.utils.WorkoutUtil
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import com.zeugmasolutions.localehelper.LocaleHelper.setLocale
import com.zeugmasolutions.localehelper.Locales
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.util.*
import kotlin.math.roundToInt

class TimerActivity : LocaleAwareCompatActivity() {

    private var isWorkoutRunning = false
    private var isWorkoutCompleted = false
    private lateinit var binding: ActivityTimerBinding
    private val args: TimerActivityArgs by navArgs()
    private lateinit var workout: Workout
    private lateinit var timerPhaseList: List<WorkoutPhase>
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

        binding.apply {
            clRoot.setBackgroundColor(workout.color)
            val elementsColor = WorkoutUtil.getContrastYIQ(workout.color)
            ivExit.setColorFilter(elementsColor)
            tvCurrentPhaseTitle.setTextColor(elementsColor)
            ivPlay.setColorFilter(elementsColor)
            ivPause.setColorFilter(elementsColor)
            tvTimer.setTextColor(elementsColor)
            ivPrevious.setColorFilter(elementsColor)
            tvPhase.setTextColor(elementsColor)
            ivNext.setColorFilter(elementsColor)
            cpi.setIndicatorColor(elementsColor)
            cpi.setBackgroundColor(workout.color)
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
        if (savedInstanceState != null) {
            isWorkoutCompleted = savedInstanceState.getBoolean("isWorkoutCompleted")
            val sharedPreferences = getSharedPreferences(CONTEXT_NAME, Context.MODE_PRIVATE)
            when (sharedPreferences.getString("language", "en")) {
                "en" -> {
                    setLocale(applicationContext, Locales.English)
                }
                "ru" -> {
                    setLocale(applicationContext, Locales.Russian)
                }
            }
        }
        if (TimerService.isServiceStopped and !isWorkoutCompleted) {
            timerPhaseList = getWorkoutDetails(workout)
            sendCommandToService(Constants.ACTION_START_SERVICE)
            TimerService.isTimerRunning = true
        } else {
            timerPhaseList = TimerService.timerPhaseList
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
        TimerService.timerEvent.observe(this, {
            when (it) {
                is TimerEvent.START -> {
                    isWorkoutRunning = true
                }
                is TimerEvent.END -> {
                    if (TimerService.isServiceStopped && !TimerService.isTimerRunning) {
                        if (!isWorkoutCompleted) {
                            isWorkoutCompleted = TimerService.currentPhaseNumber.value!! >= timerPhaseList.size
                            if (isWorkoutCompleted) {
                                val finishPlayer = MediaPlayer.create(applicationContext, R.raw.finish_sound)
                                finishPlayer.setOnCompletionListener { mp ->
                                    mp.release()
                                }
                                finishPlayer.start()
                            }
                        }
                        isWorkoutRunning = false
                        binding.tvCurrentPhaseTitle.text = resources.getString(R.string.finish_phase_title)
                        binding.tvPhase.text = resources.getString(
                            R.string.current_phase_to_all_phase_count_template,
                            timerPhaseList.size,
                            timerPhaseList.size
                        )
                        binding.viewKonfetti.build()
                            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(2000L)
                            .addShapes(Shape.Square, Shape.Circle)
                            .addSizes(Size(12))
                            .setPosition(-50f, binding.viewKonfetti.width + 50f, -50f, -50f)
                            .streamFor(300, 5000L)
                    }
                }
            }
        })

        TimerService.timerInMillis.observe(this, {
            val currentMillis = (it/1000f).roundToInt()
            binding.apply {
                tvTimer.text = currentMillis.toString()
                cpi.progress = currentMillis
            }
        })
        TimerService.currentPhaseTime.observe(this, {
            binding.cpi.max = it
        })
        TimerService.currentPhaseNumber.observe(this, {
            if (it != -1) {
                binding.tvPhase.text = resources.getString(
                    R.string.current_phase_to_all_phase_count_template,
                    it,
                    WorkoutUtil.getWorkoutStepsCount(workout)
                )
                binding.recyclerView.smoothScrollToPosition(it - 1)
                binding.tvCurrentPhaseTitle.text = timerPhaseList[it - 1].phaseTitle
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isWorkoutCompleted", isWorkoutCompleted)
    }

    private fun sendCommandToService(action: String) {
        startService(
            Intent(this, TimerService::class.java).apply {
                this.action = action
                if (action == Constants.ACTION_START_SERVICE) {
                    this.putExtra("workout", workout)
                    this.putParcelableArrayListExtra("timerPhaseList", timerPhaseList as ArrayList<WorkoutPhase>)
                }
            }
        )
    }

    override fun onBackPressed() {
        exitWorkout()
    }

    private fun exitWorkout() {
        AlertDialog.Builder(this).apply {
            setTitle(resources.getString(R.string.exit_workout_alert_dialog_title))
            setMessage(resources.getString(R.string.exit_workout_alert_dialog_message))
            setPositiveButton(resources.getString(R.string.exit_workout_alert_dialog_positive_button)) { _,_ ->
                val intent = Intent(this@TimerActivity,MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
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
        workoutPhaseAdapter.differ.submitList(timerPhaseList)
    }

    private fun getWorkoutDetails(workout: Workout): List<WorkoutPhase> {
        val phaseList = mutableListOf<WorkoutPhase>()
        val stepsCount = WorkoutUtil.getWorkoutStepsCount(workout)
        phaseList.add(
            WorkoutPhase(
                1,
                workout.color,
                resources.getString(R.string.prepare_phase_title),
                workout.prepareDescription.toString(),
                workout.prepareTime
            )
        )
        var currentStepIndex = 2
        for (j in 0 until workout.sets) {
            for (k in 0 until workout.cycles-1) {
                phaseList.add(
                    WorkoutPhase(
                        currentStepIndex++,
                        workout.color,resources.getString(R.string.work_phase_title),
                        workout.workDescription.toString(),
                        workout.workTime
                    )
                )
                phaseList.add(
                    WorkoutPhase(
                        currentStepIndex++,
                        workout.color,
                        resources.getString(R.string.rest_phase_title),
                        workout.restDescription.toString(),
                        workout.restTime
                    )
                )
            }
            phaseList.add(
                WorkoutPhase(
                    currentStepIndex++,
                    workout.color,
                    resources.getString(R.string.work_phase_title),
                    workout.workDescription.toString(),
                    workout.workTime
                )
            )
            phaseList.add(
                WorkoutPhase(
                    currentStepIndex++,
                    workout.color,
                    resources.getString(R.string.rest_between_sets_phase_title),
                    workout.restBetweenSetsDescription.toString(),
                    workout.restBetweenSetsTime
                )
            )
        }
        phaseList[stepsCount - 1] =
            WorkoutPhase(
                stepsCount,
                workout.color,
                resources.getString(R.string.cooldown_phase_title),
                workout.coolDownDescription.toString(),
                workout.coolDownTime
            )
        return phaseList
    }

}
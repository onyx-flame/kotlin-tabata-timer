package com.onyx.tabatatimer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.onyx.tabatatimer.R
import com.onyx.tabatatimer.SplashScreenActivity
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.models.WorkoutPhase
import com.onyx.tabatatimer.utils.Constants
import com.onyx.tabatatimer.utils.TimerEvent
import com.onyx.tabatatimer.utils.WorkoutUtil
import com.zeugmasolutions.localehelper.LocaleHelper.setLocale
import com.zeugmasolutions.localehelper.Locales
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class TimerService: LifecycleService() {

    companion object {
        lateinit var workout: Workout
        lateinit var timerMap: List<WorkoutPhase>
        val timerEvent = MutableLiveData<TimerEvent>()
        val timerInMillis = MutableLiveData<Long>()
        val currentStageNumber = MutableLiveData<Int>(-1)
        val currentPhaseTitle = MutableLiveData<String>()
        val currentPhaseTime = MutableLiveData<Int>()
        var isServiceStopped = true
        var isTimerRunning = true
    }

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var tickPlayer: MediaPlayer
    private lateinit var successPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
        tickPlayer = MediaPlayer.create(applicationContext, R.raw.tick_sound)
        successPlayer = MediaPlayer.create(applicationContext, R.raw.success_sound)
        isTimerRunning = true
        timerEvent.postValue(TimerEvent.END)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                Constants.ACTION_START_SERVICE -> {
                    workout = it.extras?.getParcelable("workout")!!
                    timerMap = WorkoutUtil.getWorkoutDetails(workout, applicationContext)
                    startForegroundService()
                }
                Constants.ACTION_STOP_SERVICE -> {
                    stopService()
                }
                Constants.ACTION_START_PAUSE_TIMER -> {
                    if (!isServiceStopped) {
                        if (isTimerRunning) {
                            isTimerRunning = false
                            countDownTimer.cancel()
                        } else {
                            startTimer()
                        }
                    }
                }
                Constants.ACTION_NEXT_STEP_TIMER -> {
                    if (!isServiceStopped) {
                        countDownTimer.cancel()
                        if (currentStageNumber.value!! < timerMap.size) {
                            updateWorkoutPhaseInfo(1)
                        } else {
                            timerInMillis.value = 0L
                            stopService()
                        }
                        if (isTimerRunning) {
                            startTimer()
                        }
                    }
                }
                Constants.ACTION_PREVIOUS_STEP_TIMER -> {
                    if (!isServiceStopped) {
                        countDownTimer.cancel()
                        if (currentStageNumber.value!! > 1) {
                            updateWorkoutPhaseInfo(-1)
                        } else {
                            currentStageNumber.value = 1
                            updateWorkoutPhaseInfo()
                        }
                        if (isTimerRunning) {
                            startTimer()
                        }
                    }
                }
                else -> Unit
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun resetValues() {
        timerEvent.value = TimerEvent.END
        currentPhaseTitle.value = "Congrats!"
        timerInMillis.value = 0L
    }

    private fun startForegroundService() {
        isServiceStopped = false
        timerEvent.postValue(TimerEvent.START)
        startTimer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(Constants.NOTIFICATION_ID, getNotificationBuilder().build())

        timerInMillis.observe(this, Observer {
            if (!isServiceStopped) {
                val currentMillis = (it/1000f).roundToInt()
                val builder = getNotificationBuilder().setContentText(
                    currentMillis.toString()
                ).setContentTitle(currentPhaseTitle.value)
                notificationManager.notify(Constants.NOTIFICATION_ID, builder.build())
            }
        })
    }

    private fun stopService() {
        isServiceStopped = true
        isTimerRunning = false
        currentStageNumber.value = -1
        //resetValues()
        notificationManager.cancel(Constants.NOTIFICATION_ID)
        countDownTimer.cancel()
        tickPlayer.release()
        successPlayer.release()
        stopForeground(true)
        stopSelf()
    }

    private fun startTimer() {
        CoroutineScope(Dispatchers.Main).launch {
            if (!isServiceStopped && timerEvent.value == TimerEvent.START) {
                if (currentStageNumber.value == -1) {
                    currentStageNumber.value = 1
                    updateWorkoutPhaseInfo()
                }
                countDownTimer = object : CountDownTimer((timerInMillis.value!!).toLong(), 1000) {
                    override fun onTick(p0: Long) {
                        isTimerRunning = true
                        if (p0 <= 3000 ) {
                            tickPlayer.start()
                        }
                        timerInMillis.value = p0
                    }

                    override fun onFinish() {
                        isTimerRunning = false
                        successPlayer.start()

                        if (currentStageNumber.value!! + 1 <= timerMap.size) {
                            updateWorkoutPhaseInfo(1)
                            this@TimerService.startTimer()
                        } else {
                            timerInMillis.value = 0L
                            stopService()
                        }
                    }

                }
                countDownTimer.start()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentIntent(getMainActivityPendingIntent())
    }

    private fun getMainActivityPendingIntent() =
        PendingIntent.getActivity(
            this,
            143,
            Intent(this, SplashScreenActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun getPhaseTime(currentNumber: Int): Int {
        return when (timerMap[currentNumber - 1].phaseTitle) {
            resources.getString(R.string.prepare_phase_title) -> workout.prepareTime
            resources.getString(R.string.work_phase_title) -> workout.workTime
            resources.getString(R.string.rest_phase_title) -> workout.restTime
            resources.getString(R.string.rest_between_sets_phase_title) -> workout.restBetweenSetsTime
            resources.getString(R.string.cooldown_phase_title) -> workout.coolDownTime
            else -> 0
        }
    }

    private fun updateWorkoutPhaseInfo(phaseShift: Int = 0) {
        if (phaseShift != 0) {
            currentStageNumber.value = currentStageNumber.value!! + phaseShift
        }
        currentPhaseTitle.value = timerMap[currentStageNumber.value!! -1].phaseTitle
        currentPhaseTime.value = getPhaseTime(currentStageNumber.value!!)
        timerInMillis.value = (getPhaseTime(currentStageNumber.value!!) * 1000).toLong()
    }

}
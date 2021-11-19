package com.onyx.tabatatimer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.onyx.tabatatimer.MainActivity
import com.onyx.tabatatimer.R
import com.onyx.tabatatimer.SplashScreenActivity
import com.onyx.tabatatimer.TimerActivity
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.utils.Constants
import com.onyx.tabatatimer.utils.TimerEvent
import com.onyx.tabatatimer.utils.WorkoutUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class TimerService: LifecycleService() {

    companion object {
        lateinit var workout: Workout
        lateinit var timerMap: MutableMap<Int, String>
        val timerEvent = MutableLiveData<TimerEvent>()
        val timerInMillis = MutableLiveData<Long>()
        val currentStageNumber = MutableLiveData<Int>(-1)
        val currentPhaseTitle = MutableLiveData<String>()
        val currentPhaseTime = MutableLiveData<Int>()
        var isServiceStopped = true
    }

    private var isTimerRunning = false
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var tickPlayer: MediaPlayer
    private lateinit var successPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
        tickPlayer = MediaPlayer.create(applicationContext, R.raw.tick_sound)
        successPlayer = MediaPlayer.create(applicationContext, R.raw.success_sound)
        timerEvent.postValue(TimerEvent.END)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                Constants.ACTION_START_SERVICE -> {
                    workout = it.extras?.getParcelable("workout")!!
                    timerMap = WorkoutUtil.getWorkoutDetails(workout).first
                    startForegroundService()
                }
                Constants.ACTION_STOP_SERVICE -> {
                    stopService()
                }
                Constants.ACTION_START_PAUSE_TIMER -> {
                    if (isTimerRunning) {
                        isTimerRunning = false
                        countDownTimer.cancel()
                    } else {
                        startTimer()
                    }
                }
                Constants.ACTION_NEXT_STEP_TIMER -> {
                    if (currentStageNumber.value!! < timerMap.size) {
                        countDownTimer.cancel()
                        currentStageNumber.value = currentStageNumber.value!! + 1

                        currentPhaseTime.value = getStageTime(currentStageNumber.value!!)
                        timerInMillis.value = (getStageTime(currentStageNumber.value!!) * 1000).toLong()
                        currentPhaseTitle.value = timerMap[currentStageNumber.value]

                        if (isTimerRunning) {
                            startTimer()
                        }

                    } else {
                        Toast.makeText(this, "Can't Next", Toast.LENGTH_SHORT).show()
                    }
                }
                Constants.ACTION_PREVIOUS_STEP_TIMER -> {
                    if (currentStageNumber.value!! > 1) {
                        countDownTimer.cancel()
                        currentStageNumber.value = currentStageNumber.value!! -  1

                        currentPhaseTime.value = getStageTime(currentStageNumber.value!!)
                        timerInMillis.value = (getStageTime(currentStageNumber.value!!) * 1000).toLong()
                        currentPhaseTitle.postValue(timerMap[currentStageNumber.value])

                        if (isTimerRunning) {
                            startTimer()
                        }

                    } else {
                        Toast.makeText(this, "Can't Previous", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> Unit
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun resetValues() {
        timerEvent.postValue(TimerEvent.END)
        currentPhaseTitle.postValue("Congrats!")
        timerInMillis.postValue(0L)
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
        currentStageNumber.value = -1
        resetValues()
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
                    currentPhaseTitle.postValue(timerMap[currentStageNumber.value])
                    currentPhaseTime.value = getStageTime(currentStageNumber.value!!)
                    timerInMillis.value = (getStageTime(currentStageNumber.value!!) * 1000).toLong()
                }
                countDownTimer = object : CountDownTimer((timerInMillis.value!!).toLong(), 1000) {
                    override fun onTick(p0: Long) {
                        isTimerRunning = true
                        if (p0 <= 3000) {
                            tickPlayer.start()
                        }
                        timerInMillis.postValue(p0)
                    }

                    override fun onFinish() {
                        isTimerRunning = false
                        successPlayer.start()

                        if (currentStageNumber.value!! + 1 <= timerMap.size) {
                            currentStageNumber.value = currentStageNumber.value!! + 1
                            currentPhaseTime.value = getStageTime(currentStageNumber.value!!)
                            timerInMillis.value = (getStageTime(currentStageNumber.value!!) * 1000).toLong()
                            currentPhaseTitle.postValue(timerMap[currentStageNumber.value])
                            this@TimerService.startTimer()
                        } else {
                            timerInMillis.value = 0L
                            Toast.makeText(applicationContext, "Time's up!", Toast.LENGTH_SHORT).show()
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

    private fun getStageTime(currentNumber: Int): Int {
        return when (timerMap[currentNumber]) {
            "Prepare" -> workout.prepareTime
            "Work" -> workout.workTime
            "Rest" -> workout.restTime
            "Cycle Rest" -> workout.cyclesRestTime
            "CoolDown" -> workout.coolDownTime
            else -> 0
        }
    }

}
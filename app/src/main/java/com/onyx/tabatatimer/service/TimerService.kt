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
import java.util.*
import kotlin.math.roundToInt

class TimerService: LifecycleService() {

    companion object {
        lateinit var workout: Workout
        lateinit var timerPhaseList: List<WorkoutPhase>
        val timerEvent = MutableLiveData<TimerEvent>()
        val timerInMillis = MutableLiveData<Long>()
        val currentPhaseNumber = MutableLiveData<Int>(-1)
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
        isTimerRunning = true
        timerEvent.value = TimerEvent.END
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                Constants.ACTION_START_SERVICE -> {
                    workout = it.extras?.getParcelable("workout")!!
                    timerPhaseList = it.extras?.getParcelableArrayList("timerPhaseList")!!
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
                        if (currentPhaseNumber.value!! < timerPhaseList.size) {
                            updateWorkoutPhaseInfo(1)
                        } else {
                            isTimerRunning = false
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
                        if (currentPhaseNumber.value!! > 1) {
                            updateWorkoutPhaseInfo(-1)
                        } else {
                            currentPhaseNumber.value = 1
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
        timerInMillis.value = 0L
    }

    private fun startForegroundService() {
        isServiceStopped = false
        tickPlayer = MediaPlayer.create(applicationContext, R.raw.tick_sound)
        successPlayer = MediaPlayer.create(applicationContext, R.raw.success_sound)
        timerEvent.postValue(TimerEvent.START)
        startTimer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(Constants.NOTIFICATION_ID, getNotificationBuilder().build())

        timerInMillis.observe(this, {
            if (!isServiceStopped) {
                val currentMillis = (it/1000f).roundToInt()
                val builder = getNotificationBuilder().setContentText(
                    currentMillis.toString()
                ).setContentTitle(currentPhaseTitle.value + " | " + resources.getString(
                    R.string.current_phase_to_all_phase_count_template,
                    currentPhaseNumber.value,
                    timerPhaseList.size
                ))
                notificationManager.notify(Constants.NOTIFICATION_ID, builder.build())
            }
        })
    }

    private fun stopService() {
        try {
            if (!isServiceStopped && currentPhaseNumber.value!! >= timerPhaseList.size) {
                Toast.makeText(applicationContext, resources.getString(
                    R.string.current_phase_to_all_phase_count_template,
                    timerPhaseList.size,
                    timerPhaseList.size
                ), Toast.LENGTH_SHORT).show()
            }
            isServiceStopped = true
            isTimerRunning = false
            resetValues()
            currentPhaseNumber.value = -1
            notificationManager.cancel(Constants.NOTIFICATION_ID)
            countDownTimer.cancel()
            tickPlayer.release()
            successPlayer.release()
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startTimer() {
        CoroutineScope(Dispatchers.Main).launch {
            if (!isServiceStopped && timerEvent.value == TimerEvent.START) {
                if (currentPhaseNumber.value == -1) {
                    currentPhaseNumber.value = 1
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
                        if (currentPhaseNumber.value!! + 1 <= timerPhaseList.size) {
                            successPlayer.start()
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
            .setSmallIcon(R.drawable.ic_timer)
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
        return timerPhaseList[currentNumber - 1].phaseTime
    }

    private fun updateWorkoutPhaseInfo(phaseShift: Int = 0) {
        if (phaseShift != 0) {
            currentPhaseNumber.value = currentPhaseNumber.value!! + phaseShift
        }
        currentPhaseTitle.value = timerPhaseList[currentPhaseNumber.value!! -1].phaseTitle
        currentPhaseTime.value = getPhaseTime(currentPhaseNumber.value!!)
        timerInMillis.value = (getPhaseTime(currentPhaseNumber.value!!) * 1000).toLong()
    }

}
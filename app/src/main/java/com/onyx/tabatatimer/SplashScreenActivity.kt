package com.onyx.tabatatimer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.navigation.findNavController
import com.onyx.tabatatimer.databinding.ActivitySplashScreenBinding
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.service.TimerService

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Handler().postDelayed({
            val intent: Intent
            if (TimerService.isServiceStopped) {
                intent = Intent(this,MainActivity::class.java)
            } else {
                intent = Intent(this, TimerActivity::class.java)
                val workout: Workout? = null
                intent.putExtra("workout", workout)
            }
            startActivity(intent)
            finish()
        }, 1000)
    }
}
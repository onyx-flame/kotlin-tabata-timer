package com.onyx.tabatatimer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.onyx.tabatatimer.databinding.ActivitySplashScreenBinding
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.service.TimerService
import com.onyx.tabatatimer.utils.Constants.CONTEXT_NAME
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import com.zeugmasolutions.localehelper.LocaleHelper.setLocale
import com.zeugmasolutions.localehelper.Locales

class SplashScreenActivity : LocaleAwareCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val sharedPreferences = getSharedPreferences(CONTEXT_NAME, Context.MODE_PRIVATE)
        when (sharedPreferences.getBoolean("dark_theme", false)) {
            true -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            false -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        when (sharedPreferences.getString("language", "en")) {
            "en" -> {
                setLocale(applicationContext, Locales.English)
            }
            "ru" -> {
                setLocale(applicationContext, Locales.Russian)
            }
        }

    }

    override fun onResume() {
        super.onResume()
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
            finishAffinity()
        }, 1000)
    }

}
package com.onyx.tabatatimer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.onyx.tabatatimer.databinding.ActivityMainBinding
import com.onyx.tabatatimer.db.WorkoutDatabase
import com.onyx.tabatatimer.fragments.SettingsFragment
import com.onyx.tabatatimer.repository.WorkoutRepository
import com.onyx.tabatatimer.viewmodels.WorkoutViewModel
import com.onyx.tabatatimer.viewmodels.WorkoutViewModelProviderFactory
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import com.zeugmasolutions.localehelper.Locales
import java.util.*

class MainActivity : LocaleAwareCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var workoutViewModel: WorkoutViewModel
    private var CONTEXT_NAME = "com.onyx.tabatatimer_preferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        sharedPreferences = getSharedPreferences(CONTEXT_NAME, Context.MODE_PRIVATE)
        setContentView(binding.root)
        updateAppFontSize()
        setSupportActionBar(binding.toolbar)
        setUpViewModel()

    }

    override fun onResume() {
        super.onResume()

        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                "dark_theme" -> {
                    updateAppTheme()
                }
                "font_size" -> {
                    updateAppFontSize()
                    try {
                        findNavController(R.id.fragmentContainerView).navigate(R.id.action_settingsFragment_to_settingsFragment)
                    } catch (e: Exception) {
                        findNavController(R.id.fragmentContainerView).navigate(R.id.action_homeFragment_to_settingsFragment)
                    }

                }
                "language" -> {
                    updateAppLanguage()
                }
            }
        }
        updateAppFontSize()
        updateAppTheme()
    }

    private fun updateAppFontSize() {
        resources.configuration.fontScale =
            when (sharedPreferences.getString("font_size", "normal")) {
                "small" -> 0.75F
                "normal" -> 1.00F
                "large" -> 1.25F
                else -> 1.00F
            }
        resources.displayMetrics.scaledDensity = resources.configuration.fontScale * resources.displayMetrics.density
        baseContext.resources.updateConfiguration(resources.configuration, DisplayMetrics())

    }

    private fun updateAppTheme() {
        when (sharedPreferences.getBoolean("dark_theme", false)) {
            true -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            false -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun updateAppLanguage() {
        when (sharedPreferences.getString("language", "en")) {
            "en" -> {
                updateLocale(Locales.English)
            }
            "ru" -> {
                updateLocale(Locales.Russian)
            }
        }
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
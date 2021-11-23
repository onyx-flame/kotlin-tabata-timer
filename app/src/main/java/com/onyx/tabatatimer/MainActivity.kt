package com.onyx.tabatatimer

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.onyx.tabatatimer.databinding.ActivityMainBinding
import com.onyx.tabatatimer.db.WorkoutDatabase
import com.onyx.tabatatimer.repository.WorkoutRepository
import com.onyx.tabatatimer.utils.Constants.CONTEXT_NAME
import com.onyx.tabatatimer.viewmodels.WorkoutViewModel
import com.onyx.tabatatimer.viewmodels.WorkoutViewModelProviderFactory
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import com.zeugmasolutions.localehelper.LocaleHelper.setLocale
import com.zeugmasolutions.localehelper.Locales

class MainActivity : LocaleAwareCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var workoutViewModel: WorkoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        sharedPreferences = getSharedPreferences(CONTEXT_NAME, Context.MODE_PRIVATE)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setUpViewModel()
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

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        updateAppFontSize()
    }

    override fun onStop() {
        super.onStop()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
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
                setLocale(applicationContext, Locales.English)
            }
            "ru" -> {
                updateLocale(Locales.Russian)
                setLocale(applicationContext, Locales.Russian)
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

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle(resources.getString(R.string.exit_app_alert_dialog_title))
            setMessage(resources.getString(R.string.exit_app_alert_dialog_message))
            setPositiveButton(resources.getString(R.string.exit_app_alert_dialog_positive_button)) { _,_ ->
                finishAffinity()
            }
            setNegativeButton(resources.getString(R.string.exit_app_alert_dialog_negative_button), null)
        }.create().show()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        when (key) {
            "dark_theme" -> {
                updateAppTheme()
            }
            "font_size" -> {
                updateAppFontSize()
                try {
                    findNavController(R.id.navigation_header_container).navigate(R.id.action_settingsFragment_to_settingsFragment)
                } catch (e: Exception) {
                    e.printStackTrace()
                    try {
                        findNavController(R.id.navigation_header_container).navigate(R.id.action_settingsFragment_to_settingsFragment)
                    } catch (e: Exception) {}
                }
                updateAppLanguage()
                updateAppTheme()

            }
            "language" -> {
                updateAppLanguage()
                updateAppTheme()
            }
        }
    }

}
package com.onyx.tabatatimer.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import com.onyx.tabatatimer.MainActivity
import com.onyx.tabatatimer.R
import com.onyx.tabatatimer.viewmodels.WorkoutViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var workoutViewModel: WorkoutViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setHasOptionsMenu(true)
        (activity as MainActivity).supportActionBar?.title = resources.getString(R.string.settings_menu_toolbar_title)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workoutViewModel = (activity as MainActivity).workoutViewModel

        val clearWorkouts: Preference = findPreference("clear_workouts")!!
        clearWorkouts.setOnPreferenceClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle(resources.getString(R.string.delete_workouts_alert_dialog_title))
                setMessage(resources.getString(R.string.delete_workouts_alert_dialog_message))
                setPositiveButton(resources.getString(R.string.delete_workouts_alert_dialog_positive_button)) { _,_ ->
                    workoutViewModel.deleteAllWorkouts()
                    Snackbar.make(view, resources.getString(R.string.delete_workouts_success_message), Snackbar.LENGTH_SHORT).show()
                }
                setNegativeButton(resources.getString(R.string.delete_workouts_alert_dialog_negative_button), null)
            }.create().show()
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                view?.findNavController()?.navigate(R.id.action_settingsFragment_to_homeFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
    }

}
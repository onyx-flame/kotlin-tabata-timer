package com.onyx.tabatatimer.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.android.material.snackbar.Snackbar
import com.onyx.tabatatimer.MainActivity
import com.onyx.tabatatimer.R
import com.onyx.tabatatimer.databinding.FragmentUpdateWorkoutBinding
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.utils.DigitsInputFilter
import com.onyx.tabatatimer.viewmodels.WorkoutViewModel

class UpdateWorkoutFragment : Fragment() {

    private var _binding: FragmentUpdateWorkoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var workoutViewModel: WorkoutViewModel
    private val args: UpdateWorkoutFragmentArgs by navArgs<UpdateWorkoutFragmentArgs>()
    private lateinit var currentWorkout: Workout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.update_workout_menu, menu)
        (activity as MainActivity).supportActionBar?.title = resources.getString(R.string.update_workout_menu_toolbar_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workoutViewModel = (activity as MainActivity).workoutViewModel

        val timeInputFilter = arrayOf(DigitsInputFilter(1,999))
        val countInputFilter = arrayOf(DigitsInputFilter(1,99))

        binding.apply {
            etPrepareTime.transformationMethod = null
            etPrepareTime.filters = timeInputFilter
            etWorkTime.transformationMethod = null
            etWorkTime.filters = timeInputFilter
            etRestTime.transformationMethod = null
            etRestTime.filters = timeInputFilter
            etRestBetweenSetsTime.transformationMethod = null
            etRestBetweenSetsTime.filters = timeInputFilter
            etCoolDownTime.transformationMethod = null
            etCoolDownTime.filters = timeInputFilter
            etCyclesCount.transformationMethod = null
            etCyclesCount.filters = countInputFilter
            etSetsCount.transformationMethod = null
            etSetsCount.filters = countInputFilter
        }

        binding.cvColor.setOnClickListener {
            ColorPickerDialog
                .Builder(requireContext())
                .setTitle(resources.getString(R.string.color_picker_title))
                .setColorShape(ColorShape.CIRCLE)
                .setColorListener { color, _ ->
                    binding.cvColor.setCardBackgroundColor(color)
                }
                .show()
        }

        currentWorkout = args.workout!!
        binding.apply {
            etTitle.setText(currentWorkout.title)
            cvColor.setCardBackgroundColor(currentWorkout.color)
            etPrepareDescription.setText(currentWorkout.prepareDescription)
            etPrepareTime.setText(currentWorkout.prepareTime.toString())
            etWorkDescription.setText(currentWorkout.workDescription)
            etWorkTime.setText(currentWorkout.workTime.toString())
            etRestDescription.setText(currentWorkout.restDescription)
            etRestTime.setText(currentWorkout.restTime.toString())
            etRestBetweenSetsDescription.setText(currentWorkout.restBetweenSetsDescription)
            etRestBetweenSetsTime.setText(currentWorkout.restBetweenSetsTime.toString())
            etCoolDownDescription.setText(currentWorkout.coolDownDescription)
            etCoolDownTime.setText(currentWorkout.coolDownTime.toString())
            etCyclesCount.setText(currentWorkout.cycles.toString())
            etSetsCount.setText(currentWorkout.sets.toString())
        }
        binding.fabUpdate.setOnClickListener {
            val workoutTitle = binding.etTitle.text.toString()
            val workoutColor = binding.cvColor.cardBackgroundColor.defaultColor
            val workoutPrepareDescription = binding.etPrepareDescription.text.toString()
            val workoutPrepareTime = binding.etPrepareTime.text.toString().toInt()
            val workoutWorkDescription = binding.etWorkDescription.text.toString()
            val workoutWorkTime = binding.etWorkTime.text.toString().toInt()
            val workoutRestDescription = binding.etRestDescription.text.toString()
            val workoutRestTime = binding.etRestTime.text.toString().toInt()
            val workoutRestBetweenSetsDescription = binding.etRestBetweenSetsDescription.text.toString()
            val workoutRestBetweenSets = binding.etRestBetweenSetsTime.text.toString().toInt()
            val workoutCoolDownDescription = binding.etCoolDownDescription.text.toString()
            val workoutCoolDownTime = binding.etCoolDownTime.text.toString().toInt()
            val workoutCyclesCount = binding.etCyclesCount.text.toString().toInt()
            val workoutSetsCount = binding.etSetsCount.text.toString().toInt()

            val workout = Workout(
                currentWorkout.id,
                workoutTitle,
                workoutColor,
                workoutPrepareDescription,
                workoutPrepareTime,
                workoutWorkDescription,
                workoutWorkTime,
                workoutRestDescription,
                workoutRestTime,
                workoutCyclesCount,
                workoutSetsCount,
                workoutRestBetweenSetsDescription,
                workoutRestBetweenSets,
                workoutCoolDownDescription,
                workoutCoolDownTime
            )

            workoutViewModel.updateWorkout(workout)
            Snackbar.make(view, resources.getString(R.string.edit_workout_success_message), Snackbar.LENGTH_SHORT).show()
            view.findNavController().navigate(R.id.action_updateWorkoutFragment_to_homeFragment)
        }
    }

    private fun deleteWorkout() {
        AlertDialog.Builder(activity).apply {
            setTitle(resources.getString(R.string.delete_workout_alert_dialog_title))
            setMessage(resources.getString(R.string.delete_workout_alert_dialog_message))
            setPositiveButton(resources.getString(R.string.delete_workout_alert_dialog_positive_button)) { _,_ ->
                workoutViewModel.deleteWorkout(currentWorkout)
                Snackbar.make(requireView(), resources.getString(R.string.delete_workout_success_message), Snackbar.LENGTH_SHORT).show()
                view?.findNavController()?.navigate(R.id.action_updateWorkoutFragment_to_homeFragment)
            }
            setNegativeButton(resources.getString(R.string.delete_workout_alert_dialog_negative_button), null)
        }.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_menu -> {
                deleteWorkout()
            }
            android.R.id.home -> {
                view?.findNavController()?.navigate(R.id.action_updateWorkoutFragment_to_homeFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
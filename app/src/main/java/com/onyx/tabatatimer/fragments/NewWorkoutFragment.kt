package com.onyx.tabatatimer.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.android.material.snackbar.Snackbar
import com.onyx.tabatatimer.MainActivity
import com.onyx.tabatatimer.R
import com.onyx.tabatatimer.databinding.FragmentNewWorkoutBinding
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.utils.DigitsInputFilter
import com.onyx.tabatatimer.viewmodels.WorkoutViewModel

class NewWorkoutFragment : Fragment() {

    private var _binding: FragmentNewWorkoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var mView: View

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
        _binding = FragmentNewWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.new_workout_menu, menu)
        (activity as MainActivity).supportActionBar?.title = resources.getString(R.string.new_workout_menu_toolbar_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workoutViewModel = (activity as MainActivity).workoutViewModel
        mView = view

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
    }

    private fun saveWorkout(view: View) {
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
            0,
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

        workoutViewModel.addWorkout(workout)
        Snackbar.make(view, resources.getString(R.string.new_workout_success_message),Snackbar.LENGTH_SHORT).show()
        view.findNavController().navigate(R.id.action_newWorkoutFragment_to_homeFragment)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save_menu -> {
                saveWorkout(mView)
            }
            android.R.id.home -> {
                view?.findNavController()?.navigate(R.id.action_newWorkoutFragment_to_homeFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
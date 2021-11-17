package com.onyx.tabatatimer.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.onyx.tabatatimer.MainActivity
import com.onyx.tabatatimer.R
import com.onyx.tabatatimer.databinding.FragmentNewWorkoutBinding
import com.onyx.tabatatimer.models.Workout
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
    ): View? {
        _binding = FragmentNewWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.new_workout_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workoutViewModel = (activity as MainActivity).workoutViewModel
        mView = view

        binding.apply {
            etPrepareTime.transformationMethod = null
            etWorkTime.transformationMethod = null
            etRestTime.transformationMethod = null
            etCyclesRestTime.transformationMethod = null
            etCoolDownTime.transformationMethod = null
            etCyclesCount.transformationMethod = null
            etSetsCount.transformationMethod = null
        }
    }

    private fun saveWorkout(view: View) {
        val workoutTitle = binding.etTitle.text.toString()
        val workoutColor = binding.etColor.text.toString()
        val workoutPrepareTitle = binding.etPrepareTitle.text.toString()
        val workoutPrepareTime = binding.etPrepareTime.text.toString().toInt()
        val workoutWorkTitle = binding.etWorkTitle.text.toString()
        val workoutWorkTime = binding.etWorkTime.text.toString().toInt()
        val workoutRestTitle = binding.etRestTitle.text.toString()
        val workoutRestTime = binding.etRestTime.text.toString().toInt()
        val workoutCyclesRestTitle = binding.etCyclesRestTitle.text.toString()
        val workoutCyclesRestTime = binding.etCyclesRestTime.text.toString().toInt()
        val workoutCoolDownTitle = binding.etCoolDownTitle.text.toString()
        val workoutCoolDownTime = binding.etCoolDownTime.text.toString().toInt()
        val workoutCyclesCount = binding.etCyclesCount.text.toString().toInt()
        val workoutSetsCount = binding.etSetsCount.text.toString().toInt()

        val workout = Workout(
            0,
            workoutTitle,
            workoutColor,
            workoutPrepareTitle,
            workoutPrepareTime,
            workoutWorkTitle,
            workoutWorkTime,
            workoutRestTitle,
            workoutRestTime,
            workoutCyclesRestTitle,
            workoutCyclesRestTime,
            workoutCoolDownTitle,
            workoutCoolDownTime,
            workoutCyclesCount,
            workoutSetsCount
        )

        workoutViewModel.addWorkout(workout)
        Snackbar.make(view, "Workout saved!",Snackbar.LENGTH_SHORT).show()
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
package com.onyx.tabatatimer.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.onyx.tabatatimer.MainActivity
import com.onyx.tabatatimer.R
import com.onyx.tabatatimer.databinding.FragmentUpdateWorkoutBinding
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.viewmodels.WorkoutViewModel

class UpdateWorkoutFragment : Fragment() {

    private var _binding: FragmentUpdateWorkoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var workoutViewModel: WorkoutViewModel
    private val args: UpdateWorkoutFragmentArgs by navArgs()
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
    ): View? {
        _binding = FragmentUpdateWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.update_workout_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workoutViewModel = (activity as MainActivity).workoutViewModel
        binding.apply {
            etPrepareTimeUpdate.transformationMethod = null
            etWorkTimeUpdate.transformationMethod = null
            etRestTimeUpdate.transformationMethod = null
            etCyclesRestTimeUpdate.transformationMethod = null
            etCoolDownTimeUpdate.transformationMethod = null
            etCyclesCountUpdate.transformationMethod = null
            etSetsCountUpdate.transformationMethod = null
        }

        currentWorkout = args.workout!!
        binding.apply {
            etTitleUpdate.setText(currentWorkout.title)
            etColorUpdate.setText(currentWorkout.color)
            etPrepareTitleUpdate.setText(currentWorkout.prepareDescription)
            etPrepareTimeUpdate.setText(currentWorkout.prepareTime.toString())
            etWorkTitleUpdate.setText(currentWorkout.workDescription)
            etWorkTimeUpdate.setText(currentWorkout.workTime.toString())
            etRestTitleUpdate.setText(currentWorkout.restDescription)
            etRestTimeUpdate.setText(currentWorkout.restTime.toString())
            etCyclesRestTitleUpdate.setText(currentWorkout.cyclesRestDescription)
            etCyclesRestTimeUpdate.setText(currentWorkout.cyclesRestTime.toString())
            etCoolDownTitleUpdate.setText(currentWorkout.coolDownDescription)
            etCoolDownTimeUpdate.setText(currentWorkout.coolDownTime.toString())
            etCyclesCountUpdate.setText(currentWorkout.cycles.toString())
            etSetsCountUpdate.setText(currentWorkout.sets.toString())
        }
        binding.fabUpdate.setOnClickListener {
            val workoutTitle = binding.etTitleUpdate.text.toString()
            val workoutColor = binding.etColorUpdate.text.toString()
            val workoutPrepareTitle = binding.etPrepareTitleUpdate.text.toString()
            val workoutPrepareTime = binding.etPrepareTimeUpdate.text.toString().toInt()
            val workoutWorkTitle = binding.etWorkTitleUpdate.text.toString()
            val workoutWorkTime = binding.etWorkTimeUpdate.text.toString().toInt()
            val workoutRestTitle = binding.etRestTitleUpdate.text.toString()
            val workoutRestTime = binding.etRestTimeUpdate.text.toString().toInt()
            val workoutCyclesRestTitle = binding.etCyclesRestTitleUpdate.text.toString()
            val workoutCyclesRestTime = binding.etCyclesRestTimeUpdate.text.toString().toInt()
            val workoutCoolDownTitle = binding.etCoolDownTitleUpdate.text.toString()
            val workoutCoolDownTime = binding.etCoolDownTimeUpdate.text.toString().toInt()
            val workoutCyclesCount = binding.etCyclesCountUpdate.text.toString().toInt()
            val workoutSetsCount = binding.etSetsCountUpdate    .text.toString().toInt()

            val workout = Workout(
                currentWorkout.id,
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

            workoutViewModel.updateWorkout(workout)
            Snackbar.make(view, "Workout Updated!", Snackbar.LENGTH_SHORT).show()
            view.findNavController().navigate(R.id.action_updateWorkoutFragment_to_homeFragment)
        }
    }

    private fun deleteWorkout() {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Workout")
            setMessage("Are you sure to delete this workout?")
            setPositiveButton("Delete") { _,_ ->
                workoutViewModel.deleteWorkout(currentWorkout)
                Snackbar.make(requireView(), "Workout deleted!", Snackbar.LENGTH_SHORT).show()
                view?.findNavController()?.navigate(R.id.action_updateWorkoutFragment_to_homeFragment)
            }
            setNegativeButton("Cancel", null)
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
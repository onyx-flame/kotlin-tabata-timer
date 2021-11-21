package com.onyx.tabatatimer.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.onyx.tabatatimer.MainActivity
import com.onyx.tabatatimer.R
import com.onyx.tabatatimer.adapter.WorkoutAdapter
import com.onyx.tabatatimer.databinding.FragmentHomeBinding
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.viewmodels.WorkoutViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)
        (activity as MainActivity).supportActionBar?.title = resources.getString(R.string.home_menu_toolbar_title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings_menu -> {
                view?.findNavController()?.navigate(R.id.action_homeFragment_to_settingsFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workoutViewModel = (activity as MainActivity).workoutViewModel
        setUpRecyclerView()

        binding.fabAddWorkout.setOnClickListener { mView ->
            mView.findNavController().navigate(R.id.action_homeFragment_to_newWorkoutFragment)
        }
    }

    private fun setUpRecyclerView() {
        workoutAdapter = WorkoutAdapter()
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                1,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = workoutAdapter
        }

        activity?.let {
            workoutViewModel.getWorkouts().observe(viewLifecycleOwner, { workouts ->
                workoutAdapter.differ.submitList(workouts)
                updateUI(workouts)
            })
        }
    }

    private fun updateUI(workouts: List<Workout>) {
        if (workouts.isNotEmpty()) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvNoWorkouts.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.tvNoWorkouts.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
    }

}
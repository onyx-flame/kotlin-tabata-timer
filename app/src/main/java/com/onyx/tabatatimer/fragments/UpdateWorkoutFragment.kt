package com.onyx.tabatatimer.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.onyx.tabatatimer.MainActivity
import com.onyx.tabatatimer.R
import com.onyx.tabatatimer.databinding.FragmentUpdateWorkoutBinding

class UpdateWorkoutFragment : Fragment() {

    private var _binding: FragmentUpdateWorkoutBinding? = null
    private val binding get() = _binding!!

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
        binding.apply {
            etPrepareTimeUpdate.transformationMethod = null
            etWorkTimeUpdate.transformationMethod = null
            etRestTimeUpdate.transformationMethod = null
            etCyclesRestTimeUpdate.transformationMethod = null
            etCoolDownTimeUpdate.transformationMethod = null
            etCyclesCountUpdate.transformationMethod = null
            etSetsCountUpdate.transformationMethod = null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_menu -> {
                Unit
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
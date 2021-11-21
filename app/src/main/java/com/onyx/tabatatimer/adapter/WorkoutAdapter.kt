package com.onyx.tabatatimer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.onyx.tabatatimer.databinding.WorkoutLayoutAdapterBinding
import com.onyx.tabatatimer.fragments.HomeFragmentDirections
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.utils.WorkoutUtil

class WorkoutAdapter: RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(val itemBinding: WorkoutLayoutAdapterBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback =
        object : DiffUtil.ItemCallback<Workout>() {
            override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        return WorkoutViewHolder(
            WorkoutLayoutAdapterBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        )
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val currentWorkout = differ.currentList[position]
        holder.itemBinding.cvWorkout.setCardBackgroundColor(currentWorkout.color)
        holder.itemBinding.tvWorkoutTitle.text = currentWorkout.title
        holder.itemBinding.tvWorkoutDetails.text =
            "Prepare: ${currentWorkout.prepareTime} sec\n" +
            "Work: ${currentWorkout.workTime} sec\n" +
            "Rest: ${currentWorkout.restTime} sec\n" +
            "Cycles Rest: ${currentWorkout.cyclesRestTime} sec\n" +
            "CoolDown: ${currentWorkout.coolDownTime} sec\n" +
            "Cycles: ${currentWorkout.cycles}\n" +
            "Sets: ${currentWorkout.sets}"
        val time = WorkoutUtil.getWorkoutTime(currentWorkout)
        holder.itemBinding.tvWorkoutInfo.text = "${WorkoutUtil.getWorkoutStepsCount(currentWorkout)} intervals | ${getFormattedWorkoutTime(time)}"

        holder.itemView.setOnClickListener {
            if (holder.itemBinding.tvWorkoutDetails.visibility == View.GONE) {
                holder.itemBinding.tvWorkoutDetails.visibility = View.VISIBLE
            } else {
                holder.itemBinding.tvWorkoutDetails.visibility = View.GONE
            }

        }
        holder.itemBinding.startWorkout.setOnClickListener { view ->
            val direction = HomeFragmentDirections.actionHomeFragmentToTimerActivity(currentWorkout)
            view.findNavController().navigate(direction)
        }
        holder.itemBinding.updateWorkout.setOnClickListener { view ->
            val direction = HomeFragmentDirections.actionHomeFragmentToUpdateWorkoutFragment(currentWorkout)
            view.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private fun getFormattedWorkoutTime(time: Int) : String {
        return if (time % 60 < 10) {
            "${time / 60}:0${time % 60}"
        } else {
            "${time / 60}:${time % 60}"
        }

    }

}
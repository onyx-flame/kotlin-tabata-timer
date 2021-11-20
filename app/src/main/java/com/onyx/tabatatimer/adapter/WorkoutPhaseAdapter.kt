package com.onyx.tabatatimer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.onyx.tabatatimer.databinding.WorkoutPhaseLayoutAdapterBinding
import com.onyx.tabatatimer.models.Workout
import com.onyx.tabatatimer.models.WorkoutPhase

class WorkoutPhaseAdapter: RecyclerView.Adapter<WorkoutPhaseAdapter.WorkoutPhaseViewHolder>() {

    class WorkoutPhaseViewHolder(val itemBinding: WorkoutPhaseLayoutAdapterBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback =
        object : DiffUtil.ItemCallback<WorkoutPhase>() {
            override fun areItemsTheSame(oldItem: WorkoutPhase, newItem: WorkoutPhase): Boolean {
                return oldItem.number == newItem.number
            }

            override fun areContentsTheSame(oldItem: WorkoutPhase, newItem: WorkoutPhase): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutPhaseViewHolder {
        return WorkoutPhaseViewHolder(
            WorkoutPhaseLayoutAdapterBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        )
    }

    override fun onBindViewHolder(holder: WorkoutPhaseViewHolder, position: Int) {
        val currentWorkoutPhase = differ.currentList[position]
        holder.itemBinding.tvPhaseNumber.text = currentWorkoutPhase.number.toString()
        holder.itemBinding.tvPhaseTitle.text = currentWorkoutPhase.phaseTitle
        holder.itemBinding.tvPhaseDescription.text = currentWorkoutPhase.phaseDescription
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
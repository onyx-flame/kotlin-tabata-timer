package com.onyx.tabatatimer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.onyx.tabatatimer.databinding.WorkoutPhaseLayoutAdapterBinding
import com.onyx.tabatatimer.models.WorkoutPhase
import com.onyx.tabatatimer.utils.WorkoutUtil

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
        holder.itemBinding.apply {
            mcvRoot.setCardBackgroundColor(currentWorkoutPhase.color)
            tvPhaseNumber.text = currentWorkoutPhase.number.toString()
            tvPhaseTitle.text = currentWorkoutPhase.phaseTitle
            val elementsColor = WorkoutUtil.getContrastYIQ(currentWorkoutPhase.color)
            mcvRoot.strokeColor = elementsColor
            tvPhaseNumber.setTextColor(elementsColor)
            tvPhaseDescription.setTextColor(elementsColor)
            tvPhaseTitle.setTextColor(elementsColor)
        }
        val description = currentWorkoutPhase.phaseDescription
        holder.itemBinding.tvPhaseDescription.text = description
        if (description.isEmpty()) {
            holder.itemBinding.tvPhaseDescription.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
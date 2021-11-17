package com.onyx.tabatatimer.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.onyx.tabatatimer.databinding.WorkoutLayoutAdapterBinding
import com.onyx.tabatatimer.models.Workout

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
        holder.itemBinding.tvWorkoutTitle.text = currentWorkout.title
        holder.itemBinding.tvWorkoutDetails.text = "228 intervals | 13:37"

        val random = java.util.Random()
        val color = Color.argb(
            255,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )
        holder.itemBinding.viewColor.setBackgroundColor(color)

        holder.itemView.setOnClickListener { view ->
            Toast.makeText(view.context, "Future Launch!", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}
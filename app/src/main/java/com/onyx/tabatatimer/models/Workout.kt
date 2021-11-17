package com.onyx.tabatatimer.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "workouts")
@Parcelize
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val color: String,
    val prepareDescription: String?,
    val prepareTime: Int,
    val workDescription: String?,
    val workTime: Int,
    val restDescription: String?,
    val restTime: Int,
    val cyclesRestDescription: String?,
    val cyclesRestTime: Int,
    val coolDownDescription: String?,
    val coolDownTime: Int,
    val cycles: Int,
    val sets: Int
): Parcelable
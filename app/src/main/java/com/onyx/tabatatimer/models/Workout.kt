package com.onyx.tabatatimer.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "workouts")
@Parcelize
@Serializable
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val color: Int,
    val prepareDescription: String?,
    val prepareTime: Int,
    val workDescription: String?,
    val workTime: Int,
    val restDescription: String?,
    val restTime: Int,
    val cycles: Int,
    val sets: Int,
    val restBetweenSetsDescription: String?,
    val restBetweenSetsTime: Int,
    val coolDownDescription: String?,
    val coolDownTime: Int
): Parcelable
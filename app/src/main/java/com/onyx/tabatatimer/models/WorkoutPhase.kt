package com.onyx.tabatatimer.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class WorkoutPhase(
    val number: Int,
    val color: Int,
    val phaseTitle: String,
    val phaseDescription: String,
    val phaseTime: Int
): Parcelable
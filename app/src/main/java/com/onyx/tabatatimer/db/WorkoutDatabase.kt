package com.onyx.tabatatimer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.onyx.tabatatimer.models.Workout


@Database(entities = [Workout::class], version = 1)
abstract class WorkoutDatabase: RoomDatabase() {

    abstract fun getWorkoutDao(): WorkoutDao


    companion object {

        @Volatile
        private var instance: WorkoutDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK) {
            instance?:
            createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            WorkoutDatabase::class.java,
            "workouts_db"
        ).build()

    }

}
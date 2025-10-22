package com.example.persistent.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.presistent.api.PersistentWorkoutManager
import kotlinx.serialization.json.Json

fun PersistentWorkoutManager(context: Context): PersistentWorkoutManager =
    PersistentWorkoutManagerImpl(
        workoutDao = Room
            .databaseBuilder(context, AppDatabase::class.java, "workout-database")
            .build()
            .workoutDao()
    )

internal class Converters {
    @TypeConverter
    fun fromStringList(list: List<ExerciseEntity>) = Json.encodeToString(list)

    @TypeConverter
    fun toStringList(data: String) = Json.decodeFromString<List<ExerciseEntity>>(data)
}

@Database(entities = [ExerciseEntity::class, WorkoutEntity::class], version = 1)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}
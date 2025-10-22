package com.example.persistent.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface WorkoutDao {
    @Query("SELECT * FROM ExerciseEntity")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg exercises: ExerciseEntity)

    @Query("SELECT * FROM WorkoutEntity")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkout(workoutEntity: WorkoutEntity)

    @Query("SELECT * FROM ExerciseEntity WHERE uuid = :exerciseUUID")
    suspend fun getAllExercises(exerciseUUID: String): ExerciseEntity

    @Query("DELETE FROM ExerciseEntity WHERE uuid = :exerciseUUID")
    suspend fun deleteByUUID(exerciseUUID: String)
}
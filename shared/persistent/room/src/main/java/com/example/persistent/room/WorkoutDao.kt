package com.example.persistent.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface WorkoutDao {
    @Query("SELECT * FROM ExerciseEntity ORDER BY date DESC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg exercises: ExerciseEntity)

    @Query("SELECT * FROM WorkoutEntity ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkout(workoutEntity: WorkoutEntity)

    @Query("SELECT * FROM ExerciseEntity WHERE uuid = :exerciseUUID")
    suspend fun getExerciseByUUID(exerciseUUID: String): ExerciseEntity

    @Query("DELETE FROM ExerciseEntity WHERE uuid = :exerciseUUID")
    suspend fun deleteExerciseByUUID(exerciseUUID: String)

    @Query("SELECT * FROM WorkoutEntity WHERE uuid = :workoutUUID")
    suspend fun getWorkoutByUUID(workoutUUID: String): WorkoutEntity

    @Query("DELETE FROM WorkoutEntity WHERE uuid = :workoutUUID")
    suspend fun deleteWorkoutByUUID(workoutUUID: String)
}
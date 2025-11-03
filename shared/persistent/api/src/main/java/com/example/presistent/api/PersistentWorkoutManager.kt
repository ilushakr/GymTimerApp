package com.example.presistent.api

import kotlinx.coroutines.flow.Flow

interface PersistentWorkoutManager {
    fun exercisesFlow(): Flow<List<ExercisePersistentModel>>
    suspend fun saveExercises(vararg exercises: ExercisePersistentModel): Result<Unit>
    fun workoutsFlow(): Flow<List<WorkoutPersistentModel>>
    suspend fun saveWorkout(workoutPersistentModel: WorkoutPersistentModel): Result<Unit>
    suspend fun getExerciseByUUID(uuid: String): Result<ExercisePersistentModel>
    suspend fun deleteExerciseByUUID(uuid: String): Result<Unit>
    suspend fun getWorkoutByUUID(uuid: String): Result<WorkoutPersistentModel>
    suspend fun deleteWorkoutByUUID(uuid: String): Result<Unit>
}
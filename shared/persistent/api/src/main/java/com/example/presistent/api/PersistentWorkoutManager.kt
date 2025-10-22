package com.example.presistent.api

import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface PersistentWorkoutManager {
    fun exercisesFlow(): Flow<List<ExercisePersistentModel>>
    suspend fun saveExercises(vararg exercises: ExercisePersistentModel): Result<Unit>
    fun workoutsFlow(): Flow<List<WorkoutPersistentModel>>
    suspend fun saveWorkout(workoutPersistentModel: WorkoutPersistentModel): Result<Unit>

    suspend fun getByUUID(uuid: String): Result<ExercisePersistentModel>
    suspend fun deleteByUUID(uuid: String): Result<Unit>
}
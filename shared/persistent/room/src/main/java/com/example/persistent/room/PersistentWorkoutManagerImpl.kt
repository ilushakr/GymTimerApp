package com.example.persistent.room

import com.example.persistent.room.ExerciseEntity.Companion.toDataModel
import com.example.persistent.room.WorkoutEntity.Companion.toDataModel
import com.example.presistent.api.ExercisePersistentModel
import com.example.presistent.api.PersistentWorkoutManager
import com.example.presistent.api.WorkoutPersistentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class PersistentWorkoutManagerImpl(
    private val workoutDao: WorkoutDao
) : PersistentWorkoutManager {
    override fun exercisesFlow() = workoutDao
        .getAllExercises()
        .map { it.map(ExerciseEntity::domainModel) }

    override suspend fun saveExercises(vararg exercises: ExercisePersistentModel) =
        dispatch {
            workoutDao.insertAll(*exercises.map { it.toDataModel() }.toTypedArray())
        }

    override fun workoutsFlow() = workoutDao
        .getAllWorkouts()
        .map { it.map(WorkoutEntity::domainModel) }

    override suspend fun saveWorkout(workoutPersistentModel: WorkoutPersistentModel) =
        dispatch {
            workoutDao.insertWorkout(workoutPersistentModel.toDataModel())
        }

    override suspend fun getByUUID(uuid: String): Result<ExercisePersistentModel> {
        return dispatch {
            workoutDao.getAllExercises(uuid).domainModel
        }
    }

    override suspend fun deleteByUUID(uuid: String): Result<Unit> {
        return dispatch { workoutDao.deleteByUUID(uuid) }
    }

    private suspend fun <T> dispatch(block: suspend () -> T) = withContext(Dispatchers.IO) {
        runCatching {
            block.invoke()
        }
    }
}
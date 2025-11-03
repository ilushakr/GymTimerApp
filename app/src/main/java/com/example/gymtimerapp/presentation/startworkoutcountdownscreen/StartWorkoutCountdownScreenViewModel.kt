package com.example.gymtimerapp.presentation.startworkoutcountdownscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtimer.GymTimer
import com.example.gymtimerapp.domain.ConnectionManager
import com.example.gymtimerapp.navigation.NavigationManager
import com.example.presistent.api.PersistentWorkoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StartWorkoutCountdownScreenViewModel(
    private val uuidOfWorkoutToStart: String,
    private val shouldSaveWorkoutToPersistent: Boolean,
    private val persistentWorkoutManager: PersistentWorkoutManager,
    private val navigationManager: NavigationManager,
    private val connectionManager: ConnectionManager,
) : ViewModel() {
    private val _state = MutableStateFlow("Start workout in 3")
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val workoutPersistentModel =
                persistentWorkoutManager.getWorkoutByUUID(uuidOfWorkoutToStart).getOrNull()
                    ?: return@launch

            if (shouldSaveWorkoutToPersistent.not()) {
                launch {
                    persistentWorkoutManager.deleteWorkoutByUUID(uuidOfWorkoutToStart)
                }
            }

            repeat(3) {
                _state.value = "Start workout in ${3 - it}"
                delay(1200)
            }

            launch {
                connectionManager.startWearableActivity(
                    GymTimer.WorkoutModel(
                        exerciseList = workoutPersistentModel.exerciseList.map { exerciseModel ->
                            GymTimer.ExerciseModel(
                                name = exerciseModel.name,
                                numberOfSets = exerciseModel.numberOfSets,
                                workDuration = exerciseModel.workDuration,
                                restDuration = exerciseModel.restDuration,
                                finishWorkRemainingDuration = exerciseModel.finishWorkRemainingDuration,
                                finishRestRemainingDuration = exerciseModel.finishRestRemainingDuration,
                            )
                        }
                    )
                )
            }

            _state.value = "GO GO GO"

            delay(1500)

            navigationManager.navigateUp()
        }
    }
}
package com.example.gymtimerapp.presentation.newexercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtimerapp.navigation.NavigationManager
import com.example.presistent.api.ExercisePersistentModel
import com.example.presistent.api.PersistentWorkoutManager
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class NewExerciseViewModel(
    private val persistentWorkoutManager: PersistentWorkoutManager,
    private val navigationManager: NavigationManager
) : ViewModel() {
    var exerciseName = ""
    var setCount = 0
    var workDuration = 0
    var restDuration = 0
    var workRemaining: Int? = null
    var restRemaining: Int? = null

    fun save() {
        viewModelScope.launch {
            persistentWorkoutManager.saveExercises(
                ExercisePersistentModel(
                    name = exerciseName,
                    numberOfSets = setCount,
                    workDuration = workDuration.seconds,
                    restDuration = restDuration.seconds,
                    finishWorkRemainingDuration = workRemaining?.seconds ?: Duration.ZERO,
                    finishRestRemainingDuration = restRemaining?.seconds ?: Duration.ZERO,
                    uuid = UUID.randomUUID().toString()
                )
            )
            navigationManager.navigateUp()
        }
    }

    fun onBackClick() {
        navigationManager.navigateUp()
    }
}
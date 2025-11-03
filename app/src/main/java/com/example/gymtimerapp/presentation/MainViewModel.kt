package com.example.gymtimerapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtimerapp.domain.ConnectionManager
import com.example.gymtimerapp.navigation.NavigationManager
import com.example.presistent.api.ExercisePersistentModel
import com.example.presistent.api.PersistentWorkoutManager
import com.example.presistent.api.WorkoutPersistentModel
import com.example.shared.connectivity.data.HandheldConnectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MainViewModel(
    private val connectionManager: ConnectionManager,
    private val navigationManager: NavigationManager,
    private val persistentWorkoutManager: PersistentWorkoutManager,
) : ViewModel() {
//
////    val connectionState = connectionManager.state
//    val connectionState = MutableStateFlow(HandheldConnectionRepository.ConnectionState.Connected)
//
//    val navigationEvent = navigationManager.navigationFlow
//
//    val startDestination = navigationManager.startScreen.destination
//
//    init {
////        viewModelScope.launch(Dispatchers.IO) {
////            val t = persistentWorkoutManager.saveExercises(
////                ExercisePersistentModel(
////                    name = "Pull ups",
////                    numberOfSets = 6,
////                    workDuration = 1.minutes + 40.seconds,
////                    restDuration = 1.minutes,
////                    finishWorkRemainingDuration = 10.seconds,
////                    finishRestRemainingDuration = 15.seconds,
////                    uuid = UUID.randomUUID().toString()
////                )
////            )
////            t
////        }
//    }

    init {

        viewModelScope.launch {
            val workout = List(20){
                WorkoutPersistentModel(
                    name = "workout -> $it",
                    exerciseList = List(20){
                        ExercisePersistentModel(
                            name = "item -> $it",
                            numberOfSets = it,
                            workDuration = it.minutes,
                            restDuration = it.minutes,
                            uuid = UUID.randomUUID().toString()
                        )
                    }.also {
                        persistentWorkoutManager.saveExercises(*it.toTypedArray())
                    },
                    uuid = UUID.randomUUID().toString()
                )
            }
            workout.forEach {
                persistentWorkoutManager.saveWorkout(it)
            }
        }
    }
//
}
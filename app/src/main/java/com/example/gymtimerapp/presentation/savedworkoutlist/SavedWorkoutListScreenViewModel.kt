package com.example.gymtimerapp.presentation.savedworkoutlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtimerapp.navigation.NavigationManager
import com.example.presistent.api.ExercisePersistentModel
import com.example.presistent.api.PersistentWorkoutManager
import com.example.presistent.api.WorkoutPersistentModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn

class SavedWorkoutListScreenViewModel(
    private val navigationManager: NavigationManager,
    private val persistentWorkoutManager: PersistentWorkoutManager
) : ViewModel() {

    private var firstEmission = true

    @OptIn(FlowPreview::class)
    val state = persistentWorkoutManager
        .workoutsFlow()
        .debounce {
            when(firstEmission){
                true -> {
                    firstEmission = false
                    1000
                }
                false -> 0
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    fun onItemClick(item: WorkoutPersistentModel) {
//        navigationManager.navigateToDeleteExerciseScreen(item.uuid)
    }

    fun onAddClick() {
        navigationManager.navigateToNewWorkoutScreen()
    }
}
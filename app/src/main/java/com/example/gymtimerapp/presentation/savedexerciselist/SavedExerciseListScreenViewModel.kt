package com.example.gymtimerapp.presentation.savedexerciselist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtimerapp.navigation.NavigationManager
import com.example.presistent.api.ExercisePersistentModel
import com.example.presistent.api.PersistentWorkoutManager
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn

class SavedExerciseListScreenViewModel(
    private val navigationManager: NavigationManager,
    private val persistentWorkoutManager: PersistentWorkoutManager
) : ViewModel() {

    private var firstEmission = true

    @OptIn(FlowPreview::class)
    val state = persistentWorkoutManager
        .exercisesFlow()
        .debounce {
            when(firstEmission){
                true -> {
                    firstEmission = false
                    300
                }
                false -> 0
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    fun onItemClick(item: ExercisePersistentModel) {
        navigationManager.navigateToDeleteExerciseScreen(item.uuid)
    }

    fun onAddClick() {
        navigationManager.navigateToNewExerciseScreen()
    }
}
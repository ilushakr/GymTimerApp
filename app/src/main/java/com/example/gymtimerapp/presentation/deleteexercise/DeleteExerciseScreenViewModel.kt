package com.example.gymtimerapp.presentation.deleteexercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtimerapp.navigation.NavigationManager
import com.example.presistent.api.ExercisePersistentModel
import com.example.presistent.api.PersistentWorkoutManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeleteExerciseScreenViewModel(
    private val uuidOfExerciseForDelete: String,
    private val persistentWorkoutManager: PersistentWorkoutManager,
    private val navigationManager: NavigationManager
): ViewModel() {
    private val _state = MutableStateFlow<ExercisePersistentModel?>(null)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = persistentWorkoutManager.getExerciseByUUID(uuidOfExerciseForDelete).getOrNull()
        }
    }

    fun onCancelClick() {
        navigationManager.navigateUp()
    }

    fun onDeleteClick() {
        _state.value?.uuid?.let { uuid ->
            viewModelScope.launch {
                persistentWorkoutManager.deleteExerciseByUUID(uuid)
                navigationManager.navigateUp()
            }
        } ?: navigationManager.navigateUp()
    }
}
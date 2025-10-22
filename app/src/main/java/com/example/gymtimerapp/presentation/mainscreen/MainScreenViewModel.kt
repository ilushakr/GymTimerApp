package com.example.gymtimerapp.presentation.mainscreen

import androidx.lifecycle.ViewModel
import com.example.gymtimerapp.navigation.NavigationManager
import kotlinx.coroutines.flow.MutableStateFlow

class MainScreenViewModel(private val navigationManager: NavigationManager): ViewModel() {

    val state = MutableStateFlow(MainScreenItem.entries.toList())

    fun onItemClick(item: MainScreenItem) {
        when(item) {
            MainScreenItem.SavedExerciseList -> navigationManager.navigateToSavedExerciseListScreen()
            MainScreenItem.SavedWorkoutList -> navigationManager.navigateToSavedWorkoutListScreen()
            MainScreenItem.SetupWorkoutOnWearables -> TODO()
        }
    }

    enum class MainScreenItem(val title: String) {
        SavedWorkoutList("Saved workout list"),
        SavedExerciseList("Saved exercise list"),
        SetupWorkoutOnWearables("Setup workout on wearables"),
    }
}
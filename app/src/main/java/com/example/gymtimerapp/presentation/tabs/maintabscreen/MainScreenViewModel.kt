package com.example.gymtimerapp.presentation.tabs.maintabscreen

import androidx.lifecycle.ViewModel
import com.example.gymtimerapp.navigation.NavigationManager

class MainScreenViewModel(private val navigationManager: NavigationManager) : ViewModel() {

    fun onStartNewWorkout() {
        navigationManager.navigateToNewWorkoutScreen(false)
    }

    fun onStartSavedWorkout() {
        navigationManager.navigateToSavedWorkoutListScreen(true)
    }
}
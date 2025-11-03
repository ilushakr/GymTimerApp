package com.example.gymtimerapp.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NavigationManager {
    val scope = CoroutineScope(Dispatchers.Main)

    private val _navigationFlow = MutableSharedFlow<NavigationEvent>()
    val navigationFlow = _navigationFlow.asSharedFlow()

    val startScreen = Screens.MainScreen

    fun navigateUp(args: Any? = null) {
        scope.launch { _navigationFlow.emit(NavigationEvent.NavigateUp(args)) }
    }

    fun navigateToNewExerciseScreen(){
        Screens.NewExerciseScreen.navigate()
    }

    fun navigateToSavedExerciseListScreen(){
        Screens.SavedExerciseListScreen.navigate()
    }

    fun navigateToSavedWorkoutListScreen(){
        Screens.SavedWorkoutListScreen.navigate()
    }

    fun navigateToDeleteExerciseScreen(uuid: String) {
        Screens.DeleteExerciseScreen.withArgs(uuid).navigate()
    }

    fun navigateToDeleteWorkoutScreen(uuid: String) {
        Screens.DeleteWorkoutScreen.withArgs(uuid).navigate()
    }

    fun navigateToNewWorkoutScreen() {
        Screens.NewWorkoutScreen.navigate()
    }

    private fun Screens.navigate() {
        scope.launch { _navigationFlow.emit(NavigationEvent.NavigateToScreen(this@navigate.route)) }
    }

    private fun String.navigate() {
        scope.launch { _navigationFlow.emit(NavigationEvent.NavigateToScreen(this@navigate)) }
    }

    sealed interface NavigationEvent {
        @JvmInline
        value class NavigateToScreen(val route: String): NavigationEvent

        data class NavigateUp(val args: Any?): NavigationEvent
    }
}
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

    val tabItemStartScreen = BaseScreens.MainTabScreen

    val startScreen = BaseScreens.MainScreen

    fun showSnackBar(message: String){
        scope.launch { _navigationFlow.emit(NavigationEvent.ShowSnacBar(message)) }
    }

    fun navigateUp(args: Any? = null) {
        scope.launch { _navigationFlow.emit(NavigationEvent.NavigateUp(args)) }
    }

    fun navigateToBottomItem(item: BottomNavigationItem) {
        (item as BaseScreens).navigate(NavigationEvent.NavigateToScreen.Type.BottomNavigation)
    }

    fun navigateToNewExerciseScreen() {
        BaseScreens.NewExerciseScreen().navigate(NavigationEvent.NavigateToScreen.Type.Fullscreen)
    }

    fun navigateToSavedExerciseListScreen() {
        BaseScreens.SavedExerciseListScreen().navigate(NavigationEvent.NavigateToScreen.Type.Fullscreen)
    }

    fun navigateToSavedWorkoutListScreen(shouldSaveWorkoutToPersistent: Boolean) {
        BaseScreens.SavedWorkoutListScreen(shouldSaveWorkoutToPersistent).navigate(NavigationEvent.NavigateToScreen.Type.Fullscreen)
    }

    fun navigateToDeleteExerciseScreen(uuid: String) {
        BaseScreens.DeleteExerciseScreen(uuid).navigate(NavigationEvent.NavigateToScreen.Type.Fullscreen)
    }

    fun navigateToDeleteWorkoutScreen(uuid: String) {
        BaseScreens.DeleteWorkoutScreen(uuid).navigate(NavigationEvent.NavigateToScreen.Type.Fullscreen)
    }

    fun navigateToNewWorkoutScreen(shouldSaveWorkoutToPersistent: Boolean) {
        BaseScreens.NewWorkoutScreen(shouldSaveWorkoutToPersistent).navigate(NavigationEvent.NavigateToScreen.Type.Fullscreen)
    }

    fun navigateToStartWorkoutCountdownScreen(uuid: String, shouldSaveWorkoutToPersistent: Boolean) {
        BaseScreens.StartWorkoutCountdownScreen(
            uuid = uuid,
            shouldSaveWorkoutToPersistent = shouldSaveWorkoutToPersistent
        ).navigate(NavigationEvent.NavigateToScreen.Type.Fullscreen)
    }

    private fun BaseScreens.navigate(type: NavigationEvent.NavigateToScreen.Type) {
        scope.launch { _navigationFlow.emit(NavigationEvent.NavigateToScreen(this@navigate, type)) }
    }

    sealed interface NavigationEvent {
        data class NavigateToScreen(val screen: BaseScreens, val type: Type) : NavigationEvent {
            enum class Type {
                Fullscreen, BottomNavigation
            }
        }

        data class NavigateUp(val args: Any?) : NavigationEvent {
            object ToRoot
        }

        data class ShowSnacBar(val message: String): NavigationEvent
    }
}
package com.example.gymtimerapp.navigation

sealed class Screens(val route: String) {
    object MainScreen : Screens(route = "MainScreen")
    object SavedWorkoutListScreen : Screens(route = "SavedWorkoutListScreen")
    object SavedExerciseListScreen : Screens(route = "SavedExerciseListScreen")
    object DeleteExerciseScreen : Screens(route = "DeleteExerciseScreen")
    object NewExerciseScreen : Screens(route = "NewExerciseScreen")
    object NewWorkoutScreen : Screens(route = "NewWorkoutScreen")


    object OldMainScreen : Screens(route = "MainScreenOld")

    fun withArgs(vararg args: Any) =
        buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
}
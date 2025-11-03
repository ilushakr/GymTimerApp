package com.example.gymtimerapp.navigation

sealed class Screens(val route: String) {
    object MainScreen : Screens(route = "MainScreen")




    object OldMainScreen : Screens(route = "MainScreenOld")

    fun withArgs(vararg args: Any) =
        buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
}
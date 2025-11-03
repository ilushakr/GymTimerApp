package com.example.gymtimerapp.presentation.navigationhost

import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gymtimerapp.navigation.BaseScreens
import com.example.gymtimerapp.navigation.BottomNavigationItem
import com.example.gymtimerapp.navigation.Dialog
import com.example.gymtimerapp.navigation.NavigationManager.NavigationEvent
import com.example.gymtimerapp.navigation.routeName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Stack

class NavigatorHolder(
    eventFlow: Flow<NavigationEvent>,
    snackbarHostState: SnackbarHostState,
    val bottomSheetNavigator: BottomSheetNavigator,
    val outerNavController: NavHostController,
    val innerNavController: NavHostController,
    private val scope: CoroutineScope,
) {

    val bottomItemList = listOf<BottomNavigationItem>(
        BaseScreens.MainTabScreen(),
        BaseScreens.ProfileTabScreen(),
        BaseScreens.SettingsTabScreen(),
    )

    val currentSelectedTab = innerNavController
        .currentBackStackEntryFlow
        .map { entry ->
            bottomItemList.first { it.destination == entry.destination.route }
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = bottomItemList.first()
        )

    private val stack = Stack<Nav>()

    init {
        outerNavController
            .currentBackStackEntryFlow
            .onEach { entry -> entry.destination.route?.let { route -> stack.push(Nav.Outer(route)) } }
            .launchIn(scope)

        innerNavController
            .currentBackStackEntryFlow
            .onEach { entry -> entry.destination.route?.let { route -> stack.push(Nav.Inner(route)) } }
            .launchIn(scope)

        eventFlow
            .onEach { event ->
                when (event) {
                    is NavigationEvent.NavigateToScreen -> {
                        val screen = event.screen
                        when (screen) {
                            is BottomNavigationItem -> navigateBottom(screen)
                            is Dialog -> navigateToDialog(screen)
                            else -> navigateToScreen(event)
                        }
                    }

                    is NavigationEvent.NavigateUp -> {
                        popBackStack(event.args)
                    }

                    is NavigationEvent.ShowSnacBar -> {
                        scope.launch { snackbarHostState.showSnackbar(message = event.message) }
                    }
                }
            }
            .launchIn(scope)
    }

    private fun navigateToScreen(screen: NavigationEvent.NavigateToScreen) {
        when (screen.type) {
            NavigationEvent.NavigateToScreen.Type.Fullscreen -> {
                outerNavController.navigate(screen.screen.destination)
            }

            NavigationEvent.NavigateToScreen.Type.BottomNavigation -> {
                innerNavController.navigate(screen.screen.destination)
            }
        }
    }

    private fun navigateToDialog(screen: Dialog) {
        outerNavController.navigate(screen.destination)
    }

    private fun navigateBottom(screen: BottomNavigationItem) {
        if (currentSelectedTab.value != screen) {
            innerNavController.navigate(screen.destination) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(innerNavController.graph.startDestinationId) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        }
    }

    fun popBackStack(args: Any?) {
        val currentNavController = when (stack.pop()) {
            is Nav.Inner -> innerNavController
            is Nav.Outer -> outerNavController
        }
        when (args) {
            null -> {
                currentNavController.popBackStack()
            }

            is NavigationEvent.NavigateUp.ToRoot -> {
                currentNavController.popBackStack(
                    BaseScreens.MainScreen.routeName,
                    inclusive = false
                )
            }
        }
    }

    private sealed interface Nav {
        val route: String

        data class Outer(override val route: String) : Nav
        data class Inner(override val route: String) : Nav
    }
}

@Composable
fun rememberNavigatorHolder(
    eventFlow: Flow<NavigationEvent>,
    snackbarHostState: SnackbarHostState
): NavigatorHolder {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val innerNavController = rememberNavController()

    val scope = rememberCoroutineScope()

    return remember(
        eventFlow,
        snackbarHostState,
        bottomSheetNavigator,
        navController,
        innerNavController,
    ) {
        NavigatorHolder(
            eventFlow = eventFlow,
            snackbarHostState = snackbarHostState,
            bottomSheetNavigator = bottomSheetNavigator,
            outerNavController = navController,
            innerNavController = innerNavController,
            scope = scope
        )
    }
}
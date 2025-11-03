package com.example.gymtimerapp.presentation.navigationhost

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.BottomSheetNavigatorDestinationBuilder
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.DialogNavigatorDestinationBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.get
import androidx.navigation.navArgument
import com.example.gymtimerapp.navigation.BaseScreens
import com.example.gymtimerapp.navigation.BottomNavigationItem
import com.example.gymtimerapp.navigation.routeName
import com.example.gymtimerapp.presentation.deleteexercise.DeleteExerciseScreen
import com.example.gymtimerapp.presentation.deleteworkout.DeleteWorkoutScreen
import com.example.gymtimerapp.presentation.newexercise.NewExerciseScreen
import com.example.gymtimerapp.presentation.newworkout.NewWorkoutScreen
import com.example.gymtimerapp.presentation.savedexerciselist.SavedExerciseListScreen
import com.example.gymtimerapp.presentation.savedworkoutlist.SavedWorkoutListScreen
import com.example.gymtimerapp.presentation.startworkoutcountdownscreen.StartWorkoutCountdownScreen
import com.example.gymtimerapp.presentation.tabs.maintabscreen.MainScreen
import com.example.gymtimerapp.presentation.tabs.profiletabscreen.ProfileScreen
import com.example.gymtimerapp.presentation.tabs.settingstabscreen.SettingsScreen
import com.example.gymtimerapp.presentation.utils.BottomSheetWrapper
import com.example.gymtimerapp.presentation.utils.DialogWrapper
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationHostScreen() {
    val viewModel = koinViewModel<NavigationHostViewModel>()

    val snackbarHostState = remember { SnackbarHostState() }

    val navigatorHolder = rememberNavigatorHolder(
        eventFlow = viewModel.navigationEvent,
        snackbarHostState = snackbarHostState
    )

    SnackBarWrapper(
        modifier = Modifier.fillMaxSize(),
        snackbarHostState = snackbarHostState
    ) {
        ModalBottomSheetLayout(
            modifier = Modifier
                .imePadding()
                .fillMaxSize(),
            bottomSheetNavigator = navigatorHolder.bottomSheetNavigator,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            NavHost(
                navController = navigatorHolder.outerNavController,
                startDestination = viewModel.startDestination
            ) {

                composable(BaseScreens.MainScreen.routeName) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            BottomNavBar(
                                items = navigatorHolder.bottomItemList,
                                selectedItem = navigatorHolder.currentSelectedTab.collectAsStateWithLifecycle().value,
                                onItemClick = { viewModel.navigate(it) }
                            )
                        }
                    ) { contentPadding ->
                        NavHost(
                            navController = navigatorHolder.innerNavController,
                            startDestination = viewModel.tabItemStartScreen,
                            modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding())
                        ) {
                            composable(
                                route = BaseScreens.MainTabScreen.routeName,
                                enterTransition = {
                                    getBottomNavigationEnterTransition(
                                        navigatorHolder
                                    )
                                },
                                exitTransition = { getBottomNavigationExitTransition(navigatorHolder) },
                            ) {
                                MainScreen()
                            }

                            composable(
                                route = BaseScreens.ProfileTabScreen.routeName,
                                enterTransition = {
                                    getBottomNavigationEnterTransition(
                                        navigatorHolder
                                    )
                                },
                                exitTransition = { getBottomNavigationExitTransition(navigatorHolder) },
                            ) {
                                ProfileScreen()
                            }

                            composable(
                                route = BaseScreens.SettingsTabScreen.routeName,
                                enterTransition = {
                                    getBottomNavigationEnterTransition(
                                        navigatorHolder
                                    )
                                },
                                exitTransition = { getBottomNavigationExitTransition(navigatorHolder) },
                            ) {
                                SettingsScreen()
                            }
                        }
                    }
                }

                composableWithAnimation(
                    route = BaseScreens.SavedWorkoutListScreen.routeName + "/{shouldSaveWorkoutToPersistent}",
                    arguments = listOf(
                        navArgument("shouldSaveWorkoutToPersistent") { type = NavType.BoolType }
                    )
                ) { entry ->
                    SavedWorkoutListScreen(
                        entry.arguments?.getBoolean("shouldSaveWorkoutToPersistent") ?: false
                    )
                }

                composableWithAnimation(BaseScreens.SavedExerciseListScreen.routeName) {
                    SavedExerciseListScreen()
                }

                dialogWithWrapper(
                    route = BaseScreens.DeleteExerciseScreen.routeName + "/{uuid}",
                    arguments = listOf(
                        navArgument("uuid") { type = NavType.StringType }
                    )
                ) { entry ->
                    entry.arguments?.getString("uuid")?.let { uuid ->
                        DeleteExerciseScreen(uuid)
                    }
                }

                dialogWithWrapper(
                    route = BaseScreens.DeleteWorkoutScreen.routeName + "/{uuid}",
                    arguments = listOf(
                        navArgument("uuid") { type = NavType.StringType }
                    )
                ) { entry ->
                    entry.arguments?.getString("uuid")?.let { uuid ->
                        DeleteWorkoutScreen(uuid)
                    }
                }

                dialogWithWrapper(
                    route = BaseScreens.StartWorkoutCountdownScreen.routeName + "/{uuid}" + "/{shouldSaveWorkoutToPersistent}",
                    arguments = listOf(
                        navArgument("uuid") { type = NavType.StringType },
                        navArgument("shouldSaveWorkoutToPersistent") { type = NavType.BoolType },
                    )
                ) { entry ->
                    entry.arguments?.getString("uuid")?.let { uuid ->
                        StartWorkoutCountdownScreen(
                            uuid = uuid,
                            shouldSaveWorkoutToPersistent = entry
                                .arguments
                                ?.getBoolean("shouldSaveWorkoutToPersistent")
                                ?: false
                        )
                    }
                }

                composableWithAnimation(BaseScreens.NewExerciseScreen.routeName) {
                    NewExerciseScreen()
                }

                composableWithAnimation(
                    route = BaseScreens.NewWorkoutScreen.routeName + "/{shouldSaveWorkoutToPersistent}",
                    arguments = listOf(
                        navArgument("shouldSaveWorkoutToPersistent") { type = NavType.BoolType }
                    )
                ) { entry ->
                    NewWorkoutScreen(
                        entry.arguments?.getBoolean("shouldSaveWorkoutToPersistent") ?: false
                    )
                }
            }
        }
    }

//        Column(
//            modifier = Modifier.fillMaxSize(),
//        ) {
//
//            AnimatedVisibility(
//                visible = state == HandheldConnectionRepository.ConnectionState.Disconnected,
//                enter = expandVertically(),
//                exit = shrinkVertically()
//            ) {
//                Text(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 32.dp, vertical = 8.dp)
//                        .background(Color.Red, RoundedCornerShape(8.dp))
//                        .padding(16.dp),
//                    text = "Wearables is disconnected. Please check connection again",
//                    color = Color.White
//                )
//            }
//
//        }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun SnackBarWrapper(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) { snackbarData: SnackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    modifier = Modifier.padding(8.dp),
                    containerColor = SnackbarDefaults.color.copy(alpha = 0.95f)
                )
            }
        },
    ) { _ ->
        content()
    }
}

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    items: List<BottomNavigationItem>,
    selectedItem: BottomNavigationItem,
    onItemClick: (BottomNavigationItem) -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = item.icon(),
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(item.titleRes))
                }
            )
        }
    }
}

private fun NavGraphBuilder.dialogWithWrapper(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    dialogProperties: DialogProperties = DialogProperties(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    destination(
        DialogNavigatorDestinationBuilder(
            provider[DialogNavigator::class],
            route,
            dialogProperties,
            {
                DialogWrapper {
                    content(it)
                }
            }
        )
            .apply {
                arguments.forEach { (argumentName, argument) ->
                    argument(
                        argumentName,
                        argument
                    )
                }
                deepLinks.forEach { deepLink -> deepLink(deepLink) }
            }
    )
}

private fun NavGraphBuilder.composableWithAnimation(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = { getNavigationEnterTransition() },
        exitTransition = { getNavigationExitTransition() },
        popEnterTransition = { getNavigationPopEnterTransition() },
        popExitTransition = { getNavigationPopExitTransition() },
        content = content
    )
}

private fun NavGraphBuilder.bottomSheetWithWrapper(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit,
) {
    destination(
        BottomSheetNavigatorDestinationBuilder(
            provider[BottomSheetNavigator::class],
            route,
            {
                BottomSheetWrapper(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                ) {
                    content(it)
                }
            },
        )
            .apply {
                arguments.fastForEach { (argumentName, argument) ->
                    argument(argumentName, argument)
                }
                deepLinks.fastForEach { deepLink -> deepLink(deepLink) }
            }
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.getBottomNavigationEnterTransition(
    holder: NavigatorHolder
): EnterTransition {
    val from =
        holder.bottomItemList.indexOfFirst { it.destination == this.initialState.destination.route }
    val to =
        holder.bottomItemList.indexOfFirst { it.destination == this.targetState.destination.route }

    return when (from < to) {
        true -> slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = tween(durationMillis = 200)
        )

        false -> slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End,
            animationSpec = tween(durationMillis = 200)
        )
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.getBottomNavigationExitTransition(
    holder: NavigatorHolder
): ExitTransition {
    val from =
        holder.bottomItemList.indexOfFirst { it.destination == this.initialState.destination.route }
    val to =
        holder.bottomItemList.indexOfFirst { it.destination == this.targetState.destination.route }

    return when (from < to) {
        true -> slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = tween(durationMillis = 200)
        )

        false -> slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End,
            animationSpec = tween(durationMillis = 200)
        )
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.getNavigationEnterTransition(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Up,
        animationSpec = tween(durationMillis = 200)
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.getNavigationExitTransition(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Up,
        animationSpec = tween(durationMillis = 200)
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.getNavigationPopEnterTransition(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Down,
        animationSpec = tween(durationMillis = 200)
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.getNavigationPopExitTransition(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Down,
        animationSpec = tween(durationMillis = 200)
    )
}
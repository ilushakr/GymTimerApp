package com.example.gymtimerapp.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.gymtimerapp.R

sealed interface BottomNavigationItem {
    val titleRes: Int

    val destination: String

    @Composable
    fun icon(): ImageVector
}

sealed interface Dialog {
    val destination: String
}

sealed interface Screen {
    val destination: String
}

sealed class BaseScreens(private val route: String, vararg args: Any = arrayOf()) {
    open val destination: String = buildString {
        append(route)
        if (args.isNotEmpty()) {
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    /**
     * BottomNavigation items
     */
    class MainTabScreen : BaseScreens(MainTabScreen.routeName), BottomNavigationItem {
        @StringRes
        override val titleRes = R.string.main_tab_title

        @Composable
        override fun icon() = ImageVector.vectorResource(id = R.drawable.ic_gym_kit_filled)

        companion object
    }

    class SettingsTabScreen : BaseScreens(SettingsTabScreen.routeName), BottomNavigationItem {
        @StringRes
        override val titleRes = R.string.settings_tab_title

        @Composable
        override fun icon() = Icons.Default.Settings

        companion object
    }

    class ProfileTabScreen : BaseScreens(ProfileTabScreen.routeName), BottomNavigationItem {
        @StringRes
        override val titleRes = R.string.profile_tab_title

        //        override val icon = Icons.Default.Person
        @Composable
        override fun icon() = Icons.Default.Person

        companion object
    }


    class MainScreen : BaseScreens(MainScreen.routeName), Screen {
        companion object
    }

    class SavedWorkoutListScreen(shouldSaveWorkoutToPersistent: Boolean) :
        BaseScreens(SavedWorkoutListScreen.routeName, shouldSaveWorkoutToPersistent), Screen {
        companion object
    }

    class SavedExerciseListScreen : BaseScreens(SavedExerciseListScreen.routeName), Screen {
        companion object
    }

    class DeleteExerciseScreen(uuid: String) : BaseScreens(DeleteExerciseScreen.routeName, uuid),
        Dialog {
        companion object
    }

    class DeleteWorkoutScreen(uuid: String) : BaseScreens(DeleteWorkoutScreen.routeName, uuid),
        Dialog {
        companion object
    }

    class NewExerciseScreen : BaseScreens(NewExerciseScreen.routeName), Dialog {
        companion object
    }

    class NewWorkoutScreen(shouldSaveWorkoutToPersistent: Boolean) :
        BaseScreens(NewWorkoutScreen.routeName, shouldSaveWorkoutToPersistent), Screen {
        companion object
    }

    class StartWorkoutCountdownScreen(uuid: String, shouldSaveWorkoutToPersistent: Boolean) :
        BaseScreens(StartWorkoutCountdownScreen.routeName, uuid, shouldSaveWorkoutToPersistent), Dialog {

        companion object
    }
}

inline val <reified T : Any> T.routeName: String
    @Suppress("UsePropertyAccessSyntax")
    get() {
        return this.javaClass.name.orEmpty()
    }

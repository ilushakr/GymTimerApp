package com.example.gymtimerapp

import android.app.Application
import com.example.gymtimer.GymTimer
import com.example.gymtimerapp.domain.ConnectionManager
import com.example.gymtimerapp.navigation.NavigationManager
import com.example.gymtimerapp.presentation.MainViewModel
import com.example.gymtimerapp.presentation.deleteexercise.DeleteExerciseScreenViewModel
import com.example.gymtimerapp.presentation.deleteworkout.DeleteWorkoutScreenViewModel
import com.example.gymtimerapp.presentation.navigationhost.NavigationHostViewModel
import com.example.gymtimerapp.presentation.newexercise.NewExerciseViewModel
import com.example.gymtimerapp.presentation.newworkout.NewWorkoutViewModel
import com.example.gymtimerapp.presentation.savedexerciselist.SavedExerciseListScreenViewModel
import com.example.gymtimerapp.presentation.savedworkoutlist.SavedWorkoutListScreenViewModel
import com.example.gymtimerapp.presentation.startworkoutcountdownscreen.StartWorkoutCountdownScreenViewModel
import com.example.gymtimerapp.presentation.tabs.maintabscreen.MainScreenViewModel
import com.example.gymtimerapp.presentation.tabs.profiletabscreen.ProfileScreenViewModel
import com.example.gymtimerapp.presentation.tabs.settingstabscreen.SettingsScreenViewModel
import com.example.persistent.room.PersistentWorkoutManager
import com.example.shared.connectivity.data.ConnectionRepositoryImpl
import com.example.shared.connectivity.data.HandheldConnectionRepository
import com.example.stopwatch.StopWatch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                module {
                    single { GymTimer(StopWatch(get())) }
                },
                module {
                    viewModelOf(::MainViewModel)
                    viewModelOf(::NavigationHostViewModel)
                    viewModelOf(::MainScreenViewModel)
                    viewModelOf(::ProfileScreenViewModel)
                    viewModelOf(::SettingsScreenViewModel)
                    viewModelOf(::SavedExerciseListScreenViewModel)
                    viewModelOf(::SavedWorkoutListScreenViewModel)
                    viewModelOf(::DeleteExerciseScreenViewModel)
                    viewModelOf(::DeleteWorkoutScreenViewModel)
                    viewModelOf(::NewExerciseViewModel)
                    viewModelOf(::NewWorkoutViewModel)
                    viewModelOf(::StartWorkoutCountdownScreenViewModel)
                },
                module {
                    singleOf(::ConnectionManager)
                    singleOf(::ConnectionRepositoryImpl).bind(HandheldConnectionRepository::class)
                    singleOf(::PersistentWorkoutManager)
                    singleOf(::NavigationManager)
                }
            )
        }
    }
}
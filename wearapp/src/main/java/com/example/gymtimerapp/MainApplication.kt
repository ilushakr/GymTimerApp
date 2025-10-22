package com.example.gymtimerapp

import android.app.Application
import com.example.gymtimer.GymTimer
import com.example.stopwatch.StopWatch
import com.example.gymtimerapp.di.workoutModule
import com.example.persistent.room.PersistentWorkoutManager
import com.example.shared.connectivity.data.ConnectionRepositoryImpl
import com.example.shared.connectivity.data.HandheldConnectionRepository
import com.example.shared.connectivity.data.WearableConnectionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                module { single { GymTimer(StopWatch(get())) } },
                workoutModule,
                module { singleOf(::ConnectionRepositoryImpl).bind(WearableConnectionRepository::class) },
                module { singleOf(::PersistentWorkoutManager) },
            )
        }
    }
}
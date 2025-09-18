package com.example.wearapp

import android.app.Application
import com.example.gymtimer.GymTimer
import com.example.stopwatch.StopWatch
import com.example.wearapp.di.workoutModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
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
            )
        }
    }
}
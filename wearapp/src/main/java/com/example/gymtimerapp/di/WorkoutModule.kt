package com.example.gymtimerapp.di

import com.example.gymtimerapp.data.WorkoutRepository
import com.example.gymtimerapp.presentation.WorkoutViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val workoutModule = module {
    viewModelOf(::WorkoutViewModel)
    singleOf(::WorkoutRepository)
}
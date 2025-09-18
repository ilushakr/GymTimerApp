package com.example.wearapp.di

import com.example.wearapp.data.WorkoutRepository
import com.example.wearapp.presentation.WorkoutViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val workoutModule = module {
    viewModelOf(::WorkoutViewModel)
    singleOf(::WorkoutRepository)
}
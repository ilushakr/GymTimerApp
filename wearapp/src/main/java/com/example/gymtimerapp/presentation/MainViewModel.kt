package com.example.gymtimerapp.presentation

import androidx.lifecycle.ViewModel
import com.example.gymtimer.GymTimer
import com.example.gymtimerapp.domain.WorkoutManager
import com.example.shared.connectivity.data.WorkoutDataModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel(private val workoutManager: WorkoutManager): ViewModel() {

    val workoutState = MutableStateFlow<>

    fun setWorkout(workoutModel: WorkoutDataModel?) {
        workoutModel?.domainModelPresentation?.let {
            workoutManager.currentWorkout = it
        }
    }
}
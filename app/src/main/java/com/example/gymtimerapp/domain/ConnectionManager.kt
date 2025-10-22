package com.example.gymtimerapp.domain

import com.example.gymtimer.GymTimer
import com.example.shared.connectivity.data.HandheldConnectionRepository

class ConnectionManager(private val repository: HandheldConnectionRepository) {
    val state = repository.connectionState

    fun startWearableActivity(workoutModel: GymTimer.WorkoutModel) {
        repository.startWearableActivity(workoutModel)
    }
}
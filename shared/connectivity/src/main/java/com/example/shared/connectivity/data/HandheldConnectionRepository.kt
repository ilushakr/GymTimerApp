package com.example.shared.connectivity.data

import com.example.gymtimer.GymTimer
import kotlinx.coroutines.flow.StateFlow

interface HandheldConnectionRepository {
    val connectionState: StateFlow<ConnectionState>

    enum class ConnectionState {
        Connected, Disconnected
    }

    fun startWearableActivity(workoutModel: GymTimer.WorkoutModel)
}
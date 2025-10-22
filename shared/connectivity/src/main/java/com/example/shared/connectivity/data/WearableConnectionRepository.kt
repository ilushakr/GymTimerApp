package com.example.shared.connectivity.data

import com.example.gymtimer.GymTimer
import kotlinx.coroutines.flow.SharedFlow

interface WearableConnectionRepository {
    val messageSharedFlow: SharedFlow<Message>

    sealed interface Message {
        data object Empty: Message
        data class NewWorkoutModel(val model: GymTimer.WorkoutModel) : Message
    }
}
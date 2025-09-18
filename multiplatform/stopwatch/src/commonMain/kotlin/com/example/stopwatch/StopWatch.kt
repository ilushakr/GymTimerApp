package com.example.stopwatch

import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

fun interface TickListener {
    fun tick(tick: Long)
}

enum class TimerState {
    Prepared, Running, Paused
}

expect class StopWatch {
    val tickFlow: StateFlow<Duration>

    val timerState: StateFlow<TimerState>

    fun start()
    fun pause()
    fun stopAndReset()
}
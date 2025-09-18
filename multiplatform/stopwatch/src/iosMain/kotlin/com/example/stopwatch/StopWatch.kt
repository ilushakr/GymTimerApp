package com.example.stopwatch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

actual class StopWatch{
    actual val tickFlow: StateFlow<Duration> = MutableStateFlow(Duration.ZERO)

    actual val timerState: StateFlow<TimerState> = MutableStateFlow(TimerState.Prepared)

    actual fun start() {
    }

    actual fun pause() {
    }

    actual fun stopAndReset() {
    }

}
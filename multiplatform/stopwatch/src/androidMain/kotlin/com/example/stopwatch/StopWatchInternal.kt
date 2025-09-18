package com.example.stopwatch

import android.os.SystemClock
import kotlin.math.max

/**
 * A read-only domain object representing a stopwatch.
 */
class StopwatchInternal internal constructor(
    /**
     * Current state of this stopwatch.
     */
    val state: State,
    /**
     * Elapsed time in ms the stopwatch was last started; [.UNUSED] if not running.
     */
    val lastStartTime: Long,
    /**
     * The time since epoch at which the stopwatch was last started.
     */
    val lastWallClockTime: Long,
    /**
     * Elapsed time in ms this stopwatch has accumulated while running.
     */
    val accumulatedTime: Long
) {
    /**
     * @return the amount of time accumulated up to the last time the stopwatch was started
     */

    val isReset: Boolean
        get() = state == State.RESET

    val isPaused: Boolean
        get() = state == State.PAUSED

    val isRunning: Boolean
        get() = state == State.RUNNING

    val totalTime: Long
        /**
         * @return the total amount of time accumulated up to this moment
         */
        get() {
            if (state != State.RUNNING) {
                return accumulatedTime
            }

            // In practice, "now" can be any value due to device reboots. When the real-time clock
            // is reset, there is no more guarantee that "now" falls after the last start time. To
            // ensure the stopwatch is monotonically increasing, normalize negative time segments to 0,
            val timeSinceStart: Long = now() - lastStartTime
            return (accumulatedTime + max(0.0, timeSinceStart.toDouble())).toLong()
        }

    /**
     * @return a copy of this stopwatch that is running
     */
    fun start(): StopwatchInternal {
        if (state == State.RUNNING) {
            return this
        }

        return StopwatchInternal(State.RUNNING, now(), wallClock(), totalTime)
    }

    /**
     * @return a copy of this stopwatch that is paused
     */
    fun pause(): StopwatchInternal {
        if (state != State.RUNNING) {
            return this
        }

        return StopwatchInternal(
            State.PAUSED, UNUSED, UNUSED,
            totalTime
        )
    }

    /**
     * @return a copy of this stopwatch that is reset
     */
    fun reset(): StopwatchInternal {
        return RESET_STOPWATCHInternal
    }

    /**
     * @return this Stopwatch if it is not running or an updated version based on wallclock time.
     * The internals of the stopwatch are updated using the wallclock time which is durable
     * across reboots.
     */
    fun updateAfterReboot(): StopwatchInternal {
        if (state != State.RUNNING) {
            return this
        }
        val timeSinceBoot: Long = now()
        val wallClockTime: Long = wallClock()
        // Avoid negative time deltas. They can happen in practice, but they can't be used. Simply
        // update the recorded times and proceed with no change in accumulated time.
        val delta =
            max(0.0, (wallClockTime - lastWallClockTime).toDouble()).toLong()
        return StopwatchInternal(state, timeSinceBoot, wallClockTime, accumulatedTime + delta)
    }

    /**
     * @return this Stopwatch if it is not running or an updated version based on the realtime.
     * The internals of the stopwatch are updated using the realtime clock which is accurate
     * across wallclock time adjustments.
     */
    fun updateAfterTimeSet(): StopwatchInternal {
        if (state != State.RUNNING) {
            return this
        }
        val timeSinceBoot: Long = now()
        val wallClockTime: Long = wallClock()
        val delta = timeSinceBoot - lastStartTime
        if (delta < 0) {
            // Avoid negative time deltas. They typically happen following reboots when TIME_SET is
            // broadcast before BOOT_COMPLETED. Simply ignore the time update and hope
            // updateAfterReboot() can successfully correct the data at a later time.
            return this
        }
        return StopwatchInternal(state, timeSinceBoot, wallClockTime, accumulatedTime + delta)
    }

    enum class State {
        RESET, RUNNING, PAUSED
    }

    companion object {
        const val UNUSED: Long = Long.MIN_VALUE

        /**
         * The single, immutable instance of a reset stopwatch.
         */
        private val RESET_STOPWATCHInternal = StopwatchInternal(State.RESET, UNUSED, UNUSED, 0)

        fun now() = SystemClock.elapsedRealtime()
        fun wallClock() = System.currentTimeMillis()
    }

}
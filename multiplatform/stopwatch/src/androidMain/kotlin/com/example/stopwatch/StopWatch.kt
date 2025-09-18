package com.example.stopwatch

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlin.math.max
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.Duration

actual class StopWatch(private val context: Context) {

    private val runnable = TimeUpdateRunnable()
    private val handler = Handler(Looper.getMainLooper())

    private val listenerList = mutableListOf<TickListener>()
    private val _tickFlow = MutableStateFlow(getCurrentTime().toDuration(DurationUnit.MILLISECONDS))

    actual val tickFlow = _tickFlow.asStateFlow()

    private val _timerState = MutableStateFlow(TimerState.Prepared)

    actual val timerState = _timerState.asStateFlow()

    actual fun start(){
        _timerState.value = TimerState.Running
        doStart()
    }

    actual fun pause() {
        _timerState.value = TimerState.Paused
        doPause()
    }

    actual fun stopAndReset() {
        _timerState.value = TimerState.Prepared
        doReset()
    }

    private fun doStart() {
        startUpdatingTime()
        enforceMainLooper()
        val sW = getStopWatch(context).start()
        setStopwatch(sW)
    }

    /**
     * Pause the stopwatch.
     */
    private fun doPause() {
        enforceMainLooper()
        setStopwatch(getStopWatch(context).pause())
    }

    /**
     * Reset the stopwatch.
     */
    private fun doReset() {
        stopUpdatingTime()
        val priorState: StopwatchInternal.State = getStopWatch(context).state

        enforceMainLooper()
        setStopwatch(getStopWatch(context).reset())
        _tickFlow.tryEmit(Duration.ZERO)
    }

    fun setListener(listener: TickListener){
        listenerList.add(listener)
    }

    fun removeListener(listener: TickListener){
        listenerList.remove(listener)
    }

    /**
     * Post the first runnable to update times within the UI. It will reschedule itself as needed.
     */
    private fun startUpdatingTime() {
        // Ensure only one copy of the runnable is ever scheduled by first stopping updates.
        stopUpdatingTime()
        handler.post(runnable)
    }

    /**
     * Remove the runnable that updates times within the UI.
     */
    private fun stopUpdatingTime() {
        handler.removeCallbacks(runnable)
    }


    /**
     * @param stopwatchInternal the new state of the stopwatch
     */
    private fun setStopwatch(stopwatchInternal: StopwatchInternal) {
        val before = getStopWatch(context)
        if (before !== stopwatchInternal) {
            setStopwatch(getDefaultSharedPreferences(context), stopwatchInternal)
            mStopwatchInternal = stopwatchInternal

//            // Refresh the stopwatch notification to reflect the latest stopwatch state.
//            if (!mNotificationModel.isApplicationInForeground()) {
//                updateNotification()
//            }
//
//            // Resetting the stopwatch implicitly clears the recorded laps.
//            if (stopwatch.isReset()) {
//                clearLaps()
//            }
//
//            // Notify listeners of the stopwatch change.
//            for (stopwatchListener in mStopwatchListeners) {
//                stopwatchListener.stopwatchUpdated(stopwatch)
//            }
        }
    }

    /**
     * Update all time displays based on a single snapshot of the stopwatch progress. This includes
     * the stopwatch time drawn in the circle, the current lap time and the total elapsed time in
     * the list of laps.
     */
    private fun updateTime() {
//        // Compute the total time of the stopwatch.
//        val stopwatch: Stopwatch = getStopWatch(context)
//        val totalTime: Long = stopwatch.totalTime
//        mStopwatchTextController.setTimeString(totalTime)
        // TODO update smth
        val tt = getCurrentTime()
        listenerList.forEach { it.tick(tt) }
        _tickFlow.tryEmit(tt.toDuration(DurationUnit.MILLISECONDS))
    }

    private fun getCurrentTime(): Long {
        val stopwatchInternal: StopwatchInternal = getStopWatch(context)
        val totalTime: Long = stopwatchInternal.totalTime
        return totalTime
    }

//    fun getStopwatch(): Stopwatch {
//        val s = Stopwatch(Stopwatch.State.RESET, Stopwatch.UNUSED, Stopwatch.UNUSED, 0)
//
//        // If the stopwatch reports an illegal (negative) amount of time, remove the bad data.
//
//        return s
//    }

    /**
     * Key to a preference that stores the state of the stopwatch.
     */
    private val STATE: String = "sw_state"

    /**
     * Key to a preference that stores the last start time of the stopwatch.
     */
    private val LAST_START_TIME: String = "sw_start_time"

    /**
     * Key to a preference that stores the epoch time when the stopwatch last started.
     */
    private val LAST_WALL_CLOCK_TIME: String = "sw_wall_clock_time"

    /**
     * Key to a preference that stores the accumulated elapsed time of the stopwatch.
     */
    private val ACCUMULATED_TIME: String = "sw_accum_time"

    /**
     * Prefix for a key to a preference that stores the number of recorded laps.
     */
    private val LAP_COUNT: String = "sw_lap_num"

    /**
     * Prefix for a key to a preference that stores accumulated time at the end of a lap.
     */
    private val LAP_ACCUMULATED_TIME: String = "sw_lap_time_"

    private var mStopwatchInternal: StopwatchInternal? = null

    private fun getStopWatch(context: Context): StopwatchInternal {
        if (mStopwatchInternal == null) {
            mStopwatchInternal = getStopwatch(getDefaultSharedPreferences(context))
        }

        return mStopwatchInternal!!
    }

    private fun getStopwatch(prefs: SharedPreferences): StopwatchInternal {
        val stateIndex = prefs.getInt(STATE, StopwatchInternal.State.RESET.ordinal)
        val state = StopwatchInternal.State.entries[stateIndex]
        val lastStartTime =
            prefs.getLong(LAST_START_TIME, StopwatchInternal.UNUSED)
        val lastWallClockTime = prefs.getLong(
            LAST_WALL_CLOCK_TIME,
            StopwatchInternal.UNUSED
        )
        val accumulatedTime =
            prefs.getLong(ACCUMULATED_TIME, 0)
        var s = StopwatchInternal(state, lastStartTime, lastWallClockTime, accumulatedTime)

        // If the stopwatch reports an illegal (negative) amount of time, remove the bad data.
        if (s.totalTime < 0) {
            s = s.reset()
            setStopwatch(prefs, s)
        }
        return s
    }

    /**
     * @param stopwatchInternal the last state of the stopwatch
     */
    private fun setStopwatch(prefs: SharedPreferences, stopwatchInternal: StopwatchInternal) {
        val editor = prefs.edit()

        if (stopwatchInternal.isReset) {
            editor.remove(STATE)
                .remove(LAST_START_TIME)
                .remove(LAST_WALL_CLOCK_TIME)
                .remove(ACCUMULATED_TIME)
        } else {
            editor.putInt(
                STATE,
                stopwatchInternal.state.ordinal
            )
                .putLong(
                    LAST_START_TIME,
                    stopwatchInternal.lastStartTime
                )
                .putLong(
                    LAST_WALL_CLOCK_TIME,
                    stopwatchInternal.lastWallClockTime
                )
                .putLong(
                    ACCUMULATED_TIME,
                    stopwatchInternal.accumulatedTime
                )
        }

        editor.apply()
    }

    /**
     * Returns the default [SharedPreferences] instance from the underlying storage context.
     */
    private var prefs: SharedPreferences? = null
    private fun getDefaultSharedPreferences(context: Context): SharedPreferences {
        if(prefs != null) return prefs!!

        // All N devices have split storage areas. Migrate the existing preferences into the new
        // device encrypted storage area if that has not yet occurred.
        val storageContext: Context = context.createDeviceProtectedStorageContext()
//        val name = context.packageName + "_preferences"
//        val prefsFilename = storageContext.dataDir.toString() + "/shared_prefs/" + name + ".xml"
//        val prefs = File(Objects.requireNonNull(Uri.parse(prefsFilename).path))


        return PreferenceManager.getDefaultSharedPreferences(storageContext).also {
            this.prefs = it
        }
    }

    private fun enforceMainLooper() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw IllegalAccessError("May only call from main thread.")
        }
    }

    /**
     * This runnable periodically updates times throughout the UI. It stops these updates when the
     * stopwatch is no longer running.
     */


    private inner class TimeUpdateRunnable : Runnable {
        override fun run() {
            val startTime: Long = StopwatchInternal.now()

            updateTime()

            // Blink text iff the stopwatch is paused and not pressed.
//            val touchTarget: View = if (mTime != null) mTime else mStopwatchWrapper
            val stopwatchInternal: StopwatchInternal = getStopWatch(context)
//            val blink = stopwatch.isPaused
//                    && startTime % 1000 < 500 && !touchTarget.isPressed
//
//            if (blink) {
//                mMainTimeText.setAlpha(0f)
//                mHundredthsTimeText.setAlpha(0f)
//            } else {
//                mMainTimeText.setAlpha(1f)
//                mHundredthsTimeText.setAlpha(1f)
//            }

            if (!stopwatchInternal.isReset) {
                val period: Long = (if (stopwatchInternal.isPaused)
                    REDRAW_PERIOD_PAUSED
                else
                    REDRAW_PERIOD_RUNNING).toLong()
                val endTime: Long = StopwatchInternal.now()
                val delay =
                    max(0.0, (startTime + period - endTime).toDouble()).toLong()
                handler.postDelayed(this, delay)
            }
        }
    }
    private companion object {
        /**
         * Milliseconds between redraws while running.
         */
        const val REDRAW_PERIOD_RUNNING: Int = 25

        /**
         * Milliseconds between redraws while paused.
         */
        const val REDRAW_PERIOD_PAUSED: Int = 500
    }
}
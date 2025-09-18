package com.example.shared

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.max

class Utils(private val context: Context) {

    fun interface TickListener {
        fun tick(tick: Long)
    }

    private val runnable = TimeUpdateRunnable()
    private val handler = Handler(Looper.getMainLooper())

    private val listenerList = mutableListOf<TickListener>()
    val flow = MutableStateFlow(getCurrentTime())

    fun doStart() {
        startUpdatingTime()
        enforceMainLooper()
        val sW = getStopWatch(context).start()
        setStopwatch(sW)
    }

    /**
     * Pause the stopwatch.
     */
    fun doPause() {
        enforceMainLooper()
        setStopwatch(getStopWatch(context).pause())
    }

    /**
     * Reset the stopwatch.
     */
    fun doReset() {
        stopUpdatingTime()
        val priorState: Stopwatch.State = getStopWatch(context).state

        enforceMainLooper()
        setStopwatch(getStopWatch(context).reset())
        flow.tryEmit(0L)
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
     * @param stopwatch the new state of the stopwatch
     */
    private fun setStopwatch(stopwatch: Stopwatch) {
        val before = getStopWatch(context)
        if (before !== stopwatch) {
            setStopwatch(getDefaultSharedPreferences(context), stopwatch)
            mStopwatch = stopwatch

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
        flow.tryEmit(tt)
    }

    private fun getCurrentTime(): Long {
        val stopwatch: Stopwatch = getStopWatch(context)
        val totalTime: Long = stopwatch.totalTime
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

    private var mStopwatch: Stopwatch? = null

    private fun getStopWatch(context: Context): Stopwatch {
        if (mStopwatch == null) {
            mStopwatch = getStopwatch(getDefaultSharedPreferences(context))
        }

        return mStopwatch!!
    }

    private fun getStopwatch(prefs: SharedPreferences): Stopwatch {
        val stateIndex = prefs.getInt(STATE, Stopwatch.State.RESET.ordinal)
        val state = Stopwatch.State.entries[stateIndex]
        val lastStartTime =
            prefs.getLong(LAST_START_TIME, Stopwatch.UNUSED)
        val lastWallClockTime = prefs.getLong(
            LAST_WALL_CLOCK_TIME,
            Stopwatch.UNUSED
        )
        val accumulatedTime =
            prefs.getLong(ACCUMULATED_TIME, 0)
        var s = Stopwatch(state, lastStartTime, lastWallClockTime, accumulatedTime)

        // If the stopwatch reports an illegal (negative) amount of time, remove the bad data.
        if (s.totalTime < 0) {
            s = s.reset()
            setStopwatch(prefs, s)
        }
        return s
    }

    /**
     * @param stopwatch the last state of the stopwatch
     */
    private fun setStopwatch(prefs: SharedPreferences, stopwatch: Stopwatch) {
        val editor = prefs.edit()

        if (stopwatch.isReset) {
            editor.remove(STATE)
                .remove(LAST_START_TIME)
                .remove(LAST_WALL_CLOCK_TIME)
                .remove(ACCUMULATED_TIME)
        } else {
            editor.putInt(
                STATE,
                stopwatch.state.ordinal
            )
                .putLong(
                    LAST_START_TIME,
                    stopwatch.lastStartTime
                )
                .putLong(
                    LAST_WALL_CLOCK_TIME,
                    stopwatch.lastWallClockTime
                )
                .putLong(
                    ACCUMULATED_TIME,
                    stopwatch.accumulatedTime
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
            val startTime: Long = Stopwatch.now()

            updateTime()

            // Blink text iff the stopwatch is paused and not pressed.
//            val touchTarget: View = if (mTime != null) mTime else mStopwatchWrapper
            val stopwatch: Stopwatch = getStopWatch(context)
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

            if (!stopwatch.isReset) {
                val period: Long = (if (stopwatch.isPaused)
                    REDRAW_PERIOD_PAUSED
                else
                    REDRAW_PERIOD_RUNNING).toLong()
                val endTime: Long = Stopwatch.now()
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
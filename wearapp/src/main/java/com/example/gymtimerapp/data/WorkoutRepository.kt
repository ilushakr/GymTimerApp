package com.example.gymtimerapp.data

import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.gymtimer.GymTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class WorkoutRepository(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val vibrator: Vibrator by lazy {
        (context.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    }

    val wakeLock: PowerManager by lazy {
        (context.getSystemService(POWER_SERVICE) as PowerManager)
    }

    fun handleGymTimerEvent(event: GymTimer.Event) {
        val (timings, amplitudes) = when (event) {
            GymTimer.Event.PreRestEnd -> {
                longArrayOf(300) to intArrayOf(255)
            }

            GymTimer.Event.PreWorkEnd -> {
                longArrayOf(300) to intArrayOf(255)
            }

            GymTimer.Event.RestStart -> {
                longArrayOf(200, 100, 200) to intArrayOf(255, 10, 255)
            }

            GymTimer.Event.WorkStart -> {
                longArrayOf(200, 100, 200) to intArrayOf(255, 10, 255)
            }

            GymTimer.Event.WorkoutEnd -> {
                longArrayOf(200, 100, 200, 100, 1000) to intArrayOf(255, 10, 255, 10, 255)
            }
        }

        val repeatIndex = -1 // Don't repeat.
        vibrator.vibrate(
            VibrationEffect.createWaveform(
                timings,
                amplitudes,
                repeatIndex
            )
        )

        // TODO remove deprecated flag
//        wakeLock.newWakeLock(
//            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
//            "WorkoutRepository::VibrateWakelockTag"
//        ).apply {
//            acquire(100)
//
//            Log.d("vmkfldvf", "doWork: ")
//
//            scope.launch {
//                delay(200)
//                val repeatIndex = -1 // Don't repeat.
//                vibrator.vibrate(
//                    VibrationEffect.createWaveform(
//                        timings,
//                        amplitudes,
//                        repeatIndex
//                    )
//                )
//            }
//        }
    }
}
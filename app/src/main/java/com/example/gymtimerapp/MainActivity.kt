package com.example.gymtimerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.gymtimer.GymTimer
import com.example.gymtimer.TestApp
import com.example.stopwatch.StopWatch
import com.example.stopwatch.TimerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

@SuppressLint("VisibleForTests")
class MainActivity : ComponentActivity() {

    @Composable
    fun AnimatedProgressBar(progress: Float) {

        var prevValue by remember { mutableFloatStateOf(progress) }

        var targetValue by remember { mutableStateOf<Float?>(null) }

        LaunchedEffect(progress) {
            if(abs(prevValue - progress) > 2) {
                targetValue = progress
            }

            prevValue = progress
        }

        val m by animateFloatAsState(
            targetValue = targetValue ?: progress,
//            targetValue = when {
//                progress == 0f -> 0f
//                progress == 100f -> 100f
////                (abs(prevValue - progress) > 10) && targetValue == null-> {
////                    targetValue = progress
////                    targetValue ?: 0f
////                }
//                else -> progress
//            },
            animationSpec = tween(
                durationMillis = if(targetValue != null) 300 else 0 // Продолжительность анимации
            )
        ){
//            prevValue = progress
            targetValue = null
        }




       Canvas(Modifier.size(300.dp)) {
//           val actualValue = if (isAnimating) animatedFloatValue else progress
           val sweepAngle = (m / 100) * (360 - 50)

           val size = size

           val circleThickness = 20.dp.toPx()

           val timeWidgetGapAngle = 51f

           drawArc(
               color = Color.Red,
               startAngle = -90 + timeWidgetGapAngle / 2, // Start from top (12 o'clock position)
               sweepAngle = 360f - timeWidgetGapAngle, // Sweep based on progress
               useCenter = false,
               style = Stroke(
                   width = circleThickness,
                   cap = StrokeCap.Round
               ),
               size = Size(
                   width = size.width - circleThickness,
                   height = size.height - circleThickness
               ),
               topLeft = Offset(
                   circleThickness / 2,
                   circleThickness / 2
               )
           )

           drawArc(
               color = Color.Yellow,
               startAngle = -90f + timeWidgetGapAngle / 2, // Start from top (12 o'clock position)
               sweepAngle = sweepAngle, // Sweep based on progress
               useCenter = false,
               style = Stroke(
                   width = circleThickness,
                   cap = StrokeCap.Round
               ),
               size = Size(
                   width = size.width - circleThickness,
                   height = size.height - circleThickness
               ),
               topLeft = Offset(
                   circleThickness / 2,
                   circleThickness / 2
               )
           )
       }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val s = StopWatch(this)
                OriginalGreetingContent(
                    TestApp(s),
                    s
                )
            }
        }
    }

    enum class ProgressEvent {
        SetToMax, SetToMin
    }

    @Composable
    private fun Modifier.drawProgress(
        progress: Float, // от 0 до 100
        color: Color = Color.Blue,
        onResetToStartEvent: Flow<ProgressEvent>
    ): Modifier {


        var isAnimating by remember { mutableStateOf(true) }

        var targetFloatValue by remember { mutableFloatStateOf(0f) }
        val animatedFloatValue by animateFloatAsState(
            targetValue = targetFloatValue,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            finishedListener = {
                isAnimating = false
                targetFloatValue = progress
            }
        )

        LaunchedEffect(true) {
            onResetToStartEvent.collectLatest {
                isAnimating = true
                targetFloatValue = when(it){
                    ProgressEvent.SetToMax -> 100f
                    ProgressEvent.SetToMin -> 0f
                }
            }
        }

        return this.drawBehind {
            val actualValue = if (isAnimating) animatedFloatValue else progress
            val sweepAngle = (actualValue / 100) * (360 - 50)

            val size = size

            val circleThickness = 20.dp.toPx()

            val timeWidgetGapAngle = 51f

            drawArc(
                color = Color.Red,
                startAngle = -90 + timeWidgetGapAngle / 2, // Start from top (12 o'clock position)
                sweepAngle = 360f - timeWidgetGapAngle, // Sweep based on progress
                useCenter = false,
                style = Stroke(
                    width = circleThickness,
                    cap = StrokeCap.Round
                ),
                size = Size(
                    width = size.width - circleThickness,
                    height = size.height - circleThickness
                ),
                topLeft = Offset(
                    circleThickness / 2,
                    circleThickness / 2
                )
            )

            drawArc(
                color = color,
                startAngle = -90f + timeWidgetGapAngle / 2, // Start from top (12 o'clock position)
                sweepAngle = sweepAngle, // Sweep based on progress
                useCenter = false,
                style = Stroke(
                    width = circleThickness,
                    cap = StrokeCap.Round
                ),
                size = Size(
                    width = size.width - circleThickness,
                    height = size.height - circleThickness
                ),
                topLeft = Offset(
                    circleThickness / 2,
                    circleThickness / 2
                )
            )
        }
    }

    @Composable
    fun OriginalGreetingContent(
        testApp: TestApp,
        stopWatch: StopWatch,
        modifier: Modifier = Modifier
    ) {
        var stopWatchState by remember { mutableStateOf("StopWatch: initial") } // Local state for stopwatch

        val t = remember {
            GymTimer(stopWatch).apply {
                setModel(
                    GymTimer.WorkoutModel(
                        listOf(
                            GymTimer.ExerciseModel(
                                name = "first",
                                numberOfSets = 2,
                                workDuration = 4.seconds,
                                restDuration = 2.seconds,
                                finishWorkRemainingDuration = 10.seconds,
                                finishRestRemainingDuration = 2.seconds,
                            ),
//                            GymTimer.ExerciseModel(
//                                name = "second",
//                                numberOfSets = 2,
//                                workDuration = 8.seconds,
//                                restDuration = 5.seconds
//                            )
                        )
                    )
                )
            }
        }

        val channel by t.currentExerciseModelStateFlow.collectAsState()

//        CircularProgress(
//            progress  = 70f, // пример значения
//            modifier = Modifier.fillMaxSize(),
//            color = Color.Blue,
//            backgroundColor = Color.Yellow.copy(alpha = 0.3f),
//            strokeWidth = 12.dp
//        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stopWatchState,
                modifier = modifier.padding(bottom = 8.dp)
            )

            Button(onClick = { stopWatch.start() }, modifier = Modifier.fillMaxWidth()) {
                Text("doStart")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { stopWatch.pause() }, modifier = Modifier.fillMaxWidth()) {
                Text("doPause")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { stopWatch.stopAndReset() }, modifier = Modifier.fillMaxWidth()) {
                Text("doReset")
            }

            Spacer(modifier = Modifier.height(4.dp))

            val state by testApp.state.collectAsState()
            Text(state.toString())

            Spacer(modifier = Modifier.height(4.dp))


            Text(channel.toString(), Modifier.height(100.dp))


            val progress = if(channel is GymTimer.CurrentExerciseState.CurrentExercise){
                (channel as GymTimer.CurrentExerciseState.CurrentExercise).remainingDuration.inWholeMilliseconds.toDouble() / (channel as GymTimer.CurrentExerciseState.CurrentExercise).fullDuration.inWholeMilliseconds.toDouble()
            }else {
                0
            }

            AnimatedProgressBar(
                progress.toFloat() * 100
            )
            Box(
                Modifier
                    .size(400.dp)
                    .drawProgress(
                        progress = progress.toFloat() * 100,
                        onResetToStartEvent = merge(
                            t.eventFlow.map {
                                when (it) {
                                    GymTimer.Event.PreRestEnd -> null
                                    GymTimer.Event.PreWorkEnd -> null
                                    GymTimer.Event.RestStart -> ProgressEvent.SetToMax
                                    GymTimer.Event.WorkStart -> ProgressEvent.SetToMax
                                    GymTimer.Event.WorkoutEnd -> ProgressEvent.SetToMin
                                }
                            }.filterNotNull(),
                            stopWatch.timerState.map {
                                when (it) {
                                    TimerState.Prepared -> ProgressEvent.SetToMin
                                    TimerState.Running -> null
                                    TimerState.Paused -> null
                                }
                            }.filterNotNull()
                        )
                    )
                    .background(Color.Yellow.copy(alpha = 0.3f))
            )

        }

        val context = LocalContext.current

        val haptic = LocalHapticFeedback.current

        LaunchedEffect(true) {
            t.eventFlow.collect {
                when (it) {
                    GymTimer.Event.PreRestEnd -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    GymTimer.Event.PreWorkEnd -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    GymTimer.Event.RestStart -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    GymTimer.Event.WorkStart -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    GymTimer.Event.WorkoutEnd -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    else -> Unit
                }
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                stopWatch
            }
        }

        // LaunchedEffect to collect from stopWatch.tickFlow
        // This should ideally be handled within a ViewModel or a more robust state management solution
        // for complex scenarios, but for this example, it remains similar to your original.
        LaunchedEffect(key1 = stopWatch) { // Keyed to stopWatch instance
            stopWatch.tickFlow.collect { tick ->
                stopWatchState = "StopWatch: $tick"
            }
        }

//

    }

    @Composable
    fun CircularProgress(
        progress: Float, // от 0 до 100
        modifier: Modifier = Modifier,
        color: Color = Color.Blue,
        backgroundColor: Color = Color.LightGray,
        strokeWidth: Dp = 8.dp
    ) {
        val sweepAngle = (progress / 100) * 360
        Canvas(modifier = modifier.clipToBounds()) {
            drawRect(color = backgroundColor)

            // Calculate padding to make the arc smaller than the container
            val padding = size.minDimension * -2f // 10% padding

            // Calculate the size of the arc (smaller than the container)
            val arcSize = Size(
                width = size.width - (padding * 2),
                height = size.height - (padding * 2)
            )

            // Calculate the offset to center the arc
            val arcOffset = Offset(padding, padding)

            // Draw progress arc (red) - smaller than the container
            drawArc(
                color = color,
                startAngle = -90f, // Start from top (12 o'clock position)
                sweepAngle = sweepAngle, // Sweep based on progress
                useCenter = true,
                topLeft = arcOffset,
                size = arcSize,
                style = Fill
            )
        }
    }

}

@Preview
@Composable
fun Preview(modifier: Modifier = Modifier) {
    Icon(
        Icons.Default.Phone,
        contentDescription = null,
        modifier = Modifier.size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Red)
            .padding(8.dp),
    )
}
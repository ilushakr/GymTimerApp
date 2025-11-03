package com.example.gymtimerapp.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material3.AnimatedPage
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.HorizontalPagerScaffold
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.PagerScaffoldDefaults
import androidx.wear.compose.material3.ProgressIndicatorColors
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.gymtimerapp.presentation.theme.GymTimerAppTheme
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun MainScreen(
    onFinishWidgetClick: () -> Unit,
) {
    WorkoutScreen(onFinishWidgetClick = onFinishWidgetClick)
}


@Composable
private fun WorkoutScreen(
    workoutViewModel: WorkoutViewModel = koinViewModel(),
    onFinishWidgetClick: () -> Unit,
) {

    var overlayVisibility by remember { mutableStateOf(false) }

    Content(
        state = workoutViewModel.state.collectAsState().value,
        startWorkout = { workoutViewModel.startWorkout() },
        pauseWorkout = { workoutViewModel.playPauseTimer() },
        onFinishWidgetClick = onFinishWidgetClick,
        remainingJobTimeMainWidget = {
            Text(
                text = workoutViewModel
                    .detailedTimeIfoFlow
                    .collectAsState()
                    .value
                    .remainingDuration
                    .printToSeconds(),
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { workoutViewModel.playPauseTimer() },
                            onLongPress = { onFinishWidgetClick.invoke() }
                        )
                    }
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.numeralSmall.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        },
        remainingJobTimeWidget = {
            Text(
                text = workoutViewModel
                    .detailedTimeIfoFlow
                    .collectAsState()
                    .value
                    .remainingDuration
                    .printToSeconds(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
            )
        },
        wholeTimeWidget = {
            Text(
                text = workoutViewModel
                    .detailedTimeIfoFlow
                    .collectAsState()
                    .value
                    .wholeTime
                    .printToSeconds(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
            )
        },
        exerciseListWidget = {
            workoutViewModel
                .exerciseListState
                .collectAsState(null)
                .value
                ?.takeIf { it.currentExerciseIndex > -1 }
                ?.let {
                    ExerciseListWidget(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { workoutViewModel.playPauseTimer() },
                                    onLongPress = { onFinishWidgetClick.invoke() }
                                )
                            },
                        state = it
                    )
                }
        }
    )

    ProgressWidget(
        modifier = Modifier.fillMaxSize(),
        progressStateFlow = workoutViewModel.progressState,
        onAnimateProgressEventFlow = workoutViewModel.resetProgressWidgetEventFlow,
    )

    OverlayWidget(
        overlayVisibility = overlayVisibility,
        onContinueClick = {
            workoutViewModel.playPauseTimer()
            overlayVisibility = false
        },
        onResetClick = {
            workoutViewModel.reset()
            overlayVisibility = false
        }
    )

    LaunchedEffect(workoutViewModel) {
        workoutViewModel.eventFlow.collectLatest {
            when (it) {
                Event.HideOverlay -> {
                    overlayVisibility = false
                }

                Event.ShowOverlay -> {
                    overlayVisibility = true
                }
            }

        }
    }
}

@Composable
private fun ExerciseListWidget(
    modifier: Modifier = Modifier,
    state: ExerciseListUiModel
) {
    ScalingLazyColumn(
        autoCentering = AutoCenteringParams(itemIndex = state.currentExerciseIndex),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.Bottom)
    ) {
        itemsIndexed(state.exerciseNameList) { index, item ->
            Text(
                modifier = when {
                    index == state.currentExerciseIndex -> Modifier
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .padding(10.dp)

                    else -> Modifier
                },
                text = item,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                )
            )
        }
    }
}

@Composable
private fun ProgressWidget(
    modifier: Modifier = Modifier,
    progressStateFlow: Flow<ProgressState>,
    onAnimateProgressEventFlow: Flow<ProgressEvent>,
) {
    progressStateFlow.collectAsState(ProgressState(null)).value.progress?.let { uiProgress ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .drawProgress(
                    progress = uiProgress,
                    onAnimateProgressEventFlow = onAnimateProgressEventFlow,
                )
        )
    }
}

private enum class Pages {
    ExerciseList, MainInfo, DetailInfo
}

@Composable
private fun Content(
    state: State,
    startWorkout: () -> Unit,
    pauseWorkout: () -> Unit,
    onFinishWidgetClick: () -> Unit,
    remainingJobTimeMainWidget: @Composable () -> Unit,
    remainingJobTimeWidget: @Composable () -> Unit,
    wholeTimeWidget: @Composable () -> Unit,
    exerciseListWidget: @Composable () -> Unit,
) {
    when (state) {
        is State.WorkoutUiModel -> {
            val pagerState = rememberPagerState(
                pageCount = { Pages.entries.size },
                initialPage = Pages.entries.indexOf(Pages.MainInfo)
            )

            HorizontalPagerScaffold(
                modifier = Modifier.padding(bottom = 12.dp),
                pagerState = pagerState
            ) {
                HorizontalPager(
                    state = pagerState,
                    flingBehavior = PagerScaffoldDefaults.snapWithSpringFlingBehavior(state = pagerState),
                    rotaryScrollableBehavior = null,
                ) { page ->
                    AnimatedPage(pageIndex = page, pagerState = pagerState) {
                        ScreenScaffold(Modifier.padding(12.dp)) {
                            when (Pages.entries[page]) {
                                Pages.MainInfo -> remainingJobTimeMainWidget()
                                Pages.DetailInfo -> DetailInfoPageWidget(
                                    modifier = Modifier
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = { pauseWorkout.invoke() },
                                                onLongPress = { onFinishWidgetClick.invoke() }
                                            )
                                        },
                                    state = state,
                                    wholeTimeWidget = wholeTimeWidget,
                                    remainingJobTimeWidget = remainingJobTimeWidget,
                                )

                                Pages.ExerciseList -> exerciseListWidget()
                            }
                        }
                    }
                }
            }

        }

        is State.Ready -> ReadyStateWidget(startWorkout = startWorkout)

        is State.Finished -> FinishedStateWidget(
            state = state,
            onFinishWidgetClick = onFinishWidgetClick
        )
    }
}

@NonRestartableComposable
@Composable
private fun ReadyStateWidget(
    modifier: Modifier = Modifier,
    startWorkout: () -> Unit,
) {
    Box(modifier.fillMaxSize()) {
        FilledIconButton(
            modifier = Modifier.align(Alignment.Center),
            onClick = startWorkout
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "triggers phone action",
            )
        }
    }
}

@Composable
private fun FinishedStateWidget(
    modifier: Modifier = Modifier,
    state: State.Finished,
    onFinishWidgetClick: () -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onLongPress = { onFinishWidgetClick.invoke() }) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Workout time:",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        )
        Text(
            text = state.wholeTime.printToSeconds(),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 22.sp
            )
        )
    }
}

private fun ScalingLazyListScope.Item(
    titleText: String,
    valueText: String
) {
    item {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = titleText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
            )
        }
    }
}

private fun ScalingLazyListScope.Item(
    titleText: String,
    timeWidget: @Composable () -> Unit,
) {
    item {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = titleText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            )
            timeWidget()
        }
    }
}

@Composable
private fun DetailInfoPageWidget(
    modifier: Modifier = Modifier,
    state: State.WorkoutUiModel,
    remainingJobTimeWidget: @Composable () -> Unit,
    wholeTimeWidget: @Composable () -> Unit,
) {

    ScalingLazyColumn(
        autoCentering = AutoCenteringParams(itemIndex = 1),
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.Bottom)
    ) {
        item {
            ListHeader {
                Text(
                    text = "Detailed info:",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Item(
            titleText = "Job name:",
            valueText = state.name
        )

        Item(
            titleText = "Set and type:",
            valueText = "${state.type} of ${state.indexOfJob + 1} set of ${state.numberOfSets}"
        )

        Item(
            titleText = "Workout time",
            timeWidget = wholeTimeWidget
        )

        Item(
            titleText = "Remaining time",
            timeWidget = remainingJobTimeWidget
        )

    }
}

@Composable
private fun OverlayWidget(
    modifier: Modifier = Modifier,
    overlayVisibility: Boolean,
    onContinueClick: () -> Unit,
    onResetClick: () -> Unit,
) {

    AnimatedVisibility(
        visible = overlayVisibility,
        enter = fadeIn(), // Optional: Customize enter animation (e.g., slideIn, expandIn)
        exit = fadeOut() // Optional: Customize exit animation (e.g., slideOut, shrinkOut)
    ) {
        Box(
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.80f))
                .clickable(false) {}
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                FilledIconButton(onClick = onResetClick) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "triggers phone action",
                    )
                }

                FilledIconButton(onClick = onContinueClick) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "triggers phone action",
                    )
                }
            }

            ResponsiveTimeText(
                timeTextStyle = TimeTextDefaults.timeTextStyle(
                    color = MaterialTheme.colorScheme.primary
                ),
            )
        }
    }
}


private fun Duration.printToSeconds() = this.inWholeSeconds.seconds.toString()

@Composable
private fun Modifier.drawProgress(
    progress: Float, // от 0 до 100
    colors: ProgressIndicatorColors = ProgressIndicatorDefaults.colors(),
    indicatorWidth: Dp = 12.dp,
    timeWidgetGapAngle: Float = 51f,
    onAnimateProgressEventFlow: Flow<ProgressEvent>
): Modifier {

    var isAnimating by remember { mutableStateOf(false) }

    var targetFloatValue by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(true) {
        onAnimateProgressEventFlow.collectLatest {
            isAnimating = true
            targetFloatValue = when (it) {
                ProgressEvent.SetToMax -> 100f
                ProgressEvent.SetToMin -> 0f
            }
        }
    }

    val animatedFloatValue by animateFloatAsState(
        targetValue = targetFloatValue,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        finishedListener = {
            isAnimating = false
            targetFloatValue = progress
        }
    )

    return this.drawBehind {
        val actualValue = if (isAnimating) animatedFloatValue else progress
        val sweepAngle = (actualValue / 100) * (360 - 50)

        val size = size

        val circleThickness = indicatorWidth.toPx()

        drawArc(
            brush = colors.trackBrush,
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
            brush = colors.indicatorBrush,
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


@Preview(device = WearDevices.SMALL_ROUND)
@Composable
private fun Preview() {
    GymTimerAppTheme {
        val color = MaterialTheme.colorScheme.primary
        AppScaffold(
            timeText = {
                ResponsiveTimeText(
                    timeTextStyle = TimeTextDefaults.timeTextStyle(
                        color = color
                    )
                )
            }
        ) {
            FinishedStateWidget(
                state = State.Finished(4432434.milliseconds)
            ) {}
        }
    }
}





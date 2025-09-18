package com.example.wearapp.presentation

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtimer.GymTimer
import com.example.stopwatch.TimerState
import com.example.wearapp.data.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
internal class WorkoutViewModel(
    private val gymTimer: GymTimer,
    private val repository: WorkoutRepository,
) : ViewModel() {

    val eventFlow = gymTimer.timerState
        .map {
            when(it){
                TimerState.Prepared -> null
                TimerState.Running -> Event.HideOverlay
                TimerState.Paused -> Event.ShowOverlay
            }
        }
        .filterNotNull()

    val state = gymTimer.currentExerciseModelStateFlow
        .map { currentExerciseState ->
            when (currentExerciseState) {
                is GymTimer.CurrentExerciseState.CurrentExercise -> State.WorkoutUiModel(
                    name = currentExerciseState.name,
                    indexOfJob = currentExerciseState.indexOfJob,
                    type = currentExerciseState.type.toUiModel(),
                )
                is GymTimer.CurrentExerciseState.Finished -> State.Finished(currentExerciseState.wholeTime)
                is GymTimer.CurrentExerciseState.Preparing -> State.Ready
            }
        }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = State.Ready
        )

    val detailedTimeIfoFlow = gymTimer.currentExerciseModelStateFlow
        .sample(100.milliseconds)
        .map {
            (it as? GymTimer.CurrentExerciseState.CurrentExercise)?.let { currentExercise ->
                DetailedTimeInfo(
                    currentExercise.remainingDuration,
                    currentExercise.wholeTime
                )
            }
        }
        .filterNotNull()
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = DetailedTimeInfo(Duration.ZERO, Duration.ZERO)
        )


    val progressState = gymTimer.currentExerciseModelStateFlow
        .sample(10.milliseconds)
        .map { ProgressState(progress = (it as? GymTimer.CurrentExerciseState.CurrentExercise)?.getCurrentProgress()) }
        .flowOn(Dispatchers.Default)

    val resetProgressWidgetEventFlow = merge(
        gymTimer.eventFlow.map {
            when (it) {
                GymTimer.Event.PreRestEnd -> null
                GymTimer.Event.PreWorkEnd -> null
                GymTimer.Event.RestStart -> ProgressEvent.SetToMax
                GymTimer.Event.WorkStart -> ProgressEvent.SetToMax
                GymTimer.Event.WorkoutEnd -> ProgressEvent.SetToMin
            }
        }.filterNotNull(),
        gymTimer.timerState.map {
            when (it) {
                TimerState.Prepared -> ProgressEvent.SetToMin
                TimerState.Running -> null
                TimerState.Paused -> null
            }
        }.filterNotNull()
    )

    init {
        gymTimer
            .eventFlow
            .onEach(repository::handleGymTimerEvent)
            .onEach(::handleGymEvent)
            .launchIn(viewModelScope)
    }

    fun startWorkout() {
        gymTimer.setModel(
            GymTimer.WorkoutModel(
                listOf(
                    GymTimer.ExerciseModel(
                        name = "first",
                        numberOfSets = 10,
                        workDuration = 5.seconds,
                        restDuration = 3.seconds,
                        finishWorkRemainingDuration = 5.seconds,
                        finishRestRemainingDuration = 5.seconds,
                    ),
//                    GymTimer.ExerciseModel(
//                        name = "second",
//                        numberOfSets = 2,
//                        workDuration = 8.seconds,
//                        restDuration = 5.seconds
//                    )
                )
            )
        )
        gymTimer.start()
    }

    fun playPauseTimer() {
        when (gymTimer.timerState.value) {
            TimerState.Prepared -> Unit
            TimerState.Running -> gymTimer.pause()
            TimerState.Paused -> gymTimer.start()
        }
    }

    fun reset() {
        gymTimer.stopAndReset()
    }

    private fun handleGymEvent(event: GymTimer.Event) {
//        when (event) {
//            GymTimer.Event.PreRestEnd -> TODO()
//            GymTimer.Event.PreWorkEnd -> TODO()
//            GymTimer.Event.RestStart -> TODO()
//            GymTimer.Event.WorkStart -> TODO()
//            GymTimer.Event.WorkoutEnd -> TODO()
//        }
    }

    private fun GymTimer.CurrentExerciseState.CurrentExercise.getCurrentProgress() =
        (remainingDuration.inWholeMilliseconds.toDouble() / fullDuration.inWholeMilliseconds.toDouble()).toFloat() * 100

    private fun GymTimer.CurrentExerciseState.CurrentExercise.Type.toUiModel() = when (this) {
        GymTimer.CurrentExerciseState.CurrentExercise.Type.Work -> State.WorkoutUiModel.TypeUiModel.Work
        GymTimer.CurrentExerciseState.CurrentExercise.Type.Rest -> State.WorkoutUiModel.TypeUiModel.Rest
    }
}

@JvmInline
internal value class ProgressState(val progress: Float?)

internal sealed interface Event {
    data object ShowOverlay : Event
    data object HideOverlay : Event
}

@Stable
internal sealed interface State {
    data class WorkoutUiModel(
        val name: String,
        val indexOfJob: Int,
        val type: TypeUiModel,
    ) : State {
        enum class TypeUiModel {
            Work, Rest
        }
    }

    @JvmInline
    value class Finished(val wholeTime: Duration) : State

    data object Ready : State
}


@Stable
internal data class DetailedTimeInfo(
    val remainingDuration: Duration,
    val wholeTime: Duration
)

internal enum class ProgressEvent {
    SetToMax, SetToMin
}
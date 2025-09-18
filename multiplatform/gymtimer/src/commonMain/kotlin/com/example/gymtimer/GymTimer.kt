package com.example.gymtimer

import com.example.stopwatch.StopWatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class GymTimer(
    private val stopWatch: StopWatch,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val timeToJobMap = mutableMapOf<Long, JobInternal>()

    private var currentJobInternal: JobInternal? = null
    private var endTime = Duration.ZERO

    val timerState = stopWatch.timerState

    private val _currentExerciseModelStateFlow =
        MutableStateFlow<CurrentExerciseState>(CurrentExerciseState.Preparing(stopWatch.tickFlow.value))
    val currentExerciseModelStateFlow = _currentExerciseModelStateFlow.asStateFlow()

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        stopWatch
            .tickFlow
            .map(::handleStopWatchTick)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach(::handleSecondsTick)
            .launchIn(scope)
    }

    fun start() {
        stopWatch.start()
    }

    fun pause() {
        stopWatch.pause()
    }

    fun stopAndReset() {
        stopWatch.stopAndReset()
    }

    fun setModel(workoutModel: WorkoutModel) {
        var time = Duration.ZERO
        workoutModel.exerciseList.forEachIndexed { index, exercise ->
            repeat(exercise.numberOfSets) { indexOfSet ->
                val work = JobInternal(
                    name = exercise.name,
                    startTime = time,
                    duration = exercise.workDuration,
                    finishJobRemainingDuration = exercise.finishWorkRemainingDuration.takeIf { it != Duration.ZERO && it < exercise.workDuration },
                    indexOfJob = indexOfSet,
                    type = TypeInternal.Work,
                )

                timeToJobMap[time.inWholeSeconds] = work

                time = time + exercise.workDuration

                val rest = JobInternal(
                    name = exercise.name,
                    startTime = time,
                    duration = exercise.restDuration,
                    finishJobRemainingDuration = exercise.finishRestRemainingDuration.takeIf { it != Duration.ZERO && it < exercise.restDuration },
                    indexOfJob = indexOfSet,
                    type = TypeInternal.Rest
                )

                timeToJobMap[time.inWholeSeconds] = rest

                time = time + exercise.restDuration
            }
        }

        endTime = time
    }

    private suspend fun handleStopWatchTick(duration: Duration): Long? {
        return when  {
            duration== Duration.ZERO -> {
                _currentExerciseModelStateFlow.value = CurrentExerciseState.Preparing(duration)
                null
            }

            duration > endTime + 1.seconds -> {
                _currentExerciseModelStateFlow.value = CurrentExerciseState.Finished(duration)
                null
            }
            else -> {
                val currentElement = currentJobInternal ?: return duration.inWholeSeconds

                val remainingSeconds =
                    currentElement.duration + currentElement.startTime - duration
                _currentExerciseModelStateFlow.value = CurrentExerciseState.CurrentExercise(
                    name = currentElement.name,
                    remainingDuration = remainingSeconds,
                    indexOfJob = currentElement.indexOfJob,
                    type = currentElement.type.currentExerciseType,
                    fullDuration = currentElement.duration,
                    wholeTime = duration,
                )

                duration.inWholeSeconds
            }
        }
    }

    private suspend fun handleSecondsTick(seconds: Long) {
        if (endTime.inWholeSeconds == seconds) {
            currentJobInternal = null
            _eventFlow.emit(Event.WorkoutEnd)
            return
        }

        timeToJobMap[seconds]?.let {
            currentJobInternal = it
            val event = when (it.type) {
                TypeInternal.Work -> Event.WorkStart
                TypeInternal.Rest -> Event.RestStart
            }
            _eventFlow.emit(event)
        }

        val currentElement = currentJobInternal ?: return

        currentElement.finishJobRemainingDuration?.inWholeSeconds?.let { finishJobRemainingDuration ->
            if (finishJobRemainingDuration + seconds == currentElement.duration.inWholeSeconds + currentElement.startTime.inWholeSeconds) {
                val event = when (currentElement.type) {
                    TypeInternal.Work -> Event.PreWorkEnd
                    TypeInternal.Rest -> Event.PreRestEnd
                }
                _eventFlow.emit(event)
            }
        }

//        val remainingSeconds =
//            currentElement.duration.inWholeSeconds + currentElement.startTime.inWholeSeconds - seconds
//        _cusserExerciseModelStateFlow.value = CurrentExerciseState.CurrentExercise(
//            name = currentElement.name,
//            remainingSeconds = remainingSeconds,
//            indexOfJob = currentElement.indexOfJob,
//            type = currentElement.type.currentExerciseType,
//            fullDuration = currentElement.duration.inWholeSeconds
//        )
    }

    // External model for set up
    data class WorkoutModel(
        val exerciseList: List<ExerciseModel>
    )

    data class ExerciseModel(
        val name: String,
        val numberOfSets: Int,
        val workDuration: Duration,
        val restDuration: Duration,
        val finishWorkRemainingDuration: Duration = Duration.ZERO,
        val finishRestRemainingDuration: Duration = Duration.ZERO,
    )

    // External model for show
    sealed interface CurrentExerciseState {
        val wholeTime: Duration

        @JvmInline
        value class Preparing(override val wholeTime: Duration) : CurrentExerciseState

        @JvmInline
        value class Finished(override val wholeTime: Duration) : CurrentExerciseState

        data class CurrentExercise(
            val name: String,
            val indexOfJob: Int,
            val type: Type,
            val remainingDuration: Duration,
            val fullDuration: Duration,
            override val wholeTime: Duration
        ) : CurrentExerciseState {
            enum class Type {
                Work, Rest
            }
        }
    }

    sealed interface Event {
        data object WorkStart : Event
        data object RestStart : Event
        data object PreWorkEnd : Event
        data object PreRestEnd : Event
        data object WorkoutEnd : Event
    }

    // Private models for business logic
    private data class JobInternal(
        val startTime: Duration,
        val duration: Duration,
        val finishJobRemainingDuration: Duration?,
        val name: String,
        val indexOfJob: Int,
        val type: TypeInternal
    )

    private enum class TypeInternal {
        Work, Rest;

        val currentExerciseType
            get() = when (this) {
                Work -> CurrentExerciseState.CurrentExercise.Type.Work
                Rest -> CurrentExerciseState.CurrentExercise.Type.Rest
            }
    }
}
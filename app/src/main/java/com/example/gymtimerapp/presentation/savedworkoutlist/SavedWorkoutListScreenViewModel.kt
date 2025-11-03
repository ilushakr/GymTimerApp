package com.example.gymtimerapp.presentation.savedworkoutlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtimerapp.domain.ConnectionManager
import com.example.gymtimerapp.navigation.NavigationManager
import com.example.gymtimerapp.presentation.newworkout.NewWorkoutViewModel.ExerciseUiModel
import com.example.gymtimerapp.presentation.utils.TopBarState
import com.example.presistent.api.PersistentWorkoutManager
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration

class SavedWorkoutListScreenViewModel(
    private val shouldSaveWorkoutToPersistent: Boolean,
    private val navigationManager: NavigationManager,
    private val persistentWorkoutManager: PersistentWorkoutManager,
    private val connectionManager: ConnectionManager,
) : ViewModel() {

    private var firstEmission = true

    private val _currentQuery = MutableStateFlow("")
    val currentQuery = _currentQuery.value

    private val topBarStateFlow = MutableStateFlow(TopBarState.Title)


    @OptIn(FlowPreview::class)
    val viewState = combine(
        persistentWorkoutManager.workoutsFlow().map { workoutPersistentModelList ->
            workoutPersistentModelList.map { workoutPersistentModel ->
                WorkoutUiModel(
                    name = workoutPersistentModel.name,
                    exerciseList = workoutPersistentModel.exerciseList.map { exercisePersistentModel ->
                        ExerciseUiModel(
                            name = exercisePersistentModel.name,
                            isSelected = true,
                            numberOfSets = exercisePersistentModel.numberOfSets,
                            workDuration = exercisePersistentModel.workDuration.toUi(),
                            restDuration = exercisePersistentModel.restDuration.toUi(),
                            finishWorkRemainingDuration = exercisePersistentModel.finishWorkRemainingDuration.toUi(),
                            finishRestRemainingDuration = exercisePersistentModel.finishRestRemainingDuration.toUi(),
                            uuid = exercisePersistentModel.uuid
                        )
                    },
                    uuid = workoutPersistentModel.uuid
                )
            }
        }
            .debounce {
                when (firstEmission) {
                    true -> {
                        firstEmission = false
                        300
                    }

                    false -> 0
                }
            },
        _currentQuery.debounce(300L),
        topBarStateFlow
    ) { list, query, topBarState ->
        val filteredList = when {
            query.isBlank() -> list
            else -> {
                list.filter { workoutUiModel ->
                    val workoutNameContainsQuery = workoutUiModel.name.contains(query)
                    val anyExerciseNameContainsQuery = workoutUiModel
                        .exerciseList
                        .any { exerciseUiModel ->
                            exerciseUiModel.name.contains(query)
                        }
                    workoutNameContainsQuery || anyExerciseNameContainsQuery
                }
            }
        }

        ScreenState(
            topBarState = topBarState,
            items = filteredList,
            reasonForMessage = when {
                list.isEmpty() -> ScreenState.Reason.EmptyPersistentQuery
                filteredList.isEmpty() -> ScreenState.Reason.EmptySearchQuery
                else -> null
            }
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ScreenState(
                topBarState = TopBarState.Title,
                items = null,
                reasonForMessage = ScreenState.Reason.EmptyPersistentQuery
            )
        )

    fun onToggleTopBarState(newState: TopBarState) {
        if (newState == TopBarState.Title) {
            _currentQuery.value = ""
        }

        topBarStateFlow.value = newState
    }

    fun onSearchChanged(query: String) {
        _currentQuery.value = query

    }

    fun onItemClick(item: WorkoutUiModel) {
        navigationManager.navigateUp()
        navigationManager.navigateToStartWorkoutCountdownScreen(
            item.uuid,
            shouldSaveWorkoutToPersistent
        )
    }

    fun onItemLongClick(item: WorkoutUiModel) {
        navigationManager.navigateToDeleteWorkoutScreen(item.uuid)
    }

    fun onAddClick() {
        navigationManager.navigateToNewWorkoutScreen(shouldSaveWorkoutToPersistent)
    }

    fun onBackClick() {
        navigationManager.navigateUp()
    }

    private fun Duration.toUi() = toComponents { minutes: Long, seconds: Int, nanoseconds: Int ->
        buildString {
            if (minutes != 0L) {
                append(minutes)
                append(" min ")
            }
            if (seconds != 0) {
                append(seconds)
                append(" sec")
            }
        }
    }

    data class WorkoutUiModel(
        val name: String,
        val exerciseList: List<ExerciseUiModel>,
        val uuid: String,
    )

    data class ScreenState(
        val topBarState: TopBarState,
        val items: List<WorkoutUiModel>?,
        val reasonForMessage: Reason?,
    ) {
        enum class Reason {
            EmptyPersistentQuery, EmptySearchQuery
        }
    }
}
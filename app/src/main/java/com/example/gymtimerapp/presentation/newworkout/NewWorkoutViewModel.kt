package com.example.gymtimerapp.presentation.newworkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtimerapp.navigation.NavigationManager
import com.example.gymtimerapp.navigation.NavigationManager.NavigationEvent
import com.example.gymtimerapp.presentation.utils.TopBarState
import com.example.presistent.api.PersistentWorkoutManager
import com.example.presistent.api.WorkoutPersistentModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Duration

class NewWorkoutViewModel(
    shouldSaveWorkoutToPersistent: Boolean,
    private val persistentWorkoutManager: PersistentWorkoutManager,
    private val navigationManager: NavigationManager
) : ViewModel() {

    private var firstEmission = true

    private val shouldSaveWorkoutToPersistentStateFlow =
        MutableStateFlow(shouldSaveWorkoutToPersistent)

    private val _currentQuery = MutableStateFlow("")
    val currentQuery = _currentQuery.value

    private val topBarStateFlow = MutableStateFlow(TopBarState.Title)

    private val selectedSetFlow = MutableStateFlow<Set<String>>(setOf())

    var name = ""
        private set

    @OptIn(FlowPreview::class)
    val state = combine(
        combine(
            selectedSetFlow,
            persistentWorkoutManager.exercisesFlow()
        ) { selectedSet, exercisePersistentModelList ->
            exercisePersistentModelList.map {
                ExerciseUiModel(
                    name = it.name,
                    isSelected = it.uuid in selectedSet,
                    numberOfSets = it.numberOfSets,
                    workDuration = it.workDuration.toUi(),
                    restDuration = it.restDuration.toUi(),
                    finishWorkRemainingDuration = it.finishWorkRemainingDuration.toUi(),
                    finishRestRemainingDuration = it.finishRestRemainingDuration.toUi(),
                    uuid = it.uuid
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
        topBarStateFlow,
        shouldSaveWorkoutToPersistentStateFlow
    ) { list, query, topBarState, shouldSaveWorkoutToPersistentState ->
        val countOfSelected = list.count { it.isSelected }

        val filteredList = when {
            query.isBlank() -> list
            else -> list.filter { model -> model.name.contains(query) }
        }

        ScreenState(
            topBarState = topBarState,
            shouldSaveWorkoutToPersistentState = shouldSaveWorkoutToPersistentState,
            items = filteredList,
            reasonForMessage = when {
                list.isEmpty() -> ScreenState.Reason.EmptyPersistentQuery
                countOfSelected == 0 -> ScreenState.Reason.EmptySelectedList
                filteredList.isEmpty() -> ScreenState.Reason.EmptySearchQuery
                else -> null
            }
        )
    }
        .debounce {
            when (firstEmission) {
                true -> {
                    firstEmission = false
                    300
                }

                false -> 0
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ScreenState(
                topBarState = TopBarState.Title,
                shouldSaveWorkoutToPersistentState = shouldSaveWorkoutToPersistent,
                items = null,
                reasonForMessage = ScreenState.Reason.EmptyPersistentQuery
            )
        )

    fun onShouldSaveWorkoutToPersistent(save: Boolean) {
        shouldSaveWorkoutToPersistentStateFlow.value = save
    }

    fun onNameChange(name: String) {
        this.name = name
    }

    fun onToggleTopBarState(newState: TopBarState) {
        if (newState == TopBarState.Title) {
            _currentQuery.value = ""
        }

        topBarStateFlow.value = newState
    }

    fun onSearchChanged(query: String) {
        _currentQuery.value = query

    }

    fun onItemClick(item: ExerciseUiModel) {
        selectedSetFlow.update {
            it.toMutableSet().also { set ->
                when (set.contains(item.uuid)) {
                    true -> set.remove(item.uuid)
                    false -> set.add(item.uuid)
                }
            }
        }
    }

    fun onDeleteItem(item: ExerciseUiModel) {
        navigationManager.navigateToDeleteExerciseScreen(item.uuid)
    }

    fun onAddClick() {
        navigationManager.navigateToNewExerciseScreen()
    }

    fun onBackClick() {
        navigationManager.navigateUp()
    }

    fun onApplyClick() {
        viewModelScope.launch {
            val uuid = UUID.randomUUID().toString()

            val savedExerciseList = persistentWorkoutManager.exercisesFlow().first()
            persistentWorkoutManager.saveWorkout(
                WorkoutPersistentModel(
                    name = this@NewWorkoutViewModel.name,
                    exerciseList = selectedSetFlow.value.mapNotNull { selectedUUID ->
                        savedExerciseList.firstOrNull { it.uuid == selectedUUID }
                    },
                    uuid = uuid
                )
            )

            navigationManager.navigateUp(NavigationEvent.NavigateUp.ToRoot)
            navigationManager.navigateToStartWorkoutCountdownScreen(
                uuid = uuid,
                shouldSaveWorkoutToPersistent = shouldSaveWorkoutToPersistentStateFlow.value
            )
        }
    }

    private fun Duration.toUi() = when {
        this == Duration.ZERO -> "doesnt setted"
        else -> toComponents { minutes: Long, seconds: Int, nanoseconds: Int ->
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
    }

    data class ExerciseUiModel(
        val name: String,
        val isSelected: Boolean,
        val numberOfSets: Int,
        val workDuration: String,
        val restDuration: String,
        val finishWorkRemainingDuration: String = "doesnt setted",
        val finishRestRemainingDuration: String = "doesnt setted",
        val uuid: String
    )

    data class ScreenState(
        val topBarState: TopBarState,
        val shouldSaveWorkoutToPersistentState: Boolean,
        val items: List<ExerciseUiModel>?,
        val reasonForMessage: Reason?,
    ) {
        enum class Reason {
            EmptyPersistentQuery, EmptySearchQuery, EmptySelectedList
        }
    }

}
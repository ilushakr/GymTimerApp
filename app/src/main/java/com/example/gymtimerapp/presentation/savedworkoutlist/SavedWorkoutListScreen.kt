package com.example.gymtimerapp.presentation.savedworkoutlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtimerapp.presentation.newworkout.NewWorkoutViewModel.ExerciseUiModel
import com.example.gymtimerapp.presentation.theme.ui.GymTimerAppTheme
import com.example.gymtimerapp.presentation.theme.ui.LocalExtendedColors
import com.example.gymtimerapp.presentation.utils.AnimateSearchWrapper
import com.example.gymtimerapp.presentation.utils.DismissableItemWrapper
import com.example.gymtimerapp.presentation.utils.SearchTextField
import com.example.gymtimerapp.presentation.utils.TopBarState
import com.example.gymtimerapp.utils.PreviewCommon
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID
import kotlin.math.abs

@Composable
fun SavedWorkoutListScreen(shouldSaveWorkoutToPersistent: Boolean) {
    val viewModel = koinViewModel<SavedWorkoutListScreenViewModel>(
        parameters = { parametersOf(shouldSaveWorkoutToPersistent) }
    )

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    Content(
        modifier = Modifier.fillMaxSize(),
        items = viewState.items,
        initialSearchQuery = viewModel.currentQuery,
        onSearchChanged = viewModel::onSearchChanged,
        topBarState = viewState.topBarState,
        onToggleTopBarState = viewModel::onToggleTopBarState,
        reasonForMessage = viewState.reasonForMessage,
        onItemClick = viewModel::onItemClick,
        onAddClick = viewModel::onAddClick,
        onDeleteItem = viewModel::onItemLongClick,
        onBackClick = viewModel::onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    initialSearchQuery: String,
    onSearchChanged: (String) -> Unit,
    topBarState: TopBarState,
    onToggleTopBarState: (TopBarState) -> Unit,
    reasonForMessage: SavedWorkoutListScreenViewModel.ScreenState.Reason?,
    items: List<SavedWorkoutListScreenViewModel.WorkoutUiModel>?,
    onItemClick: (SavedWorkoutListScreenViewModel.WorkoutUiModel) -> Unit,
    onAddClick: () -> Unit,
    onDeleteItem: (SavedWorkoutListScreenViewModel.WorkoutUiModel) -> Unit,
    onBackClick: () -> Unit,
) {
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    AnimateSearchWrapper(
                        topBarState = topBarState
                    ) { targetTopBarState ->
                        when (targetTopBarState) {
                            TopBarState.Title -> {
                                Text("My Workout list")
                            }

                            TopBarState.Search -> {
                                SearchTextField(
                                    initialText = initialSearchQuery,
                                    onTextChanged = onSearchChanged,
                                    onChangeState = {
                                        onToggleTopBarState(TopBarState.Title)
                                    }
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    AnimateSearchWrapper(
                        topBarState = topBarState
                    ) { targetTopBarState ->
                        when (targetTopBarState) {
                            TopBarState.Title -> {
                                IconButton(onClick = { onToggleTopBarState(TopBarState.Search) }) {
                                    Icon(Icons.Filled.Search, contentDescription = "Search")
                                }
                            }

                            TopBarState.Search -> {}
                        }
                    }
                }
            )
        },
//        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AnimatedVisibility(
                visible = lazyListState.isScrollingUp(),
                enter = slideInVertically { it * 2 },
                exit = slideOutVertically { it * 2 }
            ) {
                ElevatedButton(
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(16.dp),
                    onClick = onAddClick,
                    colors = ButtonDefaults.elevatedButtonColors().copy(
                        containerColor = FloatingActionButtonDefaults.containerColor
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "New workout")
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            items == null -> Unit

            items.isNotEmpty() -> {
                LazyColumn(
                    state = lazyListState,
                    contentPadding = paddingValues,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    items(
                        items = items,
                        key = { item -> item.uuid }
                    ) {
                        DismissableItemWrapper(
                            onDelete = { onDeleteItem(it) }
                        ) {
                            Item(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .animateItem(),
                                item = it,
                                onItemClick = onItemClick,
                            )
                        }
                    }
                }
            }

            else -> {
                val message = when (reasonForMessage) {
                    SavedWorkoutListScreenViewModel.ScreenState.Reason.EmptyPersistentQuery -> {
                        "You dont have any saved workout"
                    }

                    SavedWorkoutListScreenViewModel.ScreenState.Reason.EmptySearchQuery -> {
                        "For this query you dont have any matching workout or exercises"
                    }

                    null -> ""
                }
                Text(
                    text = message,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Item(
    modifier: Modifier = Modifier,
    item: SavedWorkoutListScreenViewModel.WorkoutUiModel,
    onItemClick: (SavedWorkoutListScreenViewModel.WorkoutUiModel) -> Unit,
) {
    Column(
        modifier = modifier
            .background(
                color = LocalExtendedColors.current.cardBackground,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onItemClick(item) },
            )
            .padding(24.dp),
    ) {
        Text(
            modifier = Modifier,
            style = MaterialTheme.typography.headlineMedium,
            text = item.name
        )

        Spacer(Modifier.height(8.dp))

        Row {
            Column(Modifier.weight(1f)) {
                item.exerciseList.forEach { exercise ->
                    Text(text = "${exercise.name} - ${exercise.numberOfSets} sets")
                }
            }

            Image(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                painter = painterResource(
                    LocalResources.current.getIdentifier(
                        item.iconName,
                        "drawable",
                        LocalContext.current.packageName
                    )
                ),
                contentDescription = null
            )
        }
    }
}

private val SavedWorkoutListScreenViewModel.WorkoutUiModel.iconName
    get() = "ic_gym_animal_${abs(this.hashCode() % 10)}"

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@PreviewCommon
@Composable
private fun ContentPreview() {
    GymTimerAppTheme {
        Content(
            modifier = Modifier.fillMaxSize(),
            initialSearchQuery = "mdfvkls",
            onSearchChanged = {},
            topBarState = TopBarState.Search,
            onToggleTopBarState = {},
            reasonForMessage = null,
            items = List(10) {
                SavedWorkoutListScreenViewModel.WorkoutUiModel(
                    name = "Friday workout",
                    uuid = "",
                    exerciseList = List(6) {
                        ExerciseUiModel(
                            isSelected = false,
                            name = "Pull ups",
                            numberOfSets = 6,
                            workDuration = "1.minutes + 40.seconds",
                            restDuration = "1.minutes",
                            finishWorkRemainingDuration = "10.seconds",
                            uuid = UUID.randomUUID().toString()
                        )
                    }
                )
            },
            onItemClick = {},
            onAddClick = {},
            onDeleteItem = {},
            onBackClick = {},
        )
    }
}
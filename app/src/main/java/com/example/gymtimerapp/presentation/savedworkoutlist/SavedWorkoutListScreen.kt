package com.example.gymtimerapp.presentation.savedworkoutlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presistent.api.WorkoutPersistentModel
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration

@Composable
fun SavedWorkoutListScreen() {
    val viewModel = koinViewModel<SavedWorkoutListScreenViewModel>()

    val items by viewModel.state.collectAsStateWithLifecycle()

    Content(
        modifier = Modifier.fillMaxSize(),
        items = items,
        onAddClick = viewModel::onAddClick,
        onItemClick = viewModel::onItemClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    items: List<WorkoutPersistentModel>?,
    onAddClick: () -> Unit,
    onItemClick: (WorkoutPersistentModel) -> Unit
) {
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("My Workout list") },
//                navigationIcon = {
//                    IconButton(onClick = { /* Open drawer */ }) {
//                        Icon(Icons.Menu, contentDescription = "Menu")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { /* Search action */ }) {
//                        Icon(Icons.Filled.Search, contentDescription = "Search")
//                    }
//                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = lazyListState.isScrollingUp(),
                enter = slideInVertically { it * 2 },
                exit = slideOutVertically { it * 2 }
            ) {
                FloatingActionButton(
                    onClick = onAddClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
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
                        key = { item -> item }
                    ) {
                        Item(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .animateItem(),
                            item = it,
                            onItemClick = onItemClick
                        )
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Text(
                        text = "You dont have any saved workout",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun Item(
    modifier: Modifier = Modifier,
    item: WorkoutPersistentModel,
    onItemClick: (WorkoutPersistentModel) -> Unit
) {
    Row(
        modifier = modifier
            .background(Color(0xffF5F5F5))
            .combinedClickable(
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { },
                onLongClick = {
                    onItemClick(item)
                }
            )
            .padding(24.dp),
    ) {
        Column(
            Modifier.weight(0.7f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier,
                style = MaterialTheme.typography.headlineMedium,
                text = item.name
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            item.exerciseList.forEach { exercise ->
                Text(text = exercise.name)
            }
        }
    }
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

@Preview
@Composable
private fun ContentPreview() {
    MaterialTheme {
        Content(
            modifier = Modifier.fillMaxSize(),
//            items = List(100) {
//                ExercisePersistentModel(
//                    name = "Pull ups",
//                    numberOfSets = 6,
//                    workDuration = 1.minutes + 40.seconds,
//                    restDuration = 1.minutes,
//                    finishWorkRemainingDuration = 10.seconds,
//                    finishRestRemainingDuration = 15.seconds,
//                    uuid = ""
//                )
//            },
            items = emptyList(),
            onAddClick = {},
            onItemClick = {},
        )
    }
}
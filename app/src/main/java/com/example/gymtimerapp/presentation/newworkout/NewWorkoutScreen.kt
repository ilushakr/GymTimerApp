package com.example.gymtimerapp.presentation.newworkout

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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtimerapp.presentation.newworkout.NewWorkoutViewModel.ExerciseUiModel
import com.example.gymtimerapp.presentation.theme.ui.GymTimerAppTheme
import com.example.gymtimerapp.presentation.theme.ui.LocalExtendedColors
import com.example.gymtimerapp.presentation.utils.AnimateSearchWrapper
import com.example.gymtimerapp.presentation.utils.DismissableItemWrapper
import com.example.gymtimerapp.presentation.utils.SearchTextField
import com.example.gymtimerapp.presentation.utils.SupportingText
import com.example.gymtimerapp.presentation.utils.TopBarState
import com.example.gymtimerapp.utils.PreviewCommon
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun NewWorkoutScreen(shouldSaveWorkoutToPersistent: Boolean) {
    val viewModel = koinViewModel<NewWorkoutViewModel>(
        parameters = { parametersOf(shouldSaveWorkoutToPersistent) }
    )

    var textValue by remember { mutableStateOf(TextFieldValue(text = viewModel.name)) }

    val viewState by viewModel.state.collectAsStateWithLifecycle()

    Content(
        Modifier.fillMaxSize(),
        items = viewState.items,
        shouldSaveWorkoutToPersistent = viewState.shouldSaveWorkoutToPersistentState,
        onShouldSaveWorkoutToPersistent = viewModel::onShouldSaveWorkoutToPersistent,
        name = textValue,
        onNameChange = {
            textValue = it
            viewModel.onNameChange(it.text)
        },
        initialSearchQuery = viewModel.currentQuery,
        onSearchChanged = viewModel::onSearchChanged,
        topBarState = viewState.topBarState,
        onToggleTopBarState = viewModel::onToggleTopBarState,
        reasonForMessage = viewState.reasonForMessage,
        onAddClick = viewModel::onAddClick,
        onApplyClick = viewModel::onApplyClick,
        onItemClick = viewModel::onItemClick,
        onDeleteItem = viewModel::onDeleteItem,
        onBackClick = viewModel::onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    items: List<ExerciseUiModel>?,
    name: TextFieldValue,
    shouldSaveWorkoutToPersistent: Boolean,
    onShouldSaveWorkoutToPersistent: (Boolean) -> Unit,
    onNameChange: (TextFieldValue) -> Unit,
    initialSearchQuery: String,
    onSearchChanged: (String) -> Unit,
    topBarState: TopBarState,
    onToggleTopBarState: (TopBarState) -> Unit,
    reasonForMessage: NewWorkoutViewModel.ScreenState.Reason?,
    onAddClick: () -> Unit,
    onApplyClick: () -> Unit,
    onItemClick: (ExerciseUiModel) -> Unit,
    onDeleteItem: (ExerciseUiModel) -> Unit,
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
                                Text(
                                    text = when (shouldSaveWorkoutToPersistent) {
                                        true -> "New Workout"
                                        false -> "Start Workout"
                                    }
                                )
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
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AnimatedVisibility(
                visible = lazyListState.isScrollingUp(),
                enter = slideInVertically { it * 2 },
                exit = slideOutVertically { it * 2 }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val enabled = when (shouldSaveWorkoutToPersistent) {
                        true -> name.text.isNotBlank()
                        false -> true
                    } && items?.any { it.isSelected } == true
                    ElevatedButton(
                        enabled = enabled,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(16.dp),
                        onClick = onApplyClick,
                        colors = ButtonDefaults.elevatedButtonColors().copy(
                            containerColor = FloatingActionButtonDefaults.containerColor
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = when (shouldSaveWorkoutToPersistent) {
                                    true -> "Save and Run"
                                    false -> "Run"
                                }
                            )
                        }
                    }

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
                            Text(text = "New exercise")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        var isChecked by remember { mutableStateOf(shouldSaveWorkoutToPersistent) }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = onNameChange,
                label = { Text("Workout name") },
                singleLine = true,
                supportingText = {
                    SupportingText(visible = name.text.isBlank() && shouldSaveWorkoutToPersistent)
                }
            )

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        indication = LocalIndication.current,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            val newValue = isChecked.not()
                            isChecked = newValue
                            onShouldSaveWorkoutToPersistent(newValue)
                        },
                    )
                    .background(color = LocalExtendedColors.current.cardBackground)
                    .padding(vertical = 8.dp, horizontal = 24.dp),
            ) {
                Text(
                    text = "Save workout to memory",
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = isChecked,
                    onCheckedChange = null,
                    modifier = Modifier.padding(start = 8.dp) // Add some spacing
                )
            }

            Spacer(Modifier.height(16.dp))

            when {
                items == null -> Unit

                items.isNotEmpty() -> {
                    LazyColumn(
                        state = lazyListState,
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
                        NewWorkoutViewModel.ScreenState.Reason.EmptyPersistentQuery -> {
                            "You dont have any saved exercises\nYou have to create add at least one exercise and add it to this workout"
                        }

                        NewWorkoutViewModel.ScreenState.Reason.EmptySearchQuery -> {
                            "For this query you dont have any matching exercises"
                        }

                        NewWorkoutViewModel.ScreenState.Reason.EmptySelectedList -> {
                            "To save or run this workout you have to select at least one exercise"
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

@Composable
private fun Item(
    modifier: Modifier = Modifier,
    item: ExerciseUiModel,
    onItemClick: (ExerciseUiModel) -> Unit,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineMedium,
                text = item.name
            )

            Checkbox(
                item.isSelected,
                onCheckedChange = null
            )
        }

        Spacer(Modifier.height(8.dp))

        Row {
            Column(Modifier.weight(1f)) {
                Text(text = "Job duration - ${item.workDuration}")
                Text("Rest duration - ${item.restDuration}")
                Text("Job remaining - ${item.finishWorkRemainingDuration}")
                Text("Rest remaining - ${item.finishRestRemainingDuration}")
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

private val ExerciseUiModel.iconName
    get() = "ic_gym_animal_${abs(this.uuid.hashCode() % 10)}"

@PreviewCommon
@Composable
private fun NewExerciseScreenPreview() {
    GymTimerAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .background(Color.LightGray)
        ) {
            Content(
                modifier = Modifier.fillMaxWidth(),
                items = List(100) {
                    ExerciseUiModel(
                        isSelected = Random.nextBoolean(),
                        name = "Pull ups",
                        numberOfSets = 6,
                        workDuration = "1.minutes + 40.seconds",
                        restDuration = "1.minutes",
                        finishWorkRemainingDuration = "10.seconds",
//                        finishRestRemainingDuration = 15.seconds,
                        uuid = UUID.randomUUID().toString()
                    )
                },
                //                items = emptyList(),
                name = TextFieldValue("new name"),
                shouldSaveWorkoutToPersistent = true,
                onShouldSaveWorkoutToPersistent = {},
                onNameChange = {},
                initialSearchQuery = "mdfvkls",
                onSearchChanged = {},
                topBarState = TopBarState.Search,
                onToggleTopBarState = {},
                reasonForMessage = null,
                onAddClick = {},
                onApplyClick = {},
                onItemClick = {},
                onDeleteItem = {},
                onBackClick = {},
            )
        }

    }
}
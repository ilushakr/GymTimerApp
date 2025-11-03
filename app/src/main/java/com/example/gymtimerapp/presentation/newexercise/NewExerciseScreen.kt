package com.example.gymtimerapp.presentation.newexercise

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.gymtimerapp.presentation.theme.ui.GymTimerAppTheme
import com.example.gymtimerapp.presentation.utils.SupportingText
import com.example.gymtimerapp.utils.PreviewCommon
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewExerciseScreen() {
    val viewModel = koinViewModel<NewExerciseViewModel>()

    var state by remember {
        mutableStateOf(
            NewExerciseScreenState(
                exerciseNameTextFieldValue = TextFieldValue(viewModel.exerciseName),
                setCountTextFieldValue = TextFieldValue(
                    viewModel.setCount.takeIf { it > 0 }?.toString() ?: ""
                ),
                workDurationTextFieldValue = TextFieldValue(
                    viewModel.workDuration.takeIf { it > 0 }?.toString() ?: ""
                ),
                restDurationTextFieldValue = TextFieldValue(
                    viewModel.restDuration.takeIf { it > 0 }?.toString() ?: ""
                ),
                workRemainingTextFieldValue = TextFieldValue(
                    viewModel.workRemaining?.toString() ?: ""
                ),
                restRemainingTextFieldValue = TextFieldValue(
                    viewModel.restRemaining?.toString() ?: ""
                ),
            )
        )
    }

    Content(
        Modifier.fillMaxWidth(),
        state = state,
        onExerciseNameChanged = {
            state = state.copy(exerciseNameTextFieldValue = it)
            viewModel.exerciseName = it.text
        },
        onSetCountChanged = {
            state = state.copy(setCountTextFieldValue = it)
            viewModel.setCount = it.text.toIntOrNull() ?: 0
        },
        onWorkDurationNameChanged = {
            state = state.copy(workDurationTextFieldValue = it)
            viewModel.workDuration = it.text.toIntOrNull() ?: 0
        },
        onRestDurationNameChanged = {
            state = state.copy(restDurationTextFieldValue = it)
            viewModel.restDuration = it.text.toIntOrNull() ?: 0
        },
        onWorkRemainingNameChanged = {
            state = state.copy(workRemainingTextFieldValue = it)
            viewModel.workRemaining = it.text.toIntOrNull()
        },
        onRestRemainingNameChanged = {
            state = state.copy(restRemainingTextFieldValue = it)
            viewModel.restRemaining = it.text.toIntOrNull()
        },
        onBackClick = viewModel::onBackClick,
        onSaveClick = {
            viewModel.save()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: NewExerciseScreenState,
    onExerciseNameChanged: (TextFieldValue) -> Unit,
    onSetCountChanged: (TextFieldValue) -> Unit,
    onWorkDurationNameChanged: (TextFieldValue) -> Unit,
    onRestDurationNameChanged: (TextFieldValue) -> Unit,
    onWorkRemainingNameChanged: (TextFieldValue) -> Unit,
    onRestRemainingNameChanged: (TextFieldValue) -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
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
                title = {
                    Text("New Exercise")
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.exerciseNameTextFieldValue,
                    onValueChange = onExerciseNameChanged,
                    label = { Text("Exercise name") },
                    singleLine = true,
                    supportingText = { SupportingText(state.showExerciseNameTextFieldRequired) }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.setCountTextFieldValue,
                    onValueChange = onSetCountChanged,
                    label = { Text("Number of sets") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    supportingText = { SupportingText(state.showSetCountTextFieldRequired) }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.workDurationTextFieldValue,
                    onValueChange = onWorkDurationNameChanged,
                    label = { Text("Work duration (seconds)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    supportingText = { SupportingText(state.showWorkDurationTextFieldRequired) }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.restDurationTextFieldValue,
                    onValueChange = onRestDurationNameChanged,
                    label = { Text("Rest duration (seconds)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    supportingText = { SupportingText(state.showRestDurationTextFieldRequired) }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.workRemainingTextFieldValue,
                    onValueChange = onWorkRemainingNameChanged,
                    label = { Text("Work remaining (seconds)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )


                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.restRemainingTextFieldValue,
                    onValueChange = onRestRemainingNameChanged,
                    label = { Text("Rest remaining (seconds)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                )
            }

            Spacer(Modifier.height(16.dp))

            ElevatedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(16.dp),
                onClick = onSaveClick,
                enabled = state.isButtonEnabled,
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
                    Text(text = "Save exercise")
                }
            }
        }
    }
}

data class NewExerciseScreenState(
    val exerciseNameTextFieldValue: TextFieldValue,
    val setCountTextFieldValue: TextFieldValue,
    val workDurationTextFieldValue: TextFieldValue,
    val restDurationTextFieldValue: TextFieldValue,
    val workRemainingTextFieldValue: TextFieldValue,
    val restRemainingTextFieldValue: TextFieldValue,
) {
    val showExerciseNameTextFieldRequired get() = exerciseNameTextFieldValue.text.isBlank()
    val showWorkDurationTextFieldRequired get() = workDurationTextFieldValue.text.isBlank()
    val showRestDurationTextFieldRequired get() = restDurationTextFieldValue.text.isBlank()
    val showSetCountTextFieldRequired get() = setCountTextFieldValue.text.isBlank()

    val isButtonEnabled: Boolean
        get() = exerciseNameTextFieldValue.text.isNotBlank() &&
                workDurationTextFieldValue.text.isNotBlank() &&
                workDurationTextFieldValue.text.isDigitsOnly() &&
                restDurationTextFieldValue.text.isNotBlank() &&
                restDurationTextFieldValue.text.isDigitsOnly()
}

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
                modifier = Modifier
                    .fillMaxWidth(),
                state = NewExerciseScreenState(
                    TextFieldValue("fg"),
                    TextFieldValue("8"),
                    TextFieldValue("4"),
                    TextFieldValue(""),
                    TextFieldValue(""),
                    TextFieldValue(""),
                ),
                onExerciseNameChanged = {},
                onSetCountChanged = {},
                onWorkDurationNameChanged = {},
                onRestDurationNameChanged = {},
                onWorkRemainingNameChanged = {},
                onRestRemainingNameChanged = {},
                onBackClick = {},
                onSaveClick = {},
            )
        }

    }
}
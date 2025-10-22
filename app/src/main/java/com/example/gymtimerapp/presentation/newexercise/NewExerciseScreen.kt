package com.example.gymtimerapp.presentation.newexercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.gymtimerapp.presentation.BottomSheetWrapper
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewExerciseScreenBottomSheet() {
    val viewModel = koinViewModel<NewExerciseViewModel>()

    var state by remember {
        mutableStateOf(
            NewExerciseScreenState(
                exerciseNameTextFieldValue = TextFieldValue(""),
                setCountTextFieldValue = TextFieldValue(""),
                workDurationTextFieldValue = TextFieldValue(""),
                restDurationTextFieldValue = TextFieldValue(""),
                workRemainingTextFieldValue = TextFieldValue(""),
                restRemainingTextFieldValue = TextFieldValue(""),
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
            viewModel.setCount = it.text.toInt()
        },
        onWorkDurationNameChanged = {
            state = state.copy(workDurationTextFieldValue = it)
            viewModel.workDuration = it.text.toInt()
        },
        onRestDurationNameChanged = {
            state = state.copy(restDurationTextFieldValue = it)
            viewModel.restDuration = it.text.toInt()
        },
        onWorkRemainingNameChanged = {
            state = state.copy(workRemainingTextFieldValue = it)
            viewModel.workRemaining = it.text.toInt()
        },
        onRestRemainingNameChanged = {
            state = state.copy(restRemainingTextFieldValue = it)
            viewModel.restRemaining = it.text.toInt()
        },
        onSaveClick = {
            viewModel.save()
        }
    )
}

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
    onSaveClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.exerciseNameTextFieldValue,
            onValueChange = onExerciseNameChanged,
            label = { Text("Exercise name") },
            singleLine = true,
            supportingText = { if (state.showExerciseNameTextFieldRequired) SupportingText() }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.setCountTextFieldValue,
            onValueChange = onSetCountChanged,
            label = { Text("Number of sets") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            supportingText = { if (state.showSetCountTextFieldRequired) SupportingText() }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.workDurationTextFieldValue,
            onValueChange = onWorkDurationNameChanged,
            label = { Text("Work duration (seconds)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            supportingText = { if (state.showWorkDurationTextFieldRequired) SupportingText() }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.restDurationTextFieldValue,
            onValueChange = onRestDurationNameChanged,
            label = { Text("Rest duration (seconds)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            supportingText = { if (state.showRestDurationTextFieldRequired) SupportingText() }
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

        Button(
            onClick = onSaveClick,
            enabled = state.isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Add exercise")
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

@Composable
private fun SupportingText() {
    Text(
        text = "This field is required.",
        color = Color.Red
    )
}

@Preview(showSystemUi = true, showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun NewExerciseScreenPreview(modifier: Modifier = Modifier) {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .background(Color.LightGray)
        ) {
            BottomSheetWrapper(modifier.align(Alignment.BottomCenter)) {
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
                    onSaveClick = {}
                )
            }

        }

    }
}
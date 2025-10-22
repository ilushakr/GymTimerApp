package com.example.gymtimerapp.presentation.newworkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.gymtimerapp.presentation.BottomSheetWrapper
import com.example.gymtimerapp.presentation.savedexerciselist.Item
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewWorkoutScreenBottomSheet() {
    val viewModel = koinViewModel<NewWorkoutViewModel>()

    var
//    var state by remember {
//        mutableStateOf(
//            NewExerciseScreenState(
//                exerciseNameTextFieldValue = TextFieldValue(""),
//                setCountTextFieldValue = TextFieldValue(""),
//                workDurationTextFieldValue = TextFieldValue(""),
//                restDurationTextFieldValue = TextFieldValue(""),
//                workRemainingTextFieldValue = TextFieldValue(""),
//                restRemainingTextFieldValue = TextFieldValue(""),
//            )
//        )
//    }

//    Content(
//        Modifier.fillMaxWidth(),
//        state = state,
//        onWorkoutNameChanged = {
//            state = state.copy(exerciseNameTextFieldValue = it)
//            viewModel.exerciseName = it.text
//        },
//        onSetCountChanged = {
//            state = state.copy(setCountTextFieldValue = it)
//            viewModel.setCount = it.text.toInt()
//        },
//        onWorkDurationNameChanged = {
//            state = state.copy(workDurationTextFieldValue = it)
//            viewModel.workDuration = it.text.toInt()
//        },
//        onRestDurationNameChanged = {
//            state = state.copy(restDurationTextFieldValue = it)
//            viewModel.restDuration = it.text.toInt()
//        },
//        onWorkRemainingNameChanged = {
//            state = state.copy(workRemainingTextFieldValue = it)
//            viewModel.workRemaining = it.text.toInt()
//        },
//        onRestRemainingNameChanged = {
//            state = state.copy(restRemainingTextFieldValue = it)
//            viewModel.restRemaining = it.text.toInt()
//        },
//        onSaveClick = {
//            viewModel.save()
//        }
//    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
//    state: NewExerciseScreenState,

) {

    var textValue = remember { TextFieldValue() }

    LazyColumn(
        state = rememberLazyListState(),
//        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = textValue,
                onValueChange = {
                    textValue = it
                },
                label = { Text("Workout name") },
                singleLine = true,
                supportingText = { if (textValue.text.isBlank()) SupportingText() }
            )
        }

        items(
            items = items,
            key = { item -> item.uuid }
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

                )
            }

        }

    }
}
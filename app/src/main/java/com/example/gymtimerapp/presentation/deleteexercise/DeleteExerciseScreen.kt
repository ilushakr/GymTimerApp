package com.example.gymtimerapp.presentation.deleteexercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtimerapp.presentation.DialogWrapper
import com.example.presistent.api.ExercisePersistentModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun DeleteExerciseScreen(uuid: String) {
    val viewModel = koinViewModel<DeleteExerciseScreenViewModel>(
        parameters = { parametersOf(uuid) }
    )
    viewModel.state.collectAsStateWithLifecycle().value?.let {
        Content(
            modifier = Modifier.fillMaxWidth(),
            model = it,
            viewModel::onCancelClick,
            viewModel::onDeleteClick,
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    model: ExercisePersistentModel,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = buildAnnotatedString {
                append("Do you really want to delete ")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                    append(model.name)
                }

                append(" exercise from memory?")
            }
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onCancelClick
            ) {
                Text("No")
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onDeleteClick
            ) {
                Text("Yes")
            }
        }
    }
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
            DialogWrapper(modifier.align(Alignment.Center)) {
                Content(
                    modifier = Modifier
                        .fillMaxWidth(),
                    model = ExercisePersistentModel(
                        name = "Pull ups",
                        numberOfSets = 6,
                        workDuration = 1.minutes + 40.seconds,
                        restDuration = 1.minutes,
                        finishWorkRemainingDuration = 10.seconds,
                        finishRestRemainingDuration = 15.seconds,
                        uuid = "",
                    ),
                    onCancelClick = {},
                    onDeleteClick = {}
                )
            }

        }

    }
}

package com.example.gymtimerapp.presentation.deleteworkout

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtimerapp.presentation.utils.DialogWrapper
import com.example.gymtimerapp.presentation.theme.ui.GymTimerAppTheme
import com.example.gymtimerapp.presentation.theme.ui.LocalExtendedColors
import com.example.presistent.api.WorkoutPersistentModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Composable
fun DeleteWorkoutScreen(uuid: String) {
    val viewModel = koinViewModel<DeleteWorkoutScreenViewModel>(
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
    model: WorkoutPersistentModel,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {

        val myId = "inlineContentDeleteIcon"

        Text(
            style = TextStyle(fontSize = 18.sp),
            text = buildAnnotatedString {
                append("Do you really want to")

                appendInlineContent(myId, "[icon]")

                append("delete ")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(model.name)
                }

                append(" workout from memory?")
            },
            modifier = Modifier.fillMaxWidth(),
            inlineContent = mapOf(
                Pair(
                    myId,
                    InlineTextContent(
                        Placeholder(
                            width = 24.sp,
                            height = 24.sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) {
                        Icon(
                            Icons.TwoTone.Delete,
                            null,
                            tint = LocalExtendedColors.current.deleteButtonColor
                        )
                    }
                )
            )
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Cancel",
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        indication = LocalIndication.current,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onCancelClick,
                    )
                    .background(color = LocalExtendedColors.current.cancelButtonColor)
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Delete",
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        indication = LocalIndication.current,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onDeleteClick,
                    )
                    .background(color = LocalExtendedColors.current.deleteButtonColor)
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}


@Preview(showSystemUi = true, showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun NewExerciseScreenPreview(modifier: Modifier = Modifier) {
    GymTimerAppTheme {
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
                    model = WorkoutPersistentModel(
                        name = "Friday routine",
                        exerciseList = emptyList(),
                        uuid = UUID.randomUUID().toString(),
                    ),
                    onCancelClick = {},
                    onDeleteClick = {}
                )
            }

        }

    }
}

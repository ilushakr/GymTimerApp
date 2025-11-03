package com.example.gymtimerapp.presentation.startworkoutcountdownscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtimerapp.presentation.theme.ui.GymTimerAppTheme
import com.example.gymtimerapp.presentation.utils.DialogWrapper
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.random.Random

@Composable
fun StartWorkoutCountdownScreen(
    uuid: String,
    shouldSaveWorkoutToPersistent: Boolean,
) {
    val viewModel = koinViewModel<StartWorkoutCountdownScreenViewModel>(
        parameters = { parametersOf(uuid, shouldSaveWorkoutToPersistent) }
    )
    Content(
        modifier = Modifier.fillMaxWidth(),
        state = viewModel.state.collectAsStateWithLifecycle().value,
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(16.dp)),
            painter = painterResource(
                LocalResources.current.getIdentifier(
                    "ic_gym_animal_${Random.nextInt(from = 0, until = 10)}",
                    "drawable",
                    LocalContext.current.packageName
                )
            ),
            contentDescription = null
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = state,
            style = MaterialTheme.typography.headlineMedium
        )
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
                    modifier = Modifier.fillMaxWidth(),
                    state = "GOGOGO"
                )
            }

        }

    }
}

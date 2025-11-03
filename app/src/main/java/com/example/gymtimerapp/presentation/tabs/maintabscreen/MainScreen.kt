package com.example.gymtimerapp.presentation.tabs.maintabscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.example.gymtimerapp.R
import com.example.gymtimerapp.presentation.theme.ui.GymTimerAppTheme
import com.example.gymtimerapp.utils.PreviewCommon
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen() {
    val viewModel = koinViewModel<MainScreenViewModel>()

    Content(
        modifier = Modifier.fillMaxSize(),
        onStartNewWorkout = viewModel::onStartNewWorkout,
        onStartSavedWorkout = viewModel::onStartSavedWorkout,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    onStartNewWorkout: () -> Unit,
    onStartSavedWorkout: () -> Unit,
) {
    val screenSize = LocalWindowInfo.current.containerSize

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Let's gym") })
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(min(screenSize.width.dp, 400.dp))
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StartWorkoutButton(
                    modifier = Modifier.weight(1f),
                    title = "Start new workout",
                    backgroundColor = Color(0xffa4eaba),
                    iconRes = R.drawable.ic_gym_cat_green,
                    onClick = onStartNewWorkout
                )

                StartWorkoutButton(
                    modifier = Modifier.weight(1f),
                    title = "Start saved workout",
                    backgroundColor = Color(0xffffbee2),
                    iconRes = R.drawable.ic_gym_kit_purple,
                    onClick = onStartSavedWorkout
                )
            }
        }
    }
}

@Composable
private fun StartWorkoutButton(
    modifier: Modifier = Modifier,
    title: String,
    backgroundColor: Color,
    iconRes: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(iconRes),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
        )

        Text(
            text = title,
            color = Color.Black
        )
    }
}

@PreviewCommon
@Composable
private fun ContentPreview() {
    GymTimerAppTheme {
        Content(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            onStartNewWorkout = {},
            onStartSavedWorkout = {},
        )
    }
}
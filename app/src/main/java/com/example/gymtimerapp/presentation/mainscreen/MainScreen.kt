package com.example.gymtimerapp.presentation.mainscreen

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen() {
    val viewModel = koinViewModel<MainScreenViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()

    Content(
        modifier = Modifier.fillMaxSize(),
        items = state,
        onItemClick = viewModel::onItemClick
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    items: List<MainScreenViewModel.MainScreenItem>,
    onItemClick: (MainScreenViewModel.MainScreenItem) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.DarkGray.copy(alpha = 0.95f))
//                    .systemBarsPadding()
//                    .height(16.dp)
//                    .clickable(
//                        indication = null,
//                        interactionSource = remember { MutableInteractionSource() },
//                        onClick = {},
//                    )
//            )
        },
    ) { paddingValues ->
        Column(
            Modifier
                .scrollable(
                    state = rememberScrollState(),
                    orientation = Orientation.Vertical,
                    overscrollEffect = null
                )
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    item = item,
                    onCardClick = onItemClick
                )
            }
        }
    }
}

@Composable
private fun Card(
    modifier: Modifier = Modifier,
    item: MainScreenViewModel.MainScreenItem,
    onCardClick: (MainScreenViewModel.MainScreenItem) -> Unit
) {
    Text(
        text = item.title,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xffF5F5F5))
            .clickable(
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onCardClick.invoke(item) }
            )
            .padding(24.dp),
    )
}

@Preview
@Composable
private fun Preview() {
    MaterialTheme {
        Content(
            modifier = Modifier.fillMaxSize(),
            MainScreenViewModel.MainScreenItem.entries,
            onItemClick = {}
        )
    }
}
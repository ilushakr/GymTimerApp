package com.example.gymtimerapp.presentation.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class TopBarState {
    Title, Search
}

@Composable
fun AnimateSearchWrapper(
    modifier: Modifier = Modifier,
    topBarState: TopBarState,
    onNewState: @Composable (TopBarState) -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = topBarState, // Целевое состояние для анимации
        transitionSpec = {
            ContentTransform(
                targetContentEnter = when (this.targetState) {
                    TopBarState.Title -> slideInVertically { width -> -width }
                    TopBarState.Search -> slideInVertically { width -> width }
                },
                initialContentExit = when (this.targetState) {
                    TopBarState.Title -> slideOutVertically { width -> width }
                    TopBarState.Search -> slideOutVertically { width -> -width }
                }
            )
        },
        label = "AnimatedContent"
    ) { targetTopBarState ->
        onNewState(targetTopBarState)
    }
}
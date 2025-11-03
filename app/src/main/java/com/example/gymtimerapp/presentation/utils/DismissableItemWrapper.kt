package com.example.gymtimerapp.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.example.gymtimerapp.presentation.theme.ui.LocalExtendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissableItemWrapper(
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        positionalThreshold = { totalDistance -> totalDistance * 0.5f },
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) onDelete()
            // Reset item when toggling done status
            it != SwipeToDismissBoxValue.EndToStart
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        enableDismissFromStartToEnd = false,
        modifier = modifier.fillMaxWidth(),
        backgroundContent = {
            when (swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> {
                    Icon(
                        imageVector = Icons.TwoTone.Delete,
                        contentDescription = "Remove item",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                lerp(
                                    Color.LightGray,
                                    LocalExtendedColors.current.deleteButtonColor,
                                    swipeToDismissBoxState.progress
                                )
                            )
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(12.dp),
                        tint = Color.White
                    )
                }

                else -> {}
            }
        },
        content = content
    )
}
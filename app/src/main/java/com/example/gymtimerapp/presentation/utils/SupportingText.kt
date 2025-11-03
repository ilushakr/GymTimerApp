package com.example.gymtimerapp.presentation.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.example.gymtimerapp.presentation.theme.ui.LocalExtendedColors

@Composable
fun SupportingText(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.alpha(
            animateFloatAsState(
                targetValue = when (visible) {
                    true -> 1f
                    false -> 0f
                },
                label = "alpha"
            ).value
        ),
        text = "This field is required.",
        color = LocalExtendedColors.current.deleteButtonColor
    )
}
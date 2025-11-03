package com.example.gymtimerapp.presentation.mainscreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.TimeTextDefaults
import com.example.gymtimerapp.presentation.MainScreen
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ResponsiveTimeText

@Composable
fun MainScreen(onFinishWidgetClick: () -> Unit) {
    AppScaffold(
        timeText = {
            ResponsiveTimeText(
                timeTextStyle = TimeTextDefaults.timeTextStyle(
                    color = androidx.wear.compose.material3.MaterialTheme.colorScheme.primary
                )
            )
        }
    ) {
        MainScreen(
            Modifier.fillMaxSize(),
            onFinishWidgetClick = onFinishWidgetClick
        )
    }
}
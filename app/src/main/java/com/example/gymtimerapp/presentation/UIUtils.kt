package com.example.gymtimerapp.presentation

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    val size = LocalWindowInfo.current.containerSize
    return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        size.width > 840
    } else {
        size.width > 600
    }
}
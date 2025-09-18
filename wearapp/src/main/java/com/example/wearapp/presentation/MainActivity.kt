package com.example.wearapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.TimeTextDefaults
import com.example.stopwatch.StopWatch
import com.example.wearapp.presentation.theme.GymTimerAppTheme
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import org.koin.android.ext.android.getKoin


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO remove with Service with wakelock and foreground vibration
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            GymTimerAppTheme {
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
                        onFinishWidgetClick = {
                            finishAffinity()
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getKoin().get<StopWatch>().stopAndReset()
    }
}
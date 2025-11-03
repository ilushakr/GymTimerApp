package com.example.gymtimerapp.utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@PreviewDayNight
//@PreviewDevice
annotation class PreviewCommon


/**
 * Аннотация превью для светлой и темной темы
 */
@Preview(
    name = "night mode",
    group = "ui mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "light mode",
    group = "ui mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
annotation class PreviewDayNight

/**
 * Аннотация превью для светлой и темной темы для планшета
 */
@Preview(
    name = "night mode tablet",
    group = "ui mode",
    device = Devices.TABLET,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "light mode tablet",
    group = "ui mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = Devices.TABLET
)
annotation class PreviewDayNightTablet


@Preview(
    showSystemUi = true,
    name = "tablet",
    group = "devices",
    device = Devices.PIXEL_C,
)
annotation class PreviewDevice
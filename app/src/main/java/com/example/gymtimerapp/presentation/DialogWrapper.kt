package com.example.gymtimerapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DialogWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    MaterialTheme {
        Box(
            modifier = modifier
                .width(
                    when (isTablet()) {
                        true -> 300.dp
                        false -> 280.dp
                    }
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun NewWorkScreenPreview(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(Color.LightGray)
    ) {
        DialogWrapper(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("contensdfd gvdvmfdvgn dfgnfedjgnb ijefdgk b dgf gbd nbjudnbfdgjnbidfgj g rnrjgndt")
        }
    }
}

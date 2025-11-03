package com.example.gymtimerapp.presentation.tabs.settingstabscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.gymtimerapp.R
import com.example.gymtimerapp.presentation.theme.ui.GymTimerAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen() {
    val viewModel = koinViewModel<SettingsScreenViewModel>()

    Content(
        modifier = Modifier.fillMaxSize(),
        onAppBarClick = viewModel::showSnackBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    onAppBarClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(onIconClick = onAppBarClick)
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "This section now is still under development",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Image(
                painter = painterResource(R.drawable.ic_sad_kit),
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    onIconClick: () -> Unit
) {
    var showDropDownMenu by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        actions = {
            IconButton(
                onClick = { showDropDownMenu = true }
            ) {
                Icon(Icons.Default.MoreVert, null)
            }

            DropdownMenu(
                offset = DpOffset((-16).dp, 0.dp),
                expanded = showDropDownMenu,
                onDismissRequest = { showDropDownMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, null)
                            Text("Sad kitten")
                        }
                    },
                    onClick = {
                        showDropDownMenu = false
                        onIconClick()
                    }
                )
            }
        },
        title = {
            Text("Settings")
        }
    )
}

@Preview
@Composable
private fun ContentPreview() {
    GymTimerAppTheme {
        Content(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            onAppBarClick = {}
        )
    }
}
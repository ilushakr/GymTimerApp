package com.example.gymtimerapp.presentation.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import com.example.gymtimerapp.presentation.theme.ui.GymTimerAppTheme
import com.example.gymtimerapp.utils.PreviewCommon

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    initialText: String,
    onTextChanged: (String) -> Unit,
    onChangeState: (TopBarState) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    TextField(
        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = textFieldValue,
        onValueChange = {
            onTextChanged(it.text)
            textFieldValue = it
        },
        singleLine = true,
        placeholder = { Text("Seacrh") },
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            IconButton(onClick = { onChangeState(TopBarState.Title) }) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close search"
                )
            }
        },
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        textFieldValue =
            TextFieldValue(text = initialText, selection = TextRange(initialText.length))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewCommon
@Composable
fun Preview(modifier: Modifier = Modifier) {
    GymTimerAppTheme {
        TopAppBar(
            title = {
                SearchTextField(
                    initialText = "fndkjfsv",
                    onTextChanged = {},
                    onChangeState = {},
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Back"
                    )
                }
            },
        )
    }
}
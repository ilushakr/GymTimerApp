package com.example.gymtimerapp.presentation

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class ModalBottomSheetEvent {
    Show, Hide
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetWrapper(
    modalBottomSheetEventFlow: Flow<ModalBottomSheetEvent>,
    onDismissRequest: () -> Unit = { },
    sheetState: SheetState = rememberModalBottomSheetState(),
    content: @Composable ColumnScope.(closeBottomSheet: () -> Unit) -> Unit,
) {
    var showBottomSheetState by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    if (showBottomSheetState) {
        ModalBottomSheet(
            windowInsets = object : WindowInsets {
                override fun getBottom(density: Density) = 0
                override fun getLeft(density: Density, layoutDirection: LayoutDirection) = 0
                override fun getRight(density: Density, layoutDirection: LayoutDirection) = 0
                override fun getTop(density: Density) = 0
            },
            onDismissRequest = {
                onDismissRequest.invoke()
                showBottomSheetState = false
            },
            sheetState = sheetState
        ) {
            content {
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheetState = false
                    }
                }
            }
        }
    }

    LaunchedEffect(modalBottomSheetEventFlow) {
        modalBottomSheetEventFlow.collectLatest { event ->
            showBottomSheetState = when (event) {
                ModalBottomSheetEvent.Show -> true
                ModalBottomSheetEvent.Hide -> false
            }
        }
    }
}
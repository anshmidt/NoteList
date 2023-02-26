package com.anshmidt.notelist.ui

import BottomSheet
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anshmidt.notelist.ui.composable.AddNoteButton
import com.anshmidt.notelist.ui.composable.Notes
import com.anshmidt.notelist.ui.composable.TopBar
import com.anshmidt.notelist.viewmodel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    //val uiState by viewModel.uiState.collectAsState()
    val listsUiState by viewModel.listsUiState.collectAsState()
    val notesUiState by viewModel.notesUiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    StatusBar()

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = { BottomSheet() },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    lists = listsUiState.lists,
                    mode = listsUiState.mode,
                    selectedList = listsUiState.selectedList,
                    onMoveListToTrashClicked = { selectedList ->
                        viewModel.onMoveListToTrashClicked(selectedList)
                    },
                    onListSelected = { selectedList ->
                        viewModel.onListOpened(selectedList)
                    },
                    onAddNewListButtonClicked = viewModel::onAddNewListButtonClicked,
                    onDoneIconClicked = viewModel::onDoneIconClicked
                )
            },
            floatingActionButton = {
                AddNoteButton(
                    onAddNoteButtonClicked = viewModel::onAddNoteButtonClicked
                )
            },
            content = { padding ->
                Notes(
                    notes = notesUiState.notes,
                    mode = notesUiState.mode,
                    onNoteClicked = { clickedNote ->
                        viewModel.onNoteClicked(clickedNote)
                    },
                    onNoteLongClicked = { longClickedNote ->
                        coroutineScope.launch {
                            if (sheetState.isVisible) sheetState.hide()
                            else sheetState.show()
                        }
                    },
                    onNoteDismissed = { dismissedNote ->
                        viewModel.onNoteDismissed(dismissedNote)
                    },
                    onNoteEdited = { editedNote ->
                        viewModel.onNoteEdited(editedNote)
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        )

    }
}


@Composable
private fun StatusBar() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(
            color = backgroundColor,
            darkIcons = useDarkIcons
        )
        onDispose {}
    }
}


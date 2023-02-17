package com.anshmidt.notelist.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.anshmidt.notelist.ui.composable.AddNoteButton
import com.anshmidt.notelist.ui.composable.Notes
import com.anshmidt.notelist.ui.composable.TopBar
import com.anshmidt.notelist.viewmodel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    //val uiState by viewModel.uiState.collectAsState()
    val listsUiState by viewModel.listsUiState.collectAsState()
    val notesUiState by viewModel.notesUiState.collectAsState()

    StatusBar()

    Scaffold(
        topBar = {
            TopBar(
                lists = listsUiState.lists,
                selectedList = listsUiState.selectedList,
                onMoveListToTrashClicked = { selectedList ->
                    viewModel.onMoveListToTrashClicked(selectedList)
                },
                onListSelected = { selectedList ->
                    viewModel.onListOpened(selectedList)
                },
                onAddNewListButtonClicked = viewModel::onAddNewListButtonClicked
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
                editModeOn = notesUiState.editModeOn,
                onNoteClicked = { clickedNote ->
                    viewModel.onNoteClicked(clickedNote)
                },
                onNoteDismissed = { dismissedNote ->
                    viewModel.onNoteDismissed(dismissedNote)
                },
                modifier = Modifier.padding(padding)
            )
        }
    )
}


@Composable
fun StatusBar() {
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


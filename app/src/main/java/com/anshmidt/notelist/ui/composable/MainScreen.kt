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
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.ui.composable.AddNoteButton
import com.anshmidt.notelist.ui.composable.ListNameDialog
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
    val listsUiState by viewModel.listsUiState.collectAsState()
    val notesUiState by viewModel.notesUiState.collectAsState()
    val screenModeState by viewModel.screenModeState.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    var selectedNote: NoteEntity? by remember {
        mutableStateOf(null)
    }
    val coroutineScope = rememberCoroutineScope()
    var newListNameDialogOpened by remember { mutableStateOf(false) }
    var renameListDialogOpened by remember { mutableStateOf(false) }

    BackHandler(bottomSheetState.isVisible) {
        coroutineScope.launch { bottomSheetState.hide() }
    }

    StatusBar()

    LaunchedEffect(Unit) {
        snapshotFlow { bottomSheetState.currentValue }
            .collect {
                if (bottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
                    selectedNote = null
                }
            }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = { BottomSheet() },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    lists = listsUiState.lists,
                    screenMode = screenModeState,
                    selectedList = listsUiState.selectedList,
                    onMoveListToTrashClicked = { selectedList ->
                        viewModel.onMoveListToTrashClicked(selectedList)
                    },
                    onListSelected = { selectedList ->
                        viewModel.onListOpened(selectedList)
                    },
                    onAddNewListButtonClicked = {
                        newListNameDialogOpened = !newListNameDialogOpened
                    },
                    onDoneIconClicked = viewModel::onDoneIconClicked,
                    onUpIconClicked = viewModel::onUpIconClicked,
                    onRenameListIconClicked = {
                        renameListDialogOpened = !renameListDialogOpened
                    },
                    onOpenTrashClicked = viewModel::onOpenTrashClicked
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
                    screenMode = screenModeState,
                    onNoteClicked = { clickedNote ->
                        viewModel.onNoteClicked(clickedNote)
                    },
                    onNoteLongClicked = { longClickedNote ->
                        coroutineScope.launch {
                            if (bottomSheetState.isVisible) bottomSheetState.hide()
                            else bottomSheetState.show()
                        }
                        selectedNote = longClickedNote
                    },
                    onNoteDismissed = { dismissedNote ->
                        viewModel.onNoteDismissed(dismissedNote)
                    },
                    onNoteEdited = { editedNote ->
                        viewModel.onNoteEdited(editedNote)
                    },
                    selectedItem = selectedNote,
                    modifier = Modifier.padding(padding)
                )
            }
        )

    }

    if (newListNameDialogOpened) {
        ListNameDialog(
            onDialogDismissed = {
                newListNameDialogOpened = !newListNameDialogOpened
            },
            onListRenamed = { newListName ->
                viewModel.onNewListNameEntered(newListName = newListName)
            },
            currentListName = null
        )
    }
    if (renameListDialogOpened) {
        ListNameDialog(
            onDialogDismissed = {
                renameListDialogOpened = !renameListDialogOpened
            },
            onListRenamed = { newListName ->
                viewModel.onListRenamed(newListName = newListName)
            },
            currentListName = listsUiState.selectedList.name
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


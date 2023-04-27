package com.anshmidt.notelist.ui

import BottomSheet
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anshmidt.notelist.ui.composable.*
import com.anshmidt.notelist.ui.uistate.MoveNoteDialogState
import com.anshmidt.notelist.ui.uistate.ScreenMode
import com.anshmidt.notelist.viewmodel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val listsUiState by viewModel.listsUiState.collectAsState()
    val notesUiState by viewModel.notesUiState.collectAsState()
    val screenModeState by viewModel.screenModeState.collectAsState()
    val selectedNoteState by viewModel.selectedNoteState.collectAsState()
    val searchQueryState by viewModel.searchQueryState.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    var newListNameDialogOpened by remember { mutableStateOf(false) }
    var renameListDialogOpened by remember { mutableStateOf(false) }
    var moveNoteDialogState by remember {
        mutableStateOf(MoveNoteDialogState(false, null))
    }

    val navigationCallbacks = NavigationCallbacks(
        onDoneIconClicked = viewModel::onDoneIconClicked,
        onUpIconInTrashClicked = viewModel::onUpIconInTrashClicked,
        onUpIconForSearchClicked = viewModel::onUpIconForSearchClicked,
    )

    val menuCallbacks = MenuCallbacks(
        onRenameListIconClicked = {
            renameListDialogOpened = !renameListDialogOpened
        },
        onMoveListToTrashClicked = { selectedList ->
            viewModel.onMoveListToTrashClicked(selectedList)
        },
        onOpenTrashClicked = viewModel::onOpenTrashClicked,
        onCopyListToClipboardClicked = viewModel::onCopyListToClipboardClicked,
        onAddNotesFromClipboardClicked = viewModel::onAddNotesFromClipboardClicked,
        onEmptyTrashClicked = viewModel::onEmptyTrashClicked
    )

    val searchCallbacks = SearchCallbacks(
        onSearchIconClicked = viewModel::onSearchIconClicked,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onClearSearchFieldIconClicked = viewModel::onClearSearchFieldIconClicked,
        onSearchFieldFocused = viewModel::onSearchFieldFocused
    )

    val noteCallbacks = NoteCallbacks(
        onNoteClicked = { clickedNote ->
            viewModel.onNoteClicked(clickedNote)
        },
        onNoteLongClicked = { longClickedNote ->
            coroutineScope.launch {
                if (bottomSheetState.isVisible) bottomSheetState.hide()
                else bottomSheetState.show()
            }
            viewModel.onNoteSelected(longClickedNote)
        },
        onNoteDismissed = { dismissedNote ->
            viewModel.onNoteDismissed(dismissedNote)
        },
        onNoteEdited = { editedNote ->
            viewModel.onNoteEdited(editedNote)
        },
        onNoteFocused = { focusedNote ->
            viewModel.onNoteSelected(focusedNote)
        }
    )

    AnimatedContent(
        targetState = screenModeState,
        transitionSpec = {
            return@AnimatedContent getScreenAnimationContentTransform(
                initialState = initialState,
                targetState = targetState
            )
        }
    ) { targetScreenMode ->
        BackHandler(bottomSheetState.isVisible) {
            coroutineScope.launch { bottomSheetState.hide() }
        }

        StatusBar()

        LaunchedEffect(Unit) {
            snapshotFlow { bottomSheetState.currentValue }
                .collect {
                    if (bottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
                        viewModel.onNoteSelected(null)
                    }
                }
        }

        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetContent = {
                BottomSheet(
                    screenMode = screenModeState,
                    selectedNote = selectedNoteState,
                    onPutBackClicked = {
                        viewModel.onPutBackClicked(selectedNoteState)
                        coroutineScope.launch { bottomSheetState.hide() }
                    },
                    onMoveClicked = {
                        moveNoteDialogState = MoveNoteDialogState(true, selectedNoteState)
                        coroutineScope.launch { bottomSheetState.hide() }
                    },
                    onPriorityChanged = { priority ->
                        viewModel.onPriorityChanged(selectedNoteState, priority)
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    TopBar(
                        lists = listsUiState.lists,
                        screenMode = targetScreenMode,
                        searchQuery = searchQueryState,
                        selectedList = listsUiState.selectedList,
                        onListSelected = { selectedList ->
                            viewModel.onListOpened(selectedList)
                        },
                        onAddNewListButtonClicked = {
                            newListNameDialogOpened = !newListNameDialogOpened
                        },
                        navigationCallbacks = navigationCallbacks,
                        menuCallbacks = menuCallbacks,
                        searchCallbacks = searchCallbacks
                    )
                },
                floatingActionButton = {
                    AddNoteButton(
                        onAddNoteButtonClicked = viewModel::onAddNoteButtonClicked,
                        screenMode = targetScreenMode,
                        searchQuery = searchQueryState
                    )
                },
                content = { padding ->
                    Notes(
                        modifier = Modifier.padding(padding),
                        notes = notesUiState.notes,
                        screenMode = targetScreenMode,
                        searchQuery = searchQueryState,
                        noteCallbacks = noteCallbacks,
                        selectedItem = selectedNoteState,
                        listOpenedEventFlow = viewModel.listOpenedEventFlow
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
        if (moveNoteDialogState.isOpened) {
            MoveNoteDialog(
                lists = listsUiState.lists,
                onListSelected = { selectedList ->
                    viewModel.onNoteMovedToAnotherList(
                        selectedList,
                        moveNoteDialogState.selectedNote
                    )
                    moveNoteDialogState = MoveNoteDialogState(false, null)
                },
                onDialogDismissed = {
                    moveNoteDialogState = MoveNoteDialogState(false, null)
                }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun getScreenAnimationContentTransform(targetState: ScreenMode, initialState: ScreenMode): ContentTransform {
    if (targetState == ScreenMode.Trash) {
        return ContentTransform(
            targetContentEnter = slideInHorizontally { width -> width },
            initialContentExit = slideOutHorizontally { width -> -width }
        )
    }
    if (initialState == ScreenMode.Trash) {
        return ContentTransform(
            targetContentEnter = slideInHorizontally { width -> -width },
            initialContentExit = slideOutHorizontally { width -> width }
        )
    } else {
        return ContentTransform(
            targetContentEnter = EnterTransition.None,
            initialContentExit = ExitTransition.None
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


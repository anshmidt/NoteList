package com.anshmidt.notelist.ui.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.database.Priority
import com.anshmidt.notelist.ui.uistate.ScreenMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun Notes(
    notes: List<NoteEntity>,
    screenMode: ScreenMode,
    onNoteClicked: (NoteEntity) -> Unit,
    onNoteLongClicked: (NoteEntity) -> Unit,
    onNoteDismissed: (NoteEntity) -> Unit,
    onNoteEdited: (NoteEntity) -> Unit,
    onNoteFocused: (NoteEntity) -> Unit,
    selectedItem: NoteEntity?,
    modifier: Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(modifier = modifier) {
        // this first item fixes the issue with not scrolling automatically when new item added
        item {
            Spacer(Modifier.height(5.dp))
        }

        itemsIndexed(items = notes, key = { _, item -> item.id }) { index, noteEntity ->
            val isItemSelected = selectedItem?.let {
                it.id == noteEntity.id
            } ?: false

            if (selectedItem != null) {
                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        listState.scrollToItem(index)
                    }
                }
            }

            // Swipe to dismiss disabled in Trash mode
            if (screenMode is ScreenMode.Trash) {
                Note(
                    noteEntity = noteEntity,
                    screenMode = screenMode,
                    onNoteClicked = onNoteClicked,
                    onNoteLongClicked = onNoteLongClicked,
                    onNoteEdited = onNoteEdited,
                    listState = listState,
                    isSelected = isItemSelected,
                    coroutineScope = coroutineScope,
                    onTextFieldFocused = {}
                )
            } else {
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        onNoteDismissed(noteEntity)
                        true
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(
                        DismissDirection.StartToEnd,
                        DismissDirection.EndToStart
                    ),
                    background = {},
                    modifier = Modifier
                        .animateItemPlacement(),
                    dismissContent = {
                        Note(
                            noteEntity = noteEntity,
                            screenMode = screenMode,
                            onNoteClicked = onNoteClicked,
                            onNoteLongClicked = onNoteLongClicked,
                            onNoteEdited = onNoteEdited,
                            listState = listState,
                            isSelected = isItemSelected,
                            coroutineScope = coroutineScope,
                            onTextFieldFocused = onNoteFocused
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Note(
    noteEntity: NoteEntity,
    screenMode: ScreenMode,
    onNoteClicked: (NoteEntity) -> Unit,
    onNoteLongClicked: (NoteEntity) -> Unit,
    onNoteEdited: (NoteEntity) -> Unit,
    isSelected: Boolean,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    onTextFieldFocused: (NoteEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = {
                    if (screenMode is ScreenMode.View) {
                        onNoteClicked(noteEntity)
                    }
                },
                onLongClick = {
                    onNoteLongClicked(noteEntity)
                }
            ),
        elevation = 4.dp,
        backgroundColor = getNoteBackground(
            isNoteSelected = isSelected,
            screenMode = screenMode
        )
    ) {
        NoteCardContent(
            note = noteEntity,
            screenMode = screenMode,
            isNoteSelected = isSelected,
            onNoteEdited = onNoteEdited,
            listState = listState,
            coroutineScope = coroutineScope,
            onTextFieldFocused = onTextFieldFocused
        )
    }
}

@Composable
private fun getNoteBackground(isNoteSelected: Boolean, screenMode: ScreenMode): Color {
    return if (isNoteSelected && screenMode is ScreenMode.View)
        MaterialTheme.colors.onBackground.copy(alpha = 0.4f)
    else
        MaterialTheme.colors.background
}

@Composable
private fun NoteCardContent(
    note: NoteEntity,
    screenMode: ScreenMode,
    isNoteSelected: Boolean,
    onNoteEdited: (NoteEntity) -> Unit,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    onTextFieldFocused: (NoteEntity) -> Unit
) {
    Column {
        ListName(
            listName = note.listName,
            screenMode = screenMode
        )
        NoteText(
            note = note,
            screenMode = screenMode,
            isNoteSelected = isNoteSelected,
            onNoteEdited = onNoteEdited,
            listState = listState,
            coroutineScope = coroutineScope,
            onTextFieldFocused = onTextFieldFocused
        )
    }
}

@Composable
fun ListName(
    listName: String?,
    screenMode: ScreenMode
) {
    if (screenMode is ScreenMode.Trash) {
        if (!listName.isNullOrEmpty()) {
            Text(
                text = listName,
                fontSize = 14.sp,
                color = MaterialTheme.colors.primary.copy(alpha = 0.4f),
                modifier = Modifier
                    .padding(top = 17.dp, bottom = 0.dp, start = 17.dp, end = 17.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteText(
    note: NoteEntity,
    screenMode: ScreenMode,
    isNoteSelected: Boolean,
    onNoteEdited: (NoteEntity) -> Unit,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    onTextFieldFocused: (NoteEntity) -> Unit
) {
    when (screenMode) {
        is ScreenMode.Edit -> {
            val focusRequester = remember { FocusRequester() }
            val bringIntoViewRequester = remember { BringIntoViewRequester() }
            SideEffect {
                if (isNoteSelected) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                        focusRequester.requestFocus()
                    }
                }
            }

            var text by remember { mutableStateOf(note.text) }
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    val editedNote = note.copy(text = newText)
                    onNoteEdited(editedNote)
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = MaterialTheme.colors.background,
                    focusedBorderColor = MaterialTheme.colors.primary,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .padding(0.dp)
                    .fillMaxWidth()
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            onTextFieldFocused(note)
                        }
                    }
            )
        }
        is ScreenMode.View, ScreenMode.Trash -> {
            Text(
                text = note.text,
                modifier = Modifier
                    .padding(17.dp)
            )
        }
    }
}

@Composable
fun AddNoteButton(onAddNoteButtonClicked: () -> Unit) {
    FloatingActionButton(
        onClick = {
            onAddNoteButtonClicked()
        },
        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = MaterialTheme.colors.onPrimary,
        )
    }
}

fun Priority.getFontWeight() = when(this) {
    Priority.MINOR -> FontWeight.Light
    Priority.NORMAL -> FontWeight.Normal
    Priority.MAJOR -> FontWeight.Bold
}

object Notes {
    const val TAG = "NotesTag"
}


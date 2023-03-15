package com.anshmidt.notelist.ui.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.database.Priority
import com.anshmidt.notelist.ui.uistate.ScreenMode

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun Notes(
    notes: List<NoteEntity>,
    screenMode: ScreenMode,
    onNoteClicked: (NoteEntity) -> Unit,
    onNoteLongClicked: (NoteEntity) -> Unit,
    onNoteDismissed: (NoteEntity) -> Unit,
    onNoteEdited: (NoteEntity) -> Unit,
    selectedItem: NoteEntity?,
    modifier: Modifier
) {
    val listState = rememberLazyListState()
    LazyColumn(modifier = modifier) {
        items(
            items = notes,
            key = { it.id }
        ) { noteEntity ->
            val isItemSelected = selectedItem?.let {
                it.id == noteEntity.id
            } ?: false

            // Swipe to dismiss disabled in Trash mode
            if (screenMode is ScreenMode.Trash) {
                Note(
                    noteEntity = noteEntity,
                    screenMode = screenMode,
                    onNoteClicked = onNoteClicked,
                    onNoteLongClicked = onNoteLongClicked,
                    onNoteEdited = onNoteEdited,
                    listState = listState,
                    isSelected = isItemSelected
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
                    directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
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
                            isSelected = isItemSelected
                        )
                    }
                )
            }
        }
        item {
            Spacer(Modifier.height(400.dp))
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
    listState: LazyListState
) {
    val coroutineScope = rememberCoroutineScope()
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
        backgroundColor = if (isSelected)
            MaterialTheme.colors.onBackground.copy(alpha = 0.4f)
        else
            MaterialTheme.colors.background
    ) {
        NoteCardContent(
            note = noteEntity,
            screenMode = screenMode,
            onNoteEdited = onNoteEdited
        )
    }
}

@Composable
private fun NoteCardContent(
    note: NoteEntity,
    screenMode: ScreenMode,
    onNoteEdited: (NoteEntity) -> Unit
) {
    Column {
        ListName(
            listName = note.listName,
            screenMode = screenMode
        )
        NoteText(
            note = note,
            screenMode = screenMode,
            onNoteEdited = onNoteEdited
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

@Composable
fun NoteText(
    note: NoteEntity,
    screenMode: ScreenMode,
    onNoteEdited: (NoteEntity) -> Unit
) {
    when (screenMode) {
        is ScreenMode.Edit -> {
            val focusRequester = remember { FocusRequester() }
            SideEffect {
                if (screenMode.focusedNote == note) {
                    focusRequester.requestFocus()
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
                    .padding(0.dp)
                    .fillMaxWidth()
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


package com.anshmidt.notelist.ui.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.ui.NotesMode

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun Notes(
    notes: List<NoteEntity>,
    mode: NotesMode,
    onNoteClicked: (NoteEntity) -> Unit,
    onNoteLongClicked: (NoteEntity) -> Unit,
    onNoteDismissed: (NoteEntity) -> Unit,
    onNoteEdited: (NoteEntity) -> Unit,
    modifier: Modifier
) {
    val listState = rememberLazyListState()
    LazyColumn(modifier = modifier) {
        items(
            items = notes,
            key = { it.id }
        ) { noteEntity ->
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
                        mode = mode,
                        onNoteClicked = onNoteClicked,
                        onNoteLongClicked = onNoteLongClicked,
                        onNoteEdited = onNoteEdited,
                        listState = listState
                    )
                }
            )
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
    mode: NotesMode,
    onNoteClicked: (NoteEntity) -> Unit,
    onNoteLongClicked: (NoteEntity) -> Unit,
    onNoteEdited: (NoteEntity) -> Unit,
    listState: LazyListState
) {
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = {
                    onNoteClicked(noteEntity)
                },
                onLongClick = {
                    onNoteLongClicked(noteEntity)
                }
            ),
        elevation = 4.dp
    ) {
        NoteCardContent(
            note = noteEntity,
            mode = mode,
            onNoteEdited = onNoteEdited
        )
    }
}

@Composable
private fun NoteCardContent(
    note: NoteEntity,
    mode: NotesMode,
    onNoteEdited: (NoteEntity) -> Unit
) {
    when (mode) {
        is NotesMode.Edit -> {
            val focusRequester = remember { FocusRequester() }
            SideEffect {
                if (mode.focusedNote == note) {
                    focusRequester.requestFocus()
                }
            }
            //var text by remember { mutableStateOf(note.text) }
            OutlinedTextField(
                value = note.text,
                onValueChange = { newText ->
                    //text = newText
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
            )
        }
        is NotesMode.View -> {
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
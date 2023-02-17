package com.anshmidt.notelist.ui.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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
    onNoteDismissed: (NoteEntity) -> Unit,
    modifier: Modifier
) {
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
                        onNoteClicked = onNoteClicked
                    )
                }
            )
        }
    }
}

@Composable
fun Note(noteEntity: NoteEntity, mode: NotesMode, onNoteClicked: (NoteEntity) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onNoteClicked(noteEntity) },
        elevation = 4.dp
    ) {
        NoteCardContent(noteEntity = noteEntity, mode = mode)
    }
}

@Composable
fun NoteCardContent(noteEntity: NoteEntity, mode: NotesMode) {
    when (mode) {
        is NotesMode.Edit -> {
            val focusRequester = FocusRequester()
            SideEffect {
                if (mode.focusedNote == noteEntity) {
                    focusRequester.requestFocus()
                }
            }
            OutlinedTextField(
                value = noteEntity.text,
                onValueChange = {},
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
                text = noteEntity.text,
                modifier = Modifier
                    .padding(16.dp)
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
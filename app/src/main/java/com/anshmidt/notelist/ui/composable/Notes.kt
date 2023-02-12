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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anshmidt.notelist.database.NoteEntity

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun Notes(notes: List<NoteEntity>, onNoteDismissed: (NoteEntity) -> Unit, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        items(
            items = notes,
            key = { it.id }
        ) { noteEntity ->
            //Note(noteEntity = it)
            //var unread by remember { mutableStateOf(false) }
//            val dismissState = rememberDismissState(
//                confirmStateChange = {
//                    if (it == DismissValue.DismissedToEnd) unread = !unread
//                    it != DismissValue.DismissedToEnd
//                }
//            )
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
                    Note(noteEntity = noteEntity)
                }
            )
        }
    }
}

@Composable
fun Note(noteEntity: NoteEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { },
        elevation = 4.dp
    ) {
        Text(
            text = noteEntity.text,
            modifier = Modifier
                .padding(16.dp)
        )
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
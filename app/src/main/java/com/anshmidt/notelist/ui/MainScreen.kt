package com.anshmidt.notelist.ui

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.database.Priority
import com.anshmidt.notelist.ui.theme.NoteListTheme
import com.anshmidt.notelist.viewmodel.NoteListViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MainScreen(
    viewModel: NoteListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    StatusBar()

    Scaffold(
        topBar = { TopBar(selectedList = uiState.list) },
        floatingActionButton = {
            AddNoteButton(
                onAddNoteButtonClicked = viewModel::onAddNoteButtonClicked
            )
        },
        content = { padding ->
            Notes(
                notes = uiState.notes,
                onNoteDismissed = { dismissedNote ->
                      viewModel.onNoteDismissed(dismissedNote)
                },
                modifier = Modifier.padding(padding)
            )
        }
    )
}




@Preview
@Composable
fun TopBar(@PreviewParameter(ListPreviewProvider::class) selectedList: ListEntity) {
    TopAppBar(
        elevation = 0.dp,
        title = {
            Text(
                text = selectedList.name,
                color = MaterialTheme.colors.primary
            )
        },
        backgroundColor = Color.Transparent,
//        navigationIcon = {
//            IconButton(onClick = {/* Do Something*/ }) {
//                Icon(Icons.Filled.ArrowBack, null)
//            }
//        },
        actions = {
            SearchIcon()
            MoreIcon()
        }
    )
}

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

@Composable
fun SearchIcon() {
    IconButton(onClick = {/* Do Something*/ }) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}

@Composable
fun MoreIcon() {
    IconButton(onClick = {/* Do Something*/ }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
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

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DefaultPreview() {
    val notes = listOf(
        NoteEntity(
            id = 0,
            timestamp = 1L,
            text = "Note1\nsecond line",
            priority = Priority.NORMAL,
            listId = 0,
            inTrash = false
        ),
        NoteEntity(
            id = 1,
            timestamp = 1L,
            text = "Note2",
            priority = Priority.NORMAL,
            listId = 0,
            inTrash = false
        )
    )

    NoteListTheme {
        Notes(notes, modifier = Modifier, onNoteDismissed = {})
    }
}
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshmidt.notelist.R
import com.anshmidt.notelist.datasources.database.NoteEntity
import com.anshmidt.notelist.datasources.database.Priority
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

        lazyColumnItemsWithPriorityHeader(
            lazyListScope = this,
            priority = Priority.MAJOR,
            notes = notes.filter { it.priority == Priority.MAJOR },
            selectedItem = selectedItem,
            coroutineScope = coroutineScope,
            listState = listState,
            screenMode = screenMode,
            onNoteClicked = onNoteClicked,
            onNoteLongClicked = onNoteLongClicked,
            onNoteEdited = onNoteEdited,
            onNoteDismissed = onNoteDismissed,
            onNoteFocused = onNoteFocused
        )

        lazyColumnItemsWithPriorityHeader(
            lazyListScope = this,
            priority = Priority.NORMAL,
            notes = notes.filter { it.priority == Priority.NORMAL },
            selectedItem = selectedItem,
            coroutineScope = coroutineScope,
            listState = listState,
            screenMode = screenMode,
            onNoteClicked = onNoteClicked,
            onNoteLongClicked = onNoteLongClicked,
            onNoteEdited = onNoteEdited,
            onNoteDismissed = onNoteDismissed,
            onNoteFocused = onNoteFocused
        )

        lazyColumnItemsWithPriorityHeader(
            lazyListScope = this,
            priority = Priority.MINOR,
            notes = notes.filter { it.priority == Priority.MINOR },
            selectedItem = selectedItem,
            coroutineScope = coroutineScope,
            listState = listState,
            screenMode = screenMode,
            onNoteClicked = onNoteClicked,
            onNoteLongClicked = onNoteLongClicked,
            onNoteEdited = onNoteEdited,
            onNoteDismissed = onNoteDismissed,
            onNoteFocused = onNoteFocused
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
private fun lazyColumnItemsWithPriorityHeader(
    lazyListScope: LazyListScope,
    priority: Priority,
    notes: List<NoteEntity>,
    selectedItem: NoteEntity?,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    screenMode: ScreenMode,
    onNoteClicked: (NoteEntity) -> Unit,
    onNoteLongClicked: (NoteEntity) -> Unit,
    onNoteEdited: (NoteEntity) -> Unit,
    onNoteDismissed: (NoteEntity) -> Unit,
    onNoteFocused: (NoteEntity) -> Unit
) {
    if (notes.isNotEmpty()) {
        lazyListScope.item {
            PriorityHeader(priority)
        }
    }

    lazyListScope.itemsIndexed(items = notes, key = { _, item -> item.id }) { index, noteEntity ->
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
    PriorityTag(priority = note.priority)
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
fun PriorityTag(priority: Priority) {
    val text = when (priority) {
//        Priority.MAJOR -> "\uD83D\uDD34"
        Priority.MAJOR -> "◘◘◘"
//        Priority.MAJOR -> "⚑⚑⚑"
//        Priority.NORMAL -> "\uD83D\uDFE1"
        Priority.NORMAL -> "◘◘"
//        Priority.NORMAL -> "⚑⚑"
//        Priority.MINOR -> "\uD83D\uDFE2"
        Priority.MINOR -> "◘"
//        Priority.MINOR -> "⚑"
    }
    Text(
        text = text,
        fontSize = 10.sp,
        color = MaterialTheme.colors.primary.copy(alpha = 0.3f),
        fontWeight = FontWeight.W900,
        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
    )
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
                fontSize = 12.sp,
                color = MaterialTheme.colors.primary.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
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
                    backgroundColor = Color.Transparent,
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
fun AddNoteButton(
    onAddNoteButtonClicked: () -> Unit,
    screenMode: ScreenMode
) {
    if (screenMode == ScreenMode.Trash) return
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

@Composable
fun PriorityHeader(priority: Priority) {
    val text = when (priority) {
        Priority.MINOR -> stringResource(R.string.priority_minor)
        Priority.NORMAL -> stringResource(R.string.priority_normal)
        Priority.MAJOR -> stringResource(R.string.priority_major)
    }
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 24.dp,
                bottom = 0.dp
            ),
        color = MaterialTheme.colors.onBackground.copy(alpha = 0.08f)
    )
}

fun Priority.getFontWeight() = when(this) {
    Priority.MINOR -> FontWeight.ExtraLight
    Priority.NORMAL -> FontWeight.Normal
    Priority.MAJOR -> FontWeight.ExtraBold
}

object Notes {
    const val TAG = "NotesTag"
}


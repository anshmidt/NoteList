package com.anshmidt.notelist.ui.composable

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshmidt.notelist.R
import com.anshmidt.notelist.datasources.database.NoteEntity
import com.anshmidt.notelist.datasources.database.Priority
import com.anshmidt.notelist.datasources.database.TimestampConverter.toHumanReadableDate
import com.anshmidt.notelist.ui.uistate.ScreenMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun Notes(
    modifier: Modifier,
    notes: List<NoteEntity>,
    screenMode: ScreenMode,
    searchQuery: String?,
    noteCallbacks: NoteCallbacks,
    selectedItem: NoteEntity?,
    listOpenedEventFlow: SharedFlow<Unit>
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    if (notes.isEmpty()) {
        NoNotesScreen(screenMode = screenMode, searchQuery = searchQuery)
        return
    }

    /**
     * Scrolling to selected item. We need this only in Edit mode.
     */
    if (screenMode == ScreenMode.Edit) {
        selectedItem?.let { selectedNote ->
            val selectedNoteIndex = notes.indexOf(selectedNote)
            if (selectedNoteIndex >= 0) {
                SideEffect {
                    coroutineScope.launch {
                        val isSelectedNoteVisible = listState.layoutInfo.visibleItemsInfo
                            .indexOfFirst {
                                it.index == selectedNoteIndex
                            } != -1
                        if (!isSelectedNoteVisible) {
                            listState.scrollToItem(selectedNoteIndex)
                        }
                    }

                }
            }
        }
    }

    // When user switches to another list, we scroll to top
    LaunchedEffect(Unit) {
        listOpenedEventFlow.collect {
            coroutineScope.launch {
                listState.scrollToItem(0)
            }
        }
    }

    val shouldShowPriorityHeaders = shouldShowPriorityHeaders(notes)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 80.dp), // prevents content hiding by FAB
        state = listState
    ) {
        itemsIndexed(items = notes, key = { _, item -> item.id }) { index, noteEntity ->

            val isItemSelected = selectedItem?.let {
                it.id == noteEntity.id
            } ?: false

            val shouldShowPriorityHeader = shouldShowPriorityHeaders[index]

            if (shouldShowPriorityHeader) {
                PriorityHeader(priority = noteEntity.priority)
            }

            // Swipe to dismiss disabled in Trash mode
            if (screenMode is ScreenMode.Trash) {
                Note(
                    noteEntity = noteEntity,
                    screenMode = screenMode,
                    searchQuery = searchQuery,
                    onNoteClicked = noteCallbacks.onNoteClicked,
                    onNoteLongClicked = noteCallbacks.onNoteLongClicked,
                    onNoteEdited = noteCallbacks.onNoteEdited,
                    listState = listState,
                    isSelected = isItemSelected,
                    coroutineScope = coroutineScope,
                    onTextFieldFocused = {}
                )
            } else {
                val dismissThreshold = 0.6f
                /**
                 * SwipeToDismiss from the box causes deletions when threshold not reached but
                 * swipe is fast. Here's a solution for it using progress fraction.
                 */
                val currentDismissFraction = remember { mutableStateOf(0f) }

                val dismissState = rememberDismissState(
                    confirmStateChange = { dismissValue ->
                        when (dismissValue) {
                            DismissValue.DismissedToStart, DismissValue.DismissedToEnd -> {
                                if (currentDismissFraction.value >= dismissThreshold &&
                                    currentDismissFraction.value < 1.0f) {
                                    noteCallbacks.onNoteDismissed(noteEntity)
                                    true
                                } else {
                                    false
                                }
                            }
                            else -> {
                                false
                            }
                        }
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(
                        DismissDirection.StartToEnd,
                        DismissDirection.EndToStart
                    ),
                    background = {
                        Box {
                            currentDismissFraction.value = dismissState.progress.fraction
                        }
                    },
                    modifier = Modifier.animateItemPlacement(),
                    dismissContent = {
                        Note(
                            noteEntity = noteEntity,
                            screenMode = screenMode,
                            searchQuery = searchQuery,
                            onNoteClicked = noteCallbacks.onNoteClicked,
                            onNoteLongClicked = noteCallbacks.onNoteLongClicked,
                            onNoteEdited = noteCallbacks.onNoteEdited,
                            listState = listState,
                            isSelected = isItemSelected,
                            coroutineScope = coroutineScope,
                            onTextFieldFocused = noteCallbacks.onNoteFocused
                        )
                    },
                    dismissThresholds = { direction ->
                        FractionalThreshold(dismissThreshold)
                    }
                )
            }
        }
    }
}

/**
 * Priority header is displayed only for first item for each priority
 */
private fun shouldShowPriorityHeaders(sortedNotes: List<NoteEntity>): List<Boolean> {
    val result = mutableListOf<Boolean>()

    val firstMajorItemIndex = sortedNotes.indexOfFirst { it.priority == Priority.MAJOR }
    val firstNormalItemIndex = sortedNotes.indexOfFirst { it.priority == Priority.NORMAL }
    val firstMinorItemIndex = sortedNotes.indexOfFirst { it.priority == Priority.MINOR }

    for (i in sortedNotes.indices) {
        if (
            (i == firstMajorItemIndex) || (i == firstNormalItemIndex) || (i == firstMinorItemIndex)
        ) {
            result.add(i, true)
        } else {
            result.add(i, false)
        }
    }

    return result
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Note(
    noteEntity: NoteEntity,
    screenMode: ScreenMode,
    searchQuery: String?,
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
        elevation = if (isSelected) 0.dp else 4.dp,
        border = BorderStroke(2.dp, getNoteBorderColor(isNoteSelected = isSelected, screenMode = screenMode)),
        backgroundColor = getNoteBackground(
            isNoteSelected = isSelected,
            screenMode = screenMode
        )
    ) {
        NoteCardContent(
            note = noteEntity,
            screenMode = screenMode,
            searchQuery = searchQuery,
            isNoteSelected = isSelected,
            onNoteEdited = onNoteEdited,
            listState = listState,
            coroutineScope = coroutineScope,
            onTextFieldFocused = onTextFieldFocused
        )
    }
}

@Composable
private fun getNoteBorderColor(isNoteSelected: Boolean, screenMode: ScreenMode): Color {
    if (!isNoteSelected) {
        return Color.Transparent
    }
    if ((screenMode is ScreenMode.View || screenMode is ScreenMode.Trash)) {
        return MaterialTheme.colors.onBackground.copy(alpha = 0.25f)
    } else {
        return MaterialTheme.colors.primary
    }
}

@Composable
private fun getNoteBackground(isNoteSelected: Boolean, screenMode: ScreenMode): Color {
    return if (isNoteSelected && (screenMode is ScreenMode.View || screenMode is ScreenMode.Trash))
        MaterialTheme.colors.onBackground.copy(alpha = 0.15f)
    else
        MaterialTheme.colors.background
}

@Composable
private fun NoteCardContent(
    note: NoteEntity,
    screenMode: ScreenMode,
    searchQuery: String?,
    isNoteSelected: Boolean,
    onNoteEdited: (NoteEntity) -> Unit,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    onTextFieldFocused: (NoteEntity) -> Unit
) {
    val shouldDisplayTimestamp = false // TODO move this flag to Settings

    PriorityTag(priority = note.priority)
    Column {
        ListName(
            listName = note.listName,
            screenMode = screenMode,
            searchQuery = searchQuery
        )
        if (shouldDisplayTimestamp) {
            Timestamp(timestamp = note.timestamp)
        }
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
        Priority.MAJOR -> "◘◘◘"
        Priority.NORMAL -> "◘◘"
        Priority.MINOR -> "◘"
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
    screenMode: ScreenMode,
    searchQuery: String?
) {
    if ((screenMode is ScreenMode.Trash) || (searchQuery != null)) {
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

@Composable
fun Timestamp(timestamp: Long) {
    Text(
        text = timestamp.toHumanReadableDate(),
        color = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
        fontSize = 14.sp,
        modifier = Modifier
            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
    )
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
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                textStyle = getNoteTextStyle(priority = note.priority)
                    .copy(fontSize = Notes.NOTE_FONT_SIZE),
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
                style = getNoteTextStyle(priority = note.priority)
                    .copy(fontSize = Notes.NOTE_FONT_SIZE),
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Composable
internal fun getNoteTextStyle(priority: Priority): TextStyle {
    return when (priority) {
        Priority.MAJOR -> LocalTextStyle.current.copy(
            color = MaterialTheme.colors.onBackground,
            fontWeight = FontWeight.Bold
        )
        Priority.NORMAL -> LocalTextStyle.current.copy(
            color = MaterialTheme.colors.onBackground,
            fontWeight = FontWeight.Normal
        )
        Priority.MINOR -> LocalTextStyle.current.copy(
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun AddNoteButton(
    onAddNoteButtonClicked: () -> Unit,
    screenMode: ScreenMode,
    searchQuery: String?
) {
    if (screenMode == ScreenMode.Trash) return
    if (searchQuery != null) return
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

@Composable
fun NoNotesScreen(screenMode: ScreenMode, searchQuery: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Log.d("Searchbranch", "displaying NoNotesScreen: searchQuery = $searchQuery")
        // If search query is not null, but there is no notes to display, an empty screen is shown
        if (searchQuery == null) {
            Text(
                text = getNoNotesScreenTitle(screenMode = screenMode),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun getNoNotesScreenTitle(screenMode: ScreenMode): String {
    return if (screenMode == ScreenMode.Trash) {
        stringResource(id = R.string.no_notes_in_trash_title)
    } else {
        stringResource(id = R.string.no_notes_title)
    }
}

fun Priority.getFontWeight() = when(this) {
    Priority.MINOR -> FontWeight.ExtraLight
    Priority.NORMAL -> FontWeight.Normal
    Priority.MAJOR -> FontWeight.ExtraBold
}

data class NoteCallbacks(
    val onNoteClicked: (NoteEntity) -> Unit,
    val onNoteLongClicked: (NoteEntity) -> Unit,
    val onNoteDismissed: (NoteEntity) -> Unit,
    val onNoteEdited: (NoteEntity) -> Unit,
    val onNoteFocused: (NoteEntity) -> Unit
)

object Notes {
    const val TAG = "NotesTag"
    val NOTE_FONT_SIZE = 20.sp
}


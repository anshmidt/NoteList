package com.anshmidt.notelist.ui.uistate

import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.ui.NotesMode

data class NotesUiState(
    val notes: List<NoteEntity>,
    val mode: NotesMode
)
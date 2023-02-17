package com.anshmidt.notelist.ui.uistate

import com.anshmidt.notelist.database.NoteEntity

data class NotesUiState(
    val notes: List<NoteEntity>
)
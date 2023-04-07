package com.anshmidt.notelist.ui.uistate

import com.anshmidt.notelist.datasources.database.NoteEntity

data class NotesUiState(
    val notes: List<NoteEntity>
)
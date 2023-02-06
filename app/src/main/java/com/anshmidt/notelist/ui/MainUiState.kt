package com.anshmidt.notelist.ui

import com.anshmidt.notelist.database.NoteEntity

data class MainUiState(
    val notes: List<NoteEntity> = emptyList()
)
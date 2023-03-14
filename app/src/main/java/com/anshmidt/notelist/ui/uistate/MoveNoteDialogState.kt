package com.anshmidt.notelist.ui.uistate

import com.anshmidt.notelist.database.NoteEntity

data class MoveNoteDialogState(
    val isOpened: Boolean,
    val selectedNote: NoteEntity?
)
package com.anshmidt.notelist.ui

import com.anshmidt.notelist.database.NoteEntity

sealed class NotesMode {
    object View : NotesMode()
    data class Edit(val focusedNote: NoteEntity) : NotesMode()
}
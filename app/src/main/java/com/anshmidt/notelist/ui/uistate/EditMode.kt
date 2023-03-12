package com.anshmidt.notelist.ui.uistate

import com.anshmidt.notelist.database.NoteEntity

sealed class EditMode {
    object View : EditMode()
    data class Edit(val focusedNote: NoteEntity) : EditMode()
}
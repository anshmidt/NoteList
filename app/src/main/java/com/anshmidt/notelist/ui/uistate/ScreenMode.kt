package com.anshmidt.notelist.ui.uistate

import com.anshmidt.notelist.database.NoteEntity

sealed class ScreenMode {
    object View : ScreenMode()
    data class Edit(val focusedNote: NoteEntity) : ScreenMode()
    object Trash : ScreenMode()
}
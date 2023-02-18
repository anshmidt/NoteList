package com.anshmidt.notelist.ui.uistate

import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.ui.NotesMode

data class ListsUiState(
    val lists: List<ListEntity>,
    val selectedList: ListEntity,
    val mode: NotesMode
)
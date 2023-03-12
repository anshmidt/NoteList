package com.anshmidt.notelist.ui.uistate

import com.anshmidt.notelist.database.ListEntity

data class ListsUiState(
    val lists: List<ListEntity>,
    val selectedList: ListEntity,
    val mode: EditMode
)
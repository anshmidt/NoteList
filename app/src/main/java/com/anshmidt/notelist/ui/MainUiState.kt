package com.anshmidt.notelist.ui

import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NoteEntity

//sealed class MainUiState {
//    data class Loaded(
//        val notes: List<NoteEntity>,
//        val list: ListEntity
//    ) : MainUiState()
//    class Empty : MainUiState()
//}

data class MainUiState(
    val notes: List<NoteEntity>,
    val list: ListEntity
)
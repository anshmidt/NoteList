package com.anshmidt.notelist.repository

import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.database.NotesDatabase
import com.anshmidt.notelist.database.Priority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

class NoteRepository(val notesDatabase: NotesDatabase) {

    fun getNotesInList(listId: Int): Flow<List<NoteEntity>> {
        return flowOf(listOf(
            NoteEntity(
                id = 0,
                timestamp = 1L,
                text = "Note1",
                priority = Priority.NORMAL,
                listId = 0,
                inTrash = false
            ),
            NoteEntity(
                id = 1,
                timestamp = 1L,
                text = "Note2",
                priority = Priority.NORMAL,
                listId = 0,
                inTrash = false
            )
        ))
    }







}
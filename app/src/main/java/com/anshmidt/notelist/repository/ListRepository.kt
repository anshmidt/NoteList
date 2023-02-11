package com.anshmidt.notelist.repository

import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NotesDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class ListRepository(val notesDatabase: NotesDatabase) {

    fun getAllLists(): Flow<List<ListEntity>> {
        return emptyFlow()
    }

    suspend fun addList(listEntity: ListEntity) {
        notesDatabase.listDao().addList(listEntity)
    }
}
package com.anshmidt.notelist.repository

import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NotesDatabase
import com.anshmidt.notelist.sharedpreferences.DataStoreStorage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat

class ListRepository(
    val notesDatabase: NotesDatabase,
    val dataStoreStorage: DataStoreStorage
) {

    fun getAllLists(): Flow<List<ListEntity>> {
        return notesDatabase.listDao().getAllLists()
    }

    suspend fun addList(listEntity: ListEntity) {
        notesDatabase.listDao().addList(listEntity)
    }

    suspend fun deleteList(listEntity: ListEntity) {
        notesDatabase.listDao().deleteList(listEntity)
    }

    @OptIn(FlowPreview::class)
    fun getLastOpenedList(): Flow<ListEntity> {
//        return flowOf(ListEntity(
//            id = 1,
//            name = "RepositoryMock",
//            inTrash = false,
//            timestamp = 0L
//        )).onStart { delay(2000) }
        return dataStoreStorage.getLastOpenedListId().flatMapConcat { lastOpenedListId ->
            notesDatabase.listDao().getListById(lastOpenedListId)
        }
    }

    suspend fun saveLastOpenedList(listEntity: ListEntity) {
        dataStoreStorage.saveLastOpenedListId(listId = listEntity.id)
    }
}
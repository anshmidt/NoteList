package com.anshmidt.notelist.repository

import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NotesDatabase
import com.anshmidt.notelist.sharedpreferences.DataStoreStorage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow

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

    /**
     * If list not found by id, it returns null
     */
    @OptIn(FlowPreview::class)
    fun getLastOpenedList(): Flow<ListEntity?> {
        return dataStoreStorage.getLastOpenedListId().flatMapConcat { lastOpenedListId ->
            notesDatabase.listDao().getListById(lastOpenedListId)
        }
    }

    @OptIn(FlowPreview::class)
    private fun getFirstFoundListIfLastOpenedNotFound(
        lastOpenedListFlow: Flow<ListEntity?>
    ): Flow<ListEntity> {
        return lastOpenedListFlow.flatMapConcat { lastOpenedList ->
            if (lastOpenedList == null) {
                notesDatabase.listDao().getFirstFoundList()
            } else {
                flow {
                    emit(lastOpenedList)
                }
            }
        }
    }


    suspend fun saveLastOpenedList(listEntity: ListEntity) {
        dataStoreStorage.saveLastOpenedListId(listId = listEntity.id)
    }
}
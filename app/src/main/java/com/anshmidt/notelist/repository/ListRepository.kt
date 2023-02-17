package com.anshmidt.notelist.repository

import android.util.Log
import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.database.NotesDatabase
import com.anshmidt.notelist.sharedpreferences.DataStoreStorage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class ListRepository(
    val notesDatabase: NotesDatabase,
    val dataStoreStorage: DataStoreStorage
) {

    fun getAllLists(): Flow<List<ListEntity>> {
        return notesDatabase.listDao().getAllLists().onEach { Log.d(TAG, "getAllLists: $it") }
    }

    suspend fun addList(listEntity: ListEntity): Long = notesDatabase.listDao().addList(listEntity)

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

    fun getLastOpenedListId(): Flow<Int> {
        return dataStoreStorage.getLastOpenedListId().onEach { Log.d(TAG, "getLastOpenedListId: $it") }
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
        Log.d(TAG, "saveLastOpenedList: $listEntity")
        dataStoreStorage.saveLastOpenedListId(listId = listEntity.id)
    }

    companion object {
        val TAG = ListRepository::class.java.simpleName
    }
}
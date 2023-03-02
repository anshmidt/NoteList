package com.anshmidt.notelist.repository

import android.util.Log
import com.anshmidt.notelist.database.AppDatabase
import com.anshmidt.notelist.database.ListEntity
import com.anshmidt.notelist.sharedpreferences.DataStoreStorage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class ListRepository(
    val appDatabase: AppDatabase,
    val dataStoreStorage: DataStoreStorage
) {

    fun getAllLists(): Flow<List<ListEntity>> {
        return appDatabase.listDao().getAllLists().onEach { Log.d(TAG, "getAllLists: $it") }
    }

    suspend fun addList(listEntity: ListEntity): Long = appDatabase.listDao().addList(listEntity)

    suspend fun deleteList(listEntity: ListEntity) {
        appDatabase.listDao().deleteList(listEntity)
    }

    suspend fun updateList(listEntity: ListEntity) {
        appDatabase.listDao().updateList(listEntity)
    }

    /**
     * If list not found by id, it returns null
     */
    @OptIn(FlowPreview::class)
    fun getLastOpenedList(): Flow<ListEntity?> {
        return dataStoreStorage.getLastOpenedListId().flatMapConcat { lastOpenedListId ->
            appDatabase.listDao().getListById(lastOpenedListId)
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
                appDatabase.listDao().getFirstFoundList()
            } else {
                flow {
                    emit(lastOpenedList)
                }
            }
        }
    }

    /**
     * Returns id of any existing list except of the provided one.
     */
    fun getAnyOtherListId(listId: Int): Flow<Int> {
        return appDatabase.listDao().getAnyOtherListId(listId)
    }


    suspend fun saveLastOpenedList(listEntity: ListEntity) {
        Log.d(TAG, "saveLastOpenedList: $listEntity")
        dataStoreStorage.saveLastOpenedListId(listId = listEntity.id)
    }

    suspend fun saveLastOpenedList(listId: Int) {
        dataStoreStorage.saveLastOpenedListId(listId = listId)
    }

    companion object {
        val TAG = ListRepository::class.java.simpleName
    }
}
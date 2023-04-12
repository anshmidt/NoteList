package com.anshmidt.notelist.repository

import android.util.Log
import com.anshmidt.notelist.datasources.database.AppDatabase
import com.anshmidt.notelist.datasources.database.ListEntity
import com.anshmidt.notelist.datasources.sharedpreferences.DataStoreStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class ListRepository(
    val appDatabase: AppDatabase,
    val dataStoreStorage: DataStoreStorage
) {

    fun getAllLists(): Flow<List<ListEntity>> {
        return appDatabase.listDao().getAllLists().onEach { Log.d(TAG, "getAllLists: $it") }
    }

    suspend fun addList(listEntity: ListEntity): Long = appDatabase.listDao().addList(listEntity)

    suspend fun moveListToTrash(listId: Int) {
        appDatabase.listDao().moveListToTrash(listId = listId)
    }

    suspend fun removeListFromTrash(listId: Int) {
        appDatabase.listDao().removeListFromTrash(listId = listId)
    }

    suspend fun updateTimestamp(listId: Int, timestamp: Long) {
        appDatabase.listDao().updateTimestamp(listId = listId, timestamp = timestamp)
    }

    suspend fun updateList(listEntity: ListEntity) {
        appDatabase.listDao().updateList(listEntity)
    }

    fun getLastOpenedListId(): Flow<Int> {
        return dataStoreStorage.getLastOpenedListId().onEach { Log.d(TAG, "getLastOpenedListId: $it") }
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

    suspend fun deleteAllListsThatAreInTrash() {
        appDatabase.listDao().deleteAllListsThatAreInTrash()
    }

    companion object {
        val TAG = ListRepository::class.java.simpleName
    }
}
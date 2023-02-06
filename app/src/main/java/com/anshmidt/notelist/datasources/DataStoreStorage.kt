package com.anshmidt.notelist.datasources

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class DataStoreStorage(private val context: Context) {

    private val Context._dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)
    private val dataStore: DataStore<Preferences> = context._dataStore

    suspend fun saveLastOpenedListId(listId: Int) {
        dataStore.edit { pref ->
            pref[intPreferencesKey(LAST_OPENED_LIST_ID_KEY)] = listId
        }
    }

    fun getLastOpenedListId() = dataStore.data.map { pref ->
        pref[intPreferencesKey(LAST_OPENED_LIST_ID_KEY)] ?: DEFAULT_LAST_OPENED_LIST_ID
    }

    companion object {
        const val PREFERENCE_NAME = "AlarmDataStore"

        private const val LAST_OPENED_LIST_ID_KEY = "selectedListId"
        private const val DEFAULT_LAST_OPENED_LIST_ID = 0
    }
}
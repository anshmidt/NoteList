package com.anshmidt.notelist.repository

import android.util.Log
import com.anshmidt.notelist.database.AppDatabase
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.sharedpreferences.DataStoreStorage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach

class NoteRepository(
    val appDatabase: AppDatabase,
    val dataStoreStorage: DataStoreStorage
) {

    fun getNotesInList(listId: Int): Flow<List<NoteEntity>> {
        return appDatabase.noteDao().getNotesFromList(listId)
            .onEach { Log.d(ListRepository.TAG, "getNotesInList: $it") }
//            .onStart { delay(2000) }
    }

    @OptIn(FlowPreview::class)
    fun getNotesInLastOpenedList(): Flow<List<NoteEntity>> {
        return dataStoreStorage.getLastOpenedListId().flatMapConcat { lastOpenedListId ->
            appDatabase.noteDao().getNotesFromList(lastOpenedListId)
        }
    }

    suspend fun deleteNote(noteEntity: NoteEntity) {
        appDatabase.noteDao().deleteNote(noteEntity)
    }

    suspend fun addNote(note: NoteEntity): Long = appDatabase.noteDao().addNote(note)

    suspend fun updateNote(note: NoteEntity) {
        appDatabase.noteDao().updateNote(note)
    }

    suspend fun deleteAllNotesFromList(listId: Int) {
        appDatabase.noteDao().deleteAllNotesFromList(listId)
    }




}
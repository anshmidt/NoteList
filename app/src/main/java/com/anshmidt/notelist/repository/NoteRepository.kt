package com.anshmidt.notelist.repository

import android.util.Log
import com.anshmidt.notelist.datasources.database.AppDatabase
import com.anshmidt.notelist.datasources.database.NoteEntity
import com.anshmidt.notelist.datasources.database.NoteWithListEntity
import com.anshmidt.notelist.datasources.sharedpreferences.DataStoreStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class NoteRepository(
    val appDatabase: AppDatabase,
    val dataStoreStorage: DataStoreStorage
) {

    fun getNotesInList(listId: Int): Flow<List<NoteEntity>> {
        return appDatabase.noteDao().getNotesFromList(listId)
            .onEach { Log.d(ListRepository.TAG, "getNotesInList: $it") }
    }

    fun getAllNotesInTrash(): Flow<List<NoteWithListEntity>> {
        return appDatabase.noteDao().getAllNotesInTrash()
    }

    suspend fun moveNoteToTrash(noteId: Int) {
        appDatabase.noteDao().moveNoteToTrash(noteId = noteId)
    }

    suspend fun updateTimestamp(noteId: Int, timestamp: Long) {
        appDatabase.noteDao().updateTimestamp(noteId = noteId, timestamp = timestamp)
    }

    suspend fun removeNoteFromTrash(noteId: Int) {
        appDatabase.noteDao().removeNoteFromTrash(noteId = noteId)
    }

    suspend fun addNote(note: NoteEntity): Long = appDatabase.noteDao().addNote(note)

    suspend fun updateNote(note: NoteEntity) {
        appDatabase.noteDao().updateNote(note)
    }

    suspend fun moveToTrashAllNotesFromList(listId: Int) {
        appDatabase.noteDao().moveToTrashAllNotesFromList(listId = listId)
    }

    suspend fun deleteEmptyNotes(listId: Int) {
        appDatabase.noteDao().deleteEmptyNotes(listId = listId)
    }


}
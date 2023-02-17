package com.anshmidt.notelist.repository

import android.util.Log
import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.database.NotesDatabase
import com.anshmidt.notelist.sharedpreferences.DataStoreStorage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach

class NoteRepository(
    val notesDatabase: NotesDatabase,
    val dataStoreStorage: DataStoreStorage
) {

    fun getNotesInList(listId: Int): Flow<List<NoteEntity>> {
        return notesDatabase.noteDao().getNotesFromList(listId).onEach { Log.d(ListRepository.TAG, "getNotesInList: $it") }
//            .onStart { delay(2000) }
    }

    @OptIn(FlowPreview::class)
    fun getNotesInLastOpenedList(): Flow<List<NoteEntity>> {
        return dataStoreStorage.getLastOpenedListId().flatMapConcat { lastOpenedListId ->
            notesDatabase.noteDao().getNotesFromList(lastOpenedListId)
        }
    }

    suspend fun deleteNote(noteEntity: NoteEntity) {
        notesDatabase.noteDao().deleteNote(noteEntity)
    }

    suspend fun addNote(listId: Int) {
        val timeOfCreation = System.currentTimeMillis()
        notesDatabase.noteDao().addNote(
            NoteEntity(
                timestamp = timeOfCreation,
                text = timeOfCreation.toString(),
                listId = listId,
                inTrash = false
            )
        )
    }







}
package com.anshmidt.notelist.repository

import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.database.NotesDatabase
import com.anshmidt.notelist.sharedpreferences.DataStoreStorage
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    val notesDatabase: NotesDatabase,
    val dataStoreStorage: DataStoreStorage
) {

    fun getNotesInList(listId: Int): Flow<List<NoteEntity>> {
        return notesDatabase.noteDao().getAllNotes()  // TODO
    }

    fun getNotesInLastOpenedList(): Flow<List<NoteEntity>> {
        return notesDatabase.noteDao().getAllNotes()
//            .onStart { delay(2000) }
    // TODO rewrite using dataStoreStorage
//        return dataStoreStorage.getLastOpenedListId().flatMapConcat { lastOpenedListId ->
//            notesDatabase.noteDao().getAllNotes()
//        }
    }

    suspend fun deleteNote(noteEntity: NoteEntity) {
        notesDatabase.noteDao().deleteNote(noteEntity)
    }

    suspend fun addNote(noteText: String) {
        notesDatabase.noteDao().addNote(
            NoteEntity(
                timestamp = System.currentTimeMillis(),
                text = noteText,
                listId = 1,
                inTrash = false
            )
        )
    }







}
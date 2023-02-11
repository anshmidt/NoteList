package com.anshmidt.notelist.repository

import com.anshmidt.notelist.database.NoteEntity
import com.anshmidt.notelist.database.NotesDatabase
import kotlinx.coroutines.flow.Flow

class NoteRepository(val notesDatabase: NotesDatabase) {

    fun getNotesInList(listId: Int): Flow<List<NoteEntity>> {
        return notesDatabase.noteDao().getAllNotes()
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
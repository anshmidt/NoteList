package com.anshmidt.notelist.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {


    @Query("SELECT * FROM ${NoteEntity.TABLE_NAME} "+
            "WHERE ${NoteEntity.LIST_ID_COLUMN_NAME}=:listId "+
            "AND ${NoteEntity.IN_TRASH_COLUMN_NAME}=0")
    fun getNotesFromList(listId: Int): Flow<List<NoteEntity>>

//    @Query("DELETE FROM ${NoteEntity.TABLE_NAME} WHERE ${NoteEntity.LIST_ID_COLUMN_NAME}=:listId")
//    suspend fun deleteAllNotesFromList(listId: Int)
//
//    @Delete
//    suspend fun deleteNote(noteEntity: NoteEntity)

    @Query("UPDATE ${NoteEntity.TABLE_NAME} SET ${NoteEntity.IN_TRASH_COLUMN_NAME}=1 "+
            "WHERE ${NoteEntity.LIST_ID_COLUMN_NAME}=:listId")
    suspend fun moveToTrashAllNotesFromList(listId: Int)

    @Query("UPDATE ${NoteEntity.TABLE_NAME} SET ${NoteEntity.IN_TRASH_COLUMN_NAME}=1 "+
            "WHERE ${NoteEntity.ID_COLUMN_NAME}=:noteId")
    suspend fun moveNoteToTrash(noteId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(noteEntity: NoteEntity): Long

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)
}
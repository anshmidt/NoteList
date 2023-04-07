package com.anshmidt.notelist.datasources.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM ${NoteEntity.TABLE} "+
            "WHERE ${NoteEntity.LIST_ID_COLUMN}=:listId "+
            "AND ${NoteEntity.IN_TRASH_COLUMN}=0")
    fun getNotesFromList(listId: Int): Flow<List<NoteEntity>>

    @Query("SELECT ${NoteEntity.TABLE}.*, "+
            "${ListEntity.TABLE}.${ListEntity.NAME_COLUMN} AS ${NoteWithListEntity.LIST_NAME_COLUMN} "+
            "FROM ${NoteEntity.TABLE} INNER JOIN ${ListEntity.TABLE} "+
            "ON ${NoteEntity.TABLE}.${NoteEntity.LIST_ID_COLUMN} = ${ListEntity.TABLE}.${ListEntity.ID_COLUMN} "+
            "WHERE ${NoteEntity.TABLE}.${NoteEntity.IN_TRASH_COLUMN}=1")
    fun getAllNotesInTrash(): Flow<List<NoteWithListEntity>>

//    @Query("DELETE FROM ${NoteEntity.TABLE_NAME} WHERE ${NoteEntity.LIST_ID_COLUMN_NAME}=:listId")
//    suspend fun deleteAllNotesFromList(listId: Int)
//
//    @Delete
//    suspend fun deleteNote(noteEntity: NoteEntity)

    @Query("UPDATE ${NoteEntity.TABLE} SET ${NoteEntity.IN_TRASH_COLUMN}=1 "+
            "WHERE ${NoteEntity.LIST_ID_COLUMN}=:listId")
    suspend fun moveToTrashAllNotesFromList(listId: Int)

    @Query("UPDATE ${NoteEntity.TABLE} SET ${NoteEntity.IN_TRASH_COLUMN}=1 "+
            "WHERE ${NoteEntity.ID_COLUMN}=:noteId")
    suspend fun moveNoteToTrash(noteId: Int)

    @Query("UPDATE ${NoteEntity.TABLE} SET ${NoteEntity.IN_TRASH_COLUMN}=0 "+
            "WHERE ${NoteEntity.ID_COLUMN}=:noteId")
    suspend fun removeNoteFromTrash(noteId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(noteEntity: NoteEntity): Long

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Query("DELETE FROM ${NoteEntity.TABLE} WHERE ${NoteEntity.LIST_ID_COLUMN}=:listId AND ${NoteEntity.TEXT_COLUMN}=''")
    suspend fun deleteEmptyNotes(listId: Int)
}
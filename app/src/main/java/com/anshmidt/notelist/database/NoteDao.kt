package com.anshmidt.notelist.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM ${NoteEntity.TABLE_NAME}")
    fun getAllNotes(): Flow<List<NoteEntity>>
}
package com.anshmidt.notelist.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {
    @Query("SELECT * FROM ${ListEntity.TABLE_NAME}")
    fun getAllLists(): Flow<List<ListEntity>>
}
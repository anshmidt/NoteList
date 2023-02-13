package com.anshmidt.notelist.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {
    @Query("SELECT * FROM ${ListEntity.TABLE_NAME}")
    fun getAllLists(): Flow<List<ListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addList(listEntity: ListEntity)

    @Delete
    suspend fun deleteList(listEntity: ListEntity)

    @Query("SELECT * FROM ${ListEntity.TABLE_NAME} WHERE ${ListEntity.ID_COLUMN_NAME}=:id")
    fun getListById(id: Int): Flow<ListEntity>
}
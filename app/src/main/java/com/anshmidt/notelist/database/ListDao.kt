package com.anshmidt.notelist.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {
    @Query("SELECT * FROM ${ListEntity.TABLE_NAME}")
    fun getAllLists(): Flow<List<ListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addList(listEntity: ListEntity): Long

    @Delete
    suspend fun deleteList(listEntity: ListEntity)

    @Update
    suspend fun updateList(listEntity: ListEntity)

    @Query("SELECT * FROM ${ListEntity.TABLE_NAME} WHERE ${ListEntity.ID_COLUMN_NAME}=:id")
    fun getListById(id: Int): Flow<ListEntity?>

    /**
     * For cases when currently opened list is deleted,
     * and we need to open any existing list from DB
     */
    @Query("SELECT * FROM ${ListEntity.TABLE_NAME} WHERE ${ListEntity.IN_TRASH_COLUMN_NAME}<>0 LIMIT 1")
    fun getFirstFoundList(): Flow<ListEntity>

    @Query("SELECT ${ListEntity.ID_COLUMN_NAME} FROM ${ListEntity.TABLE_NAME} "+
                "WHERE ${ListEntity.ID_COLUMN_NAME}<>:listId LIMIT 1")
    fun getAnyOtherListId(listId: Int): Flow<Int>
}
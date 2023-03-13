package com.anshmidt.notelist.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {
    @Query("SELECT * FROM ${ListEntity.TABLE} WHERE ${ListEntity.IN_TRASH_COLUMN}=0")
    fun getAllLists(): Flow<List<ListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addList(listEntity: ListEntity): Long

//    @Delete
//    suspend fun deleteList(listEntity: ListEntity)

    @Query("UPDATE ${ListEntity.TABLE} SET ${ListEntity.IN_TRASH_COLUMN}=1 "+
            "WHERE ${ListEntity.ID_COLUMN}=:listId")
    suspend fun moveListToTrash(listId: Int)

    @Query("UPDATE ${ListEntity.TABLE} SET ${ListEntity.IN_TRASH_COLUMN}=0 "+
            "WHERE ${ListEntity.ID_COLUMN}=:listId")
    suspend fun removeListFromTrash(listId: Int)

    @Update
    suspend fun updateList(listEntity: ListEntity)

    @Query("SELECT * FROM ${ListEntity.TABLE} WHERE ${ListEntity.ID_COLUMN}=:id")
    fun getListById(id: Int): Flow<ListEntity?>

    /**
     * For cases when currently opened list is deleted,
     * and we need to open any existing list from DB
     */
    @Query("SELECT * FROM ${ListEntity.TABLE} WHERE ${ListEntity.IN_TRASH_COLUMN}=0 LIMIT 1")
    fun getFirstFoundList(): Flow<ListEntity>

    @Query("SELECT ${ListEntity.ID_COLUMN} FROM ${ListEntity.TABLE} "+
            "WHERE ${ListEntity.ID_COLUMN}<>:listId "+
            "AND ${ListEntity.IN_TRASH_COLUMN}=0 LIMIT 1")
    fun getAnyOtherListId(listId: Int): Flow<Int>
}
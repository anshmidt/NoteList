package com.anshmidt.notelist.datasources.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ListEntity.TABLE)
data class ListEntity(
    @ColumnInfo(name = ID_COLUMN)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = NAME_COLUMN)
    val name: String,

    @ColumnInfo(name = IN_TRASH_COLUMN)
    val inTrash: Boolean = false,

    @ColumnInfo(name = TIMESTAMP_COLUMN)
    val timestamp: Long
) {
    companion object {
        const val TABLE = "lists"
        const val ID_COLUMN = "id"
        const val NAME_COLUMN = "name"
        const val IN_TRASH_COLUMN = "in_trash"
        const val TIMESTAMP_COLUMN = "timestamp"
    }
}
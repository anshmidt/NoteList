package com.anshmidt.notelist.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = ListEntity.TABLE_NAME)
data class ListEntity(
    @ColumnInfo(name = ID_COLUMN_NAME)
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = NAME_COLUMN_NAME)
    val name: String,

    @ColumnInfo(name = IN_TRASH_COLUMN_NAME)
    val inTrash: Boolean,

    @ColumnInfo(name = TIMESTAMP_COLUMN_NAME)
    val timestamp: Long
) {
    companion object {
        const val TABLE_NAME = "lists"
        const val ID_COLUMN_NAME = "id"
        const val NAME_COLUMN_NAME = "name"
        const val IN_TRASH_COLUMN_NAME = "in_trash"
        const val TIMESTAMP_COLUMN_NAME = "timestamp"
    }
}
package com.anshmidt.notelist.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = NoteEntity.TABLE_NAME)
data class NoteEntity(
    @ColumnInfo(name = ID_COLUMN_NAME)
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = TIMESTAMP_COLUMN_NAME)
    val timestamp: Long,

    @ColumnInfo(name = TEXT_COLUMN_NAME)
    val text: String,

    @ColumnInfo(name = PRIORITY_COLUMN_NAME)
    @TypeConverters(PriorityConverter::class)
    val priority: Priority,

    @ColumnInfo(name = LIST_ID_COLUMN_NAME)
    val listId: Int,

    @ColumnInfo(name = IN_TRASH_COLUMN_NAME)
    val inTrash: Boolean
) {
    companion object {
        const val TABLE_NAME = "notes"
        const val ID_COLUMN_NAME = "id"
        const val TIMESTAMP_COLUMN_NAME = "timestamp"
        const val TEXT_COLUMN_NAME = "text"
        const val PRIORITY_COLUMN_NAME = "priority"
        const val LIST_ID_COLUMN_NAME = "list_id"
        const val IN_TRASH_COLUMN_NAME = "in_trash"
    }
}
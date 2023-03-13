package com.anshmidt.notelist.database

import androidx.room.*

@Entity(
    tableName = NoteEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = ListEntity::class,
        parentColumns = [ListEntity.ID_COLUMN_NAME],
        childColumns = [NoteEntity.LIST_ID_COLUMN_NAME]
    )]
)
data class NoteEntity(
    @ColumnInfo(name = ID_COLUMN_NAME)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = TIMESTAMP_COLUMN_NAME)
    val timestamp: Long,

    @ColumnInfo(name = TEXT_COLUMN_NAME)
    val text: String,

    @ColumnInfo(name = PRIORITY_COLUMN_NAME)
    @TypeConverters(PriorityConverter::class)
    val priority: Priority = Priority.NORMAL,

    @ColumnInfo(name = LIST_ID_COLUMN_NAME)
    val listId: Int,

    @ColumnInfo(name = IN_TRASH_COLUMN_NAME)
    val inTrash: Boolean = false
) {
    @Ignore
    val listName: String? = null

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
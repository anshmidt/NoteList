package com.anshmidt.notelist.datasources.database

import androidx.room.*

@Entity(
    tableName = NoteEntity.TABLE,
    foreignKeys = [ForeignKey(
        entity = ListEntity::class,
        parentColumns = [ListEntity.ID_COLUMN],
        childColumns = [NoteEntity.LIST_ID_COLUMN]
    )]
)
data class NoteEntity(
    @ColumnInfo(name = ID_COLUMN)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = TIMESTAMP_COLUMN)
    val timestamp: Long,

    @ColumnInfo(name = TEXT_COLUMN)
    val text: String,

    @ColumnInfo(name = PRIORITY_COLUMN)
    @TypeConverters(PriorityConverter::class)
    val priority: Priority = Priority.NORMAL,

    @ColumnInfo(name = LIST_ID_COLUMN)
    val listId: Int,

    @ColumnInfo(name = IN_TRASH_COLUMN)
    val inTrash: Boolean = false
) {
    @Ignore
    var listName: String? = null

    companion object {
        const val TABLE = "notes"
        const val ID_COLUMN = "id"
        const val TIMESTAMP_COLUMN = "timestamp"
        const val TEXT_COLUMN = "text"
        const val PRIORITY_COLUMN = "priority"
        const val LIST_ID_COLUMN = "list_id"
        const val IN_TRASH_COLUMN = "in_trash"
    }
}
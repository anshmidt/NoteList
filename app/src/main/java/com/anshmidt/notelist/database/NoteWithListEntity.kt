package com.anshmidt.notelist.database

import androidx.room.ColumnInfo
import androidx.room.Embedded

/**
 * Is used for output result of JOIN on notes and lists tables
 */
data class NoteWithListEntity(
    @Embedded
    val noteEntity: NoteEntity,
    @ColumnInfo(name = LIST_NAME_COLUMN)
//    @Relation(
//        entity = ListEntity::class,
//        parentColumn = NoteEntity.LIST_ID_COLUMN_NAME,
//        entityColumn = ListEntity.ID_COLUMN_NAME
//    )
    val listName: String
) {
    companion object {
        const val LIST_NAME_COLUMN = "listName"
    }
}

fun NoteWithListEntity.toNoteEntity(): NoteEntity {
    val note = this.noteEntity
    note.listName = this.listName
    return note
}
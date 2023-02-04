package com.anshmidt.notelist.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        NoteEntity::class,
        ListEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun listDao(): ListDao

    companion object {
        const val DATABASE_NAME = "notes_database"
    }
}
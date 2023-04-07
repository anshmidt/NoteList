package com.anshmidt.notelist.datasources.database

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
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun listDao(): ListDao

    companion object {
        const val DATABASE_NAME = "app_database"
    }
}
package com.anshmidt.notelist.di

import androidx.room.Room
import com.anshmidt.notelist.database.NotesDatabase
import org.koin.dsl.module


val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        ).build()
    }
}
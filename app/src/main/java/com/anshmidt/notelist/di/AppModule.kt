package com.anshmidt.notelist.di

import androidx.room.Room
import com.anshmidt.notelist.database.NotesDatabase
import com.anshmidt.notelist.datasources.DataStoreStorage
import com.anshmidt.notelist.repository.ListRepository
import com.anshmidt.notelist.repository.NoteRepository
import com.anshmidt.notelist.viewmodel.NoteListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    single<NotesDatabase> {
        Room.databaseBuilder(
            get(),
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        ).build()
    }
    single { DataStoreStorage(androidContext()) }
    single { NoteRepository(get()) }
    single { ListRepository(get()) }
    viewModel { NoteListViewModel(get(), get()) }
}
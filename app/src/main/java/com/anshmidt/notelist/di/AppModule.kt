package com.anshmidt.notelist.di

import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.anshmidt.notelist.database.DefaultData
import com.anshmidt.notelist.database.NotesDatabase
import com.anshmidt.notelist.repository.ListRepository
import com.anshmidt.notelist.repository.NoteRepository
import com.anshmidt.notelist.sharedpreferences.DataStoreStorage
import com.anshmidt.notelist.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    single<NotesDatabase> {
        val roomDatabaseCallback: RoomDatabase.Callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val defaultData = DefaultData(androidContext())
                CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
                    val notesDatabase: NotesDatabase = get()
                    notesDatabase.listDao().addList(defaultData.defaultList)
                    notesDatabase.noteDao().addNote(defaultData.defaultNote)
                    Log.d("On database created", "DB filled with default data")
                }
            }
        }
        Room.databaseBuilder(
            get(),
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        )
            .addCallback(roomDatabaseCallback)
            .build()
    }

    single { DataStoreStorage(androidContext()) }
    single { NoteRepository(get(), get()) }
    single { ListRepository(get(), get()) }
    viewModel { MainViewModel(get(), get()) }
}
package com.anshmidt.notelist.di

import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.anshmidt.notelist.database.AppDatabase
import com.anshmidt.notelist.database.DefaultData
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
    single<AppDatabase> {
        val roomDatabaseCallback: RoomDatabase.Callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val defaultData = DefaultData(androidContext())
                // Filling database with default data when it's created
                CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
                    val appDatabase: AppDatabase = get()
                    appDatabase.listDao().addList(defaultData.defaultList)
                    appDatabase.noteDao().addNote(defaultData.defaultNote)
                    Log.d("On database created", "DB filled with default data")
                }
            }
        }
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addCallback(roomDatabaseCallback)
            .build()
    }

    single { DataStoreStorage(androidContext()) }
    single { NoteRepository(get(), get()) }
    single { ListRepository(get(), get()) }
    viewModel { MainViewModel(get(), get()) }
}
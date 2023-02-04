package com.anshmidt.notelist.application

import android.app.Application
import androidx.room.Room
import com.anshmidt.notelist.database.NotesDatabase
import com.anshmidt.notelist.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(appModule)
        }
    }

}
package com.hooni.diettracker.core

import android.app.Application
import com.hooni.diettracker.di.databaseModule
import com.hooni.diettracker.di.repositoryModule
import com.hooni.diettracker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        stopKoin()
        startKoin {
            androidContext(this@BaseApplication)
            modules(
                viewModelModule,
                databaseModule,
                repositoryModule
            )
        }
    }
}
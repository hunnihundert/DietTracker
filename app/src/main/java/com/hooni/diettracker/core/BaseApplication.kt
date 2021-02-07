package com.hooni.diettracker.core

import android.app.Application
import com.hooni.diettracker.di.databaseModule
import com.hooni.diettracker.di.repositoryModule
import com.hooni.diettracker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(
                viewModelModule,
                databaseModule,
                repositoryModule
            )
        }
    }
}
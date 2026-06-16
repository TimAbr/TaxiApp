package org.example.project

import android.app.Application
import org.example.project.di.appModule
import org.example.project.di.initKoin
import org.koin.android.ext.koin.androidContext

class TaxiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@TaxiApplication)
            modules(appModule)
        }
    }
}

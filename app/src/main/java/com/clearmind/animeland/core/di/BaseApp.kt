package com.clearmind.animeland.core.di

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            //printLogger
            androidLogger()

            androidContext(this@BaseApp)

            modules(appModules)
        }

    }

}
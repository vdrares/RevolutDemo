package com.example.revolutdemo.di.component

import android.app.Application
import com.example.revolutdemo.di.module.AppModule
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(application: Application)
}
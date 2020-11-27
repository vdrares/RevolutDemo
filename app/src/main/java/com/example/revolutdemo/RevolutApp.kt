package com.example.revolutdemo

import android.app.Application
import com.example.revolutdemo.di.component.AppComponent
import com.example.revolutdemo.di.component.DaggerAppComponent
import com.example.revolutdemo.di.module.AppModule

class RevolutApp : Application() {

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)
    }

    fun getAppComponent() = component

    companion object {
        lateinit var instance: RevolutApp private set
    }
}
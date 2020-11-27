package com.example.revolutdemo.di.component

import com.example.revolutdemo.di.module.ActivityModule
import com.example.revolutdemo.ui.RatesActivity
import dagger.Component

@Component(modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(activity: RatesActivity)
}
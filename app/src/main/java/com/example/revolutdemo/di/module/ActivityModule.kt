package com.example.revolutdemo.di.module

import androidx.appcompat.app.AppCompatActivity
import com.example.revolutdemo.presenter.RatesContract
import com.example.revolutdemo.presenter.RatesPresenter
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: AppCompatActivity) {
    @Provides
    fun provideView() = activity

    @Provides
    fun providePresenter(): RatesContract.Presenter = RatesPresenter()

}
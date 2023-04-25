package com.example.tinkoff_chat_app

import android.app.Application
import com.example.tinkoff_chat_app.di.ApplicationComponent
import com.example.tinkoff_chat_app.di.DaggerApplicationComponent

class App : Application() {
    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.factory().create(this)
    }
}
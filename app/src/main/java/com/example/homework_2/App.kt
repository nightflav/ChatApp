package com.example.homework_2

import android.app.Application
import com.example.homework_2.di.ApplicationComponent
import com.example.homework_2.di.DaggerApplicationComponent

class App : Application() {
    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.builder().build()
    }
}
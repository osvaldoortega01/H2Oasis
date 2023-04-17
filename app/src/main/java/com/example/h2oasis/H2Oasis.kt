package com.example.h2oasis

import android.app.Application

class H2Oasis : Application() {

    companion object{
        lateinit var prefs:Prefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }
}
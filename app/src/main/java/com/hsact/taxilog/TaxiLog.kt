package com.hsact.taxilog

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaxiLog : Application() {
    override fun onCreate() {
        super.onCreate()
        val db = FirebaseApp.initializeApp(this)
    }
}
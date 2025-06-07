package com.hsact.taxilog

import android.app.Application
import com.hsact.taxilog.data.db.AppDatabase

class TaxiLog : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
    }
}
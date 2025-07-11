package com.hsact.taxilog

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.hsact.taxilog.di.SyncManagerEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class TaxiLog : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Log.d("FirebaseInit", "Firebase initialized: ${FirebaseApp.getInstance().name}")
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                val entryPoint = EntryPointAccessors.fromApplication(this, SyncManagerEntryPoint::class.java)
                val shiftSyncManager = entryPoint.shiftSyncManager()
                CoroutineScope(Dispatchers.IO).launch {
                    shiftSyncManager.sync()
                    Log.d("Application Class", "Synced")
                }
            }
        }
    }
}
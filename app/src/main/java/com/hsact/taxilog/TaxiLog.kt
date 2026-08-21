package com.hsact.taxilog

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.hsact.di.ApplicationScope
import com.hsact.domain.usecase.auth.GetAuthStateUseCase
import com.hsact.domain.usecase.shift.SyncShiftsUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class TaxiLog : Application() {
    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var syncShiftsUseCase: SyncShiftsUseCase

    @Inject
    lateinit var getAuthStateUseCase: GetAuthStateUseCase

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Log.d("FirebaseInit", "Firebase initialized: ${FirebaseApp.getInstance().name}")
        applicationScope.launch {
            getAuthStateUseCase().collect { user ->
                if (user != null) {
                    syncShiftsUseCase()
                }
            }
        }
    }
}

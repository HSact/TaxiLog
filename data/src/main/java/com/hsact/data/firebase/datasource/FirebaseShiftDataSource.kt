package com.hsact.data.firebase.datasource

import com.hsact.domain.model.Shift

interface FirebaseShiftDataSource {
    suspend fun getAll(): List<Shift>
    suspend fun save(shift: Shift): String?
    suspend fun delete(remoteId: String)
    suspend fun deleteAll()
}
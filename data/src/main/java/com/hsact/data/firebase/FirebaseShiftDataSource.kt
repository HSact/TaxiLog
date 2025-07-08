package com.hsact.data.firebase

import com.hsact.domain.model.Shift

interface FirebaseShiftDataSource {
    suspend fun save(shift: Shift)
    suspend fun delete(remoteId: String)
}
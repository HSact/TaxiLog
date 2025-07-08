package com.hsact.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.hsact.domain.model.Shift
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class FirebaseShiftDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirebaseShiftDataSource {

    override suspend fun save(shift: Shift) {
        val firebaseShift = shift.toFirebase()
        val documentId = shift.remoteId ?: shift.id.toString()

        firestore.collection("shifts") //TODO: fix
            .document(documentId)
            .set(firebaseShift)
            .await()
    }

    override suspend fun delete(remoteId: String) {
        firestore.collection("shifts")
            .document(remoteId)
            .delete()
            .await()
    }
}
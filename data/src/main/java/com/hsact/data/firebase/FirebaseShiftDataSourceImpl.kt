package com.hsact.data.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.hsact.domain.model.Shift
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseShiftDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirebaseShiftDataSource {
    private val collectionPath = "shifts"

    override suspend fun save(shift: Shift) {
        val firebaseShift = shift.toFirebase()
        val documentId = shift.remoteId ?: shift.id.toString()

        firestore.collection(collectionPath)
            .document(documentId)
            .set(firebaseShift)
            .addOnSuccessListener { Log.d("Firestore", "Shift saved successfully") }
            .addOnFailureListener { e -> Log.e("Firestore", "Failed to save shift", e) }
            .await()
    }

    override suspend fun delete(remoteId: String) {
        firestore.collection(collectionPath)
            .document(remoteId)
            .delete()
            .await()
    }
}
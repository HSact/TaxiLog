package com.hsact.data.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.hsact.data.firebase.model.FirebaseShift
import com.hsact.domain.model.Shift
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseShiftDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : FirebaseShiftDataSource {

    private fun getUserShiftsCollection() = auth.currentUser?.uid?.let { uid ->
        firestore.collection("users").document(uid).collection("shifts")
    }

    override suspend fun getAll(): List<Shift> {
        val collection = getUserShiftsCollection()
        if (collection == null) {
            Log.w("FirebaseShift", "No user is logged in. Can't fetch shifts.")
            return emptyList()
        }
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(FirebaseShift::class.java)?.toDomainOrNull(doc.id)
            }
        } catch (e: Exception) {
            Log.e("FirebaseShift", "Error fetching shifts", e)
            emptyList()
        }
    }

    override suspend fun save(shift: Shift) {
        val firebaseShift = shift.toFirebase()
        val documentId = shift.remoteId ?: UUID.randomUUID().toString()

        val collection = getUserShiftsCollection()
        if (collection == null) {
            Log.w("FirebaseShift", "No user is logged in. Can't save shift.")
            return
        }

        try {
            collection.document(documentId)
                .set(firebaseShift)
                .await()
            Log.d("FirebaseShift", "Shift saved successfully: $documentId")
        } catch (e: Exception) {
            Log.e("FirebaseShift", "Error saving shift: $documentId", e)
        }
    }

    override suspend fun delete(remoteId: String) {
        val collection = getUserShiftsCollection()
        if (collection == null) {
            Log.w("FirebaseShift", "No user is logged in. Can't delete shift.")
            return
        }
        try {
            collection.document(remoteId).delete().await()
            Log.d("FirebaseShift", "Shift deleted: $remoteId")
        } catch (e: Exception) {
            Log.e("FirebaseShift", "Error deleting shift: $remoteId", e)
        }
    }

    override suspend fun deleteAll() {
        val collection = getUserShiftsCollection() ?: return

        try {
            val snapshot = collection.get().await()
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
            Log.d("FirebaseShift", "All remote shifts deleted")
        } catch (e: Exception) {
            Log.e("FirebaseShift", "Error deleting all shifts", e)
        }
    }
}
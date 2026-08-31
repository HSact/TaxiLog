package com.hsact.data.repository.feedback

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.hsact.domain.model.Feedback
import com.hsact.domain.repository.FeedbackRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FeedbackRepository {

    override suspend fun sendFeedback(feedback: Feedback): Boolean {
        return try {
            val feedbackMap = hashMapOf(
                "userId" to feedback.userId,
                "message" to feedback.message,
                "timestamp" to feedback.timestamp.toString(),
                "appVersion" to feedback.appVersion,
                "deviceModel" to feedback.deviceModel,
                "osVersion" to feedback.osVersion
            )

            firestore.collection("feedback")
                .add(feedbackMap)
                .await()

            Log.d("Feedback", "Feedback sent successfully")
            true
        } catch (e: Exception) {
            Log.e("Feedback", "Error sending feedback", e)
            false
        }
    }
}

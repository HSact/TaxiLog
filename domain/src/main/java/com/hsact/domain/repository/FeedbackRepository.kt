package com.hsact.domain.repository

import com.hsact.domain.model.Feedback

/**
 * Interface for sending user feedback to a remote storage.
 */
interface FeedbackRepository {
    /**
     * Sends the given [feedback] to the remote storage.
     * @return Result of the operation (true if success, false otherwise).
     */
    suspend fun sendFeedback(feedback: Feedback): Boolean
}

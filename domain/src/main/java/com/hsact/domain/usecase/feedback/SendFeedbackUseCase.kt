package com.hsact.domain.usecase.feedback

import com.hsact.domain.model.Feedback
import com.hsact.domain.repository.FeedbackRepository

/**
 * Use case for sending user feedback.
 */
class SendFeedbackUseCase(
    private val repository: FeedbackRepository,
) {
    suspend operator fun invoke(feedback: Feedback): Boolean {
        return repository.sendFeedback(feedback)
    }
}

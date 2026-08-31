package com.hsact.taxilog.ui.fragments.feedback

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.Feedback
import com.hsact.domain.usecase.auth.GetAuthStateUseCase
import com.hsact.domain.usecase.feedback.SendFeedbackUseCase
import com.hsact.taxilog.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val sendFeedbackUseCase: SendFeedbackUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {

    private val _feedbackResult = MutableSharedFlow<FeedbackResult>()
    val feedbackResult: SharedFlow<FeedbackResult> = _feedbackResult.asSharedFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private var lastSentTime: Long = 0
    private val cooldownMillis = 60_000 // 1 minute

    fun sendFeedback(message: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSentTime < cooldownMillis) {
            viewModelScope.launch {
                _feedbackResult.emit(FeedbackResult.Cooldown)
            }
            return
        }

        viewModelScope.launch {
            _isSending.value = true
            val user = getAuthStateUseCase().first()
            val userId = user?.uid ?: "anonymous"

            val feedback = Feedback(
                userId = userId,
                message = message,
                timestamp = LocalDateTime.now(),
                appVersion = BuildConfig.VERSION_NAME,
                deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
                osVersion = Build.VERSION.SDK_INT
            )

            val success = sendFeedbackUseCase(feedback)
            _isSending.value = false

            if (success) {
                lastSentTime = System.currentTimeMillis()
                _feedbackResult.emit(FeedbackResult.Success)
            } else {
                _feedbackResult.emit(FeedbackResult.Error)
            }
        }
    }

    sealed interface FeedbackResult {
        object Success : FeedbackResult
        object Error : FeedbackResult
        object Cooldown : FeedbackResult
    }
}

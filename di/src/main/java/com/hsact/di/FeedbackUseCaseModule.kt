package com.hsact.di

import com.hsact.domain.repository.FeedbackRepository
import com.hsact.domain.usecase.feedback.SendFeedbackUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeedbackUseCaseModule {
    @Provides
    @Singleton
    fun provideSendFeedbackUseCase(repository: FeedbackRepository): SendFeedbackUseCase {
        return SendFeedbackUseCase(repository)
    }
}

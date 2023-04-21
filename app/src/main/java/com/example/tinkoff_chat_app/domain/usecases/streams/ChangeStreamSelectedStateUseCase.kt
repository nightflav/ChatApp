package com.example.tinkoff_chat_app.domain.usecases.streams

import com.example.tinkoff_chat_app.domain.repository.streams_repository.StreamsRepository
import com.example.tinkoff_chat_app.domain.repository.streams_repository.StreamsRepositoryImpl
import com.example.tinkoff_chat_app.models.stream_screen_models.StreamModel
import javax.inject.Inject

class ChangeStreamSelectedStateUseCase @Inject constructor(
    private val streamRepo: StreamsRepository
) {
    suspend operator fun invoke(stream: StreamModel, showSubs: Boolean) {
        (streamRepo as StreamsRepositoryImpl).changeStreamSelectedState(stream, showSubs)
    }
}
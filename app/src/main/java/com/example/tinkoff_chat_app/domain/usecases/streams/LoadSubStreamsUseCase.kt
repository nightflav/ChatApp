package com.example.tinkoff_chat_app.domain.usecases.streams

import com.example.tinkoff_chat_app.domain.repository.streams_repository.StreamsRepository
import com.example.tinkoff_chat_app.models.stream_screen_models.StreamScreenItem
import javax.inject.Inject

class LoadSubStreamsUseCase @Inject constructor(
    private val streamRepo: StreamsRepository
) {
    suspend operator fun invoke(): List<StreamScreenItem> {
        return streamRepo.getSubscribedStreams()
    }
}
package com.example.tinkoff_chat_app.domain.repository.streams_repository

import com.example.tinkoff_chat_app.models.data_transfer_models.StreamDto
import com.example.tinkoff_chat_app.models.data_transfer_models.TopicDto
import com.example.tinkoff_chat_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow

interface StreamsRepository {

    val currStreams: MutableStateFlow<Resource<List<StreamDto>?>>

    suspend fun loadAllStreams(
        shouldFetch: Boolean,
        onError: () -> Unit
    )

    suspend fun loadAllSubscriptions(
        shouldFetch: Boolean,
        onError: () -> Unit
    )

    suspend fun updateStreamTopics(
        streamId: Int,
        newTopics: List<TopicDto>,
    )

    suspend fun subscribeForStream(
        streamName: String,
        description: String?,
        announce: Boolean
    )

    suspend fun registerQueue(
        type: String
    ): Map<String, String>

    suspend fun getEventsFromQueue(
        queue: Map<String, String>
    ): String

}
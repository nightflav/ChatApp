package com.example.tinkoff_chat_app.domain.repository.topics_repository

import com.example.tinkoff_chat_app.models.data_transfer_models.TopicDto
import com.example.tinkoff_chat_app.utils.Resource

interface TopicsRepository {

    suspend fun loadAllTopicsOfCurrentStream(
        streamId: Int,
        shouldFetch: Boolean,
        streamName: String
    ): Resource<List<TopicDto>>

}
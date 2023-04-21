package com.example.tinkoff_chat_app.domain.repository.streams_repository

import com.example.tinkoff_chat_app.models.stream_screen_models.StreamScreenItem

interface StreamsRepository {

    suspend fun getAllStreams(): List<StreamScreenItem>

    suspend fun getSubscribedStreams(): List<StreamScreenItem>

}
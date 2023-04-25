package com.example.tinkoff_chat_app.domain.repository.streams_repository

import com.example.tinkoff_chat_app.models.stream_screen_models.StreamScreenItem

interface StreamsRepository {

    suspend fun getAllStreamsNetwork(): List<StreamScreenItem>

    suspend fun getSubscribedStreamsNetwork(): List<StreamScreenItem>

    suspend fun getAllStreamsLocal(): List<StreamScreenItem>

    suspend fun getSubscribedStreamsLocal(): List<StreamScreenItem>

}
package com.example.homework_2.repository.streams_repository

import com.example.homework_2.models.stream_screen_models.StreamScreenItem

interface StreamsRepository {

    suspend fun getAllStreams(): List<StreamScreenItem>

    suspend fun getSubscribedStreams(): List<StreamScreenItem>

}
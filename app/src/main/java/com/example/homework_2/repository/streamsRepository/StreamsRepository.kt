package com.example.homework_2.repository.streamsRepository

import com.example.homework_2.models.streamScreenModels.StreamScreenItem

interface StreamsRepository {

    suspend fun getAllStreams(): List<StreamScreenItem>

    suspend fun getSubscribedStreams(): List<StreamScreenItem>

}
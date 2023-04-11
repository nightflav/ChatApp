package com.example.homework_2.repository.streamsRepository

import com.example.homework_2.screens.stream.StreamScreenState
import kotlinx.coroutines.flow.Flow

interface StreamsRepository {

    suspend fun getAllStreams(): Flow<StreamScreenState>

    suspend fun getSubscribedStreams(): Flow<StreamScreenState>

    suspend fun getSearchStream(
        request: String,
        subOnly: Boolean
    ): Flow<StreamScreenState>

}
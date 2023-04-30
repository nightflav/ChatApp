package com.example.tinkoff_chat_app.domain.repository.streams_repository

import com.example.tinkoff_chat_app.data.streams.StreamDao
import com.example.tinkoff_chat_app.models.data_transfer_models.StreamDto
import com.example.tinkoff_chat_app.models.data_transfer_models.TopicDto
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.utils.Resource
import com.example.tinkoff_chat_app.utils.StreamMappers.toDatabaseStreamModel
import com.example.tinkoff_chat_app.utils.StreamMappers.toDatabaseSubscriptionModel
import com.example.tinkoff_chat_app.utils.StreamMappers.toStreamDtoList
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class StreamsRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val streamDao: StreamDao,
) : StreamsRepository {

    override val currStreams = MutableStateFlow<Resource<List<StreamDto>?>>(Resource.Loading())
    private val streams
        get() = currStreams.value

    override suspend fun loadAllStreams(
        shouldFetch: Boolean,
        onError: () -> Unit
    ) {
        val data = streamDao.getAllStreams().toStreamDtoList()

        currStreams.emit(
            Resource.Success(
                data = data
            )
        )

        if (shouldFetch) {
            currStreams.emit(
                Resource.Loading()
            )
            try {
                val networkData = chatApi.getStreams().body()!!.streams
                streamDao.deleteAllStreams()
                streamDao.addStreams(
                    networkData.toStreamDtoList().map { it.toDatabaseStreamModel() })
                currStreams.emit(
                    Resource.Success(
                        data = networkData.toStreamDtoList()
                    )
                )
            } catch (e: Exception) {
                onError()
                currStreams.emit(
                    Resource.Success(
                        data = data
                    )
                )
            }
        } else {
            currStreams.emit(
                Resource.Success(
                    data = data
                )
            )
        }
    }

    override suspend fun loadAllSubscriptions(
        shouldFetch: Boolean,
        onError: () -> Unit
    ) {
        val data = streamDao.getAllSubscriptions().toStreamDtoList()

        currStreams.emit(
            Resource.Success(
                data = data
            )
        )

        if (shouldFetch) {
            currStreams.emit(
                Resource.Loading()
            )
            try {
                val networkData = chatApi.getSubscriptions().body()!!.subscriptions
                streamDao.deleteAllStreams()
                streamDao.addSubscriptions(
                    networkData.toStreamDtoList().map { it.toDatabaseSubscriptionModel() }
                )
                currStreams.emit(
                    Resource.Success(
                        data = networkData.toStreamDtoList()
                    )
                )
            } catch (e: Exception) {
                onError()
                currStreams.emit(
                    Resource.Success(
                        data = data
                    )
                )
            }
        } else {
            currStreams.emit(
                Resource.Success(
                    data = data
                )
            )
        }
    }

    override suspend fun updateStreamTopics(streamId: Int, newTopics: List<TopicDto>) {
        val streamToUpdate: StreamDto?
        val streamsList: List<StreamDto>
        when (streams) {
            is Resource.Success -> {
                streamsList = (streams as Resource.Success<List<StreamDto>?>).data!!
            }
            else -> return
        }
        streamToUpdate = streamsList.firstOrNull { it.id == streamId }
        if (streamToUpdate == null) return
        val newList = streamsList.map {
            if (it.id == streamId) streamToUpdate.copy(
                topics = newTopics,
                isSelected = !streamToUpdate.isSelected
            ) else it
        }
        currStreams.emit(
            (streams as Resource.Success<List<StreamDto>?>).copy(
                data = newList
            )
        )
    }
}

package com.example.tinkoff_chat_app.domain.repository.streams_repository

import com.example.tinkoff_chat_app.data.streams.StreamDao
import com.example.tinkoff_chat_app.models.data_transfer_models.StreamDto
import com.example.tinkoff_chat_app.models.data_transfer_models.TopicDto
import com.example.tinkoff_chat_app.models.network_models.events_queue.streams.StreamScreenEvent
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.utils.RealTimeEvents
import com.example.tinkoff_chat_app.utils.Resource
import com.example.tinkoff_chat_app.utils.StreamMappers.toDatabaseStreamModel
import com.example.tinkoff_chat_app.utils.StreamMappers.toDatabaseSubscriptionModel
import com.example.tinkoff_chat_app.utils.StreamMappers.toStreamDtoList
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.HttpException
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
        onError: (String) -> Unit
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
                val networkData =
                    chatApi.getStreams().body()!!.streams.toStreamDtoList().toMutableList()
                streamDao.deleteAllStreams()
                streamDao.addStreams(
                    networkData.map { it.toDatabaseStreamModel() }
                )
                currStreams.emit(
                    Resource.Success(
                        data = networkData
                    )
                )
            } catch (e: HttpException) {
                onError("An issue occurred with internet connection.")
                currStreams.emit(
                    Resource.Success(
                        data = data
                    )
                )
            } catch (e: Exception) {
                onError("An issue occurred.")
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
        onError: (String) -> Unit
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
                val networkData =
                    chatApi.getSubscriptions().body()!!.subscriptions.toStreamDtoList()
                        .toMutableList()
                streamDao.deleteAllSubscriptions()
                streamDao.addSubscriptions(
                    networkData.map { it.toDatabaseSubscriptionModel() }
                )
                currStreams.emit(
                    Resource.Success(
                        data = networkData
                    )
                )
            } catch (e: HttpException) {
                onError("An issue occurred with internet connection.")
                currStreams.emit(
                    Resource.Success(
                        data = data
                    )
                )
            } catch (e: Exception) {
                onError("An issue occurred.")
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
        streamDao.addStream(
            streamToUpdate.copy(
                isSelected = !streamToUpdate.isSelected
            ).toDatabaseStreamModel()
        )
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

    override suspend fun subscribeForStream(
        streamName: String,
        description: String?,
        announce: Boolean
    ) {
        val subscriptions = if (description == null)
            "[{\"name\": \"$streamName\"}]"
        else
            "[{\"name\": \"$streamName\", \"description\": \"$description\"}]"
        chatApi.subscribeForStream(
            subscriptions = subscriptions,
            announce = announce
        )
    }

    override suspend fun registerQueue(
        type: String
    ): Map<String, String> {
        val narrow = null
        val eventType = "[\"$type\"]"
        val events = chatApi.registerQueue(narrow, eventType).body()
        return mapOf(
            RealTimeEvents.QUEUE_ID_KEY to events!!.queueId,
            RealTimeEvents.LAST_EVENT_ID_KEY to events.lastEventId.toString()
        )
    }

    override suspend fun getEventsFromQueue(queue: Map<String, String>): String {
        val queueId = queue[RealTimeEvents.QUEUE_ID_KEY]!!
        val lastEventId = queue[RealTimeEvents.LAST_EVENT_ID_KEY]!!
        val events =
            chatApi.getStreamEvents(queueId = queueId, lastEventId = lastEventId)
                .body()?.messageScreenEvents

        if (events != null) {
            for (event in events) {
                when (event.type) {
                    "subscription" -> {
                        val newStream = event.subscription!!.toStreamDtoList()
                        val cStreams =
                            (currStreams.value as Resource.Success<List<StreamDto>?>).data!!.toMutableList()
                        cStreams.addAll(newStream)
                        val newStreams = (currStreams.value as Resource.Success).copy(
                            cStreams
                        )
                        currStreams.value = newStreams
                    }
                    "stream" -> {
                        val newStream = event.stream!!.toStreamDtoList()
                        val cStreams =
                            (currStreams.value as Resource.Success<List<StreamDto>?>).data!!.toMutableList()
                        cStreams.addAll(newStream)
                        val newStreams = (currStreams.value as Resource.Success).copy(
                            cStreams
                        )
                        currStreams.value = newStreams
                    }
                    "topic" -> {}
                    else -> throw IllegalStateException()
                }
            }
        }
        return (events?.last() as StreamScreenEvent).id.toString()
    }
}

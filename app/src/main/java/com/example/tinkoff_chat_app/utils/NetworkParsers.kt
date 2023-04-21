package com.example.tinkoff_chat_app.utils

import com.example.tinkoff_chat_app.models.stream_screen_models.TopicModel
import com.example.tinkoff_chat_app.network.network_models.topics.Topic

fun List<Topic>.toTopicList(
    streamId: String,
    streamName: String
): List<TopicModel> = map {
    TopicModel(
        name = it.name,
        msgCount = 0,
        parentId = streamId,
        id = streamId + it.name,
        parentName = streamName
    )
}

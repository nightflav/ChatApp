package com.example.homework_2.utils

import com.example.homework_2.models.stream_screen_models.TopicModel
import com.example.homework_2.network.network_models.topics.Topic

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

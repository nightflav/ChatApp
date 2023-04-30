package com.example.tinkoff_chat_app.utils

import com.example.tinkoff_chat_app.models.data_transfer_models.MessageDto
import com.example.tinkoff_chat_app.models.data_transfer_models.ReactionDto
import com.example.tinkoff_chat_app.models.data_transfer_models.StreamDto
import com.example.tinkoff_chat_app.models.data_transfer_models.TopicDto
import com.example.tinkoff_chat_app.models.db_models.DatabaseMessageModel
import com.example.tinkoff_chat_app.models.db_models.DatabaseStreamModel
import com.example.tinkoff_chat_app.models.db_models.DatabaseSubscriptionModel
import com.example.tinkoff_chat_app.models.db_models.DatabaseTopicModel
import com.example.tinkoff_chat_app.models.network_models.messages.Message
import com.example.tinkoff_chat_app.models.network_models.messages.Reaction
import com.example.tinkoff_chat_app.models.network_models.streams.Stream
import com.example.tinkoff_chat_app.models.network_models.subscriptions.Subscription
import com.example.tinkoff_chat_app.models.network_models.topics.Topic
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.StreamModel
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.TopicModel
import com.example.tinkoff_chat_app.utils.Network.MISSING_AVATAR_URL
import com.example.tinkoff_chat_app.utils.TopicMappers.toTopicDto
import com.example.tinkoff_chat_app.utils.TopicMappers.toTopicModel

object MessagesMappers {

    @JvmName("toMessageDtoListFromNetworkModel")
    fun List<Message>.toMessageDtoList() = map {
        it.toMessageDto()
    }

    @JvmName("toMessageDtoListFromDBModel")
    fun List<DatabaseMessageModel>.toMessageDtoList() = map {
        it.toMessageDto()
    }

    fun List<MessageDto>.toDatabaseMessageModelList(streamName: String, topicName: String) = map {
        it.toDatabaseMessageModel(streamName, topicName)
    }

    private fun MessageDto.toDatabaseMessageModel(streamName: String, topicName: String) = DatabaseMessageModel(
        id = messageId,
        senderName = senderName,
        senderId = senderId,
        msg = msg,
        reactions = reactions,
        date = date,
        streamName = streamName,
        topicName = topicName,
        avatarUri = avatarUri
    )

    private fun Message.toMessageDto() = MessageDto(
        messageId = id,
        senderName = senderFullName,
        senderId = senderId,
        msg = content,
        reactions = reactions.map { it.toReactionDto() },
        date = timestamp,
        avatarUri = avatarUrl ?: MISSING_AVATAR_URL
    )

    private fun Reaction.toReactionDto() = ReactionDto(
        emojiCode = emojiCode, emojiName = emojiName, userId = userId
    )

    private fun DatabaseMessageModel.toMessageDto() = MessageDto(
        messageId = id,
        senderName = senderName,
        senderId = senderId,
        msg = msg,
        reactions = reactions,
        date = date,
        avatarUri = avatarUri
    )

}

object StreamMappers {
    private fun DatabaseStreamModel.toStreamDto() = StreamDto(
        id = id,
        name = name,
        isSelected = isSelected,
        isSubscriptionStream = isSubscriptionStream,
        topics = topics?.map { it.toTopicDto() }
    )

    private fun DatabaseSubscriptionModel.toStreamDto() = StreamDto(
        id = id,
        name = name,
        isSelected = isSelected,
        isSubscriptionStream = isSubscriptionStream,
        topics = topics?.map { it.toTopicDto() }
    )

    fun StreamDto.toDatabaseStreamModel() = DatabaseStreamModel(
        id = id,
        name = name,
        isSelected = isSelected,
        isSubscriptionStream = isSubscriptionStream,
        topics = topics?.map { it.toTopicModel() }
    )

    fun StreamDto.toDatabaseSubscriptionModel() = DatabaseSubscriptionModel(
        id = id,
        name = name,
        isSelected = isSelected,
        isSubscriptionStream = isSubscriptionStream,
        topics = topics?.map { it.toTopicModel() }
    )

    @JvmName("toStreamDtoListFromDBModel")
    fun List<DatabaseStreamModel>.toStreamDtoList() = map {
        it.toStreamDto()
    }

    @JvmName("toStreamDtoListFromDBModelSubscriptions")
    fun List<DatabaseSubscriptionModel>.toStreamDtoList() = map {
        it.toStreamDto()
    }

    private fun Stream.toStreamDto() = StreamDto(
        id = streamId,
        name = name,
        isSelected = false,
        isSubscriptionStream = isSubscriptionStream,
        topics = null
    )

    @JvmName("toStreamDtoListFromNetworkModel")
    fun List<Stream>.toStreamDtoList() = map {
        it.toStreamDto()
    }

    private fun Subscription.toStreamDto() = StreamDto(
        id = streamId,
        name = name,
        isSelected = false,
        isSubscriptionStream = isSubscriptionStream,
        topics = null
    )

    @JvmName("toStreamDtoListFromNetworkSubscriptionModel")
    fun List<Subscription>.toStreamDtoList() = map {
        it.toStreamDto()
    }

    private fun StreamDto.toStreamModel() = StreamModel(
        name = name,
        id = id,
        isSelected = isSelected,
        topics = topics?.map { it.toTopicModel() }
    )

    fun List<StreamDto>.toStreamModelList() = map {
        it.toStreamModel()
    }
}

object TopicMappers {
    fun TopicDto.toTopicModel() = TopicModel(
        id = id,
        parentName = parentName,
        parentId = parentId,
        name = name,
        msgCount = msgCount,
    )

    fun TopicModel.toTopicDto() = TopicDto(
        id = id,
        parentName = parentName,
        parentId = parentId,
        name = name,
        msgCount = msgCount,
        isLoading = isLoading
    )

    fun Topic.toTopicDto(streamId: Int, streamName: String) = TopicDto(
        name = name,
        msgCount = 0,
        parentId = streamId,
        id = streamId.toString() + name,
        parentName = streamName,
        isLoading = false
    )

    fun List<Topic>.toTopicDtoList(streamId: Int, streamName: String) = map {
        it.toTopicDto(streamId, streamName)
    }

    fun DatabaseTopicModel.toTopicDto() = TopicDto(
        id = id,
        parentName = parentName,
        parentId = parentId,
        msgCount = msgCount,
        name = name,
        isLoading = false
    )

    fun TopicDto.toDatabaseTopicModel() = DatabaseTopicModel(
        id = id,
        parentName = parentName,
        parentId = parentId,
        msgCount = msgCount,
        name = name
    )

    fun List<DatabaseTopicModel>.toTopicDtoList() = map {
        it.toTopicDto()
    }

}
package com.example.tinkoff_chat_app.screens.message

import com.example.tinkoff_chat_app.models.ui_models.MessageReaction

sealed class MessagesIntents {
    data class InitMessagesIntent(
        val streamName: String,
        val topicName: String?,
        val streamId: Int,
        val allTopics: Boolean
    ) : MessagesIntents()

    data class SendMessageIntent(val topic: String, val content: String, val onError: () -> Unit) :
        MessagesIntents()

    data class ChangeReactionStateIntent(val reaction: MessageReaction, val msgId: Int) :
        MessagesIntents()

    data class LoadMessagesIntent(val amount: Int, val lastMsgId: Int?) : MessagesIntents()
    data class DeleteMessageIntent(val msgId: Int) : MessagesIntents()
    data class EditMessageIntent(
        val newMessageContent: String,
        val msgId: Int,
        val onError: (String) -> Unit
    ) : MessagesIntents()

    data class ChangeMessageTopicIntent(
        val msgId: Int,
        val newTopicName: String,
        val onError: (String) -> Unit
    ) : MessagesIntents()

    data class UploadFileIntent(
        val topic: String,
        val file: ByteArray,
        val fileName: String,
        val onError: (String) -> Unit
    ) : MessagesIntents()
}

package com.example.tinkoff_chat_app.screens.message

import com.example.tinkoff_chat_app.models.ui_models.MessageReaction
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.StreamModel

sealed class MessagesIntents {
    data class InitMessagesIntent(
        val stream: StreamModel,
        val topicName: String?,
        val allTopics: Boolean
    ) : MessagesIntents()

    data class SendMessageIntent(val topic: String, val content: String, val onError: () -> Unit) :
        MessagesIntents()

    data class ChangeReactionStateIntent(val reaction: MessageReaction, val msgId: Int, val onError: (String) -> Unit) :
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

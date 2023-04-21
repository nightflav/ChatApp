package com.example.tinkoff_chat_app.screens.message

import com.example.tinkoff_chat_app.models.MessageReaction

sealed class MessagesIntents {
    data class UpdateMessagesIntent(val streamName: String, val topicName: String) : MessagesIntents()
    data class InitMessagesIntent(val streamName: String, val topicName: String) : MessagesIntents()
    data class SendMessageIntent(val content: String, val streamId: String, val topicName: String) : MessagesIntents()
    data class ChangeReactionStateIntent(val reaction: MessageReaction, val msgId: String) : MessagesIntents()
}

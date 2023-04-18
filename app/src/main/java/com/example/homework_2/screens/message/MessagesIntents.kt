package com.example.homework_2.screens.message

import com.example.homework_2.models.MessageReaction

sealed class MessagesIntents {
    data class UpdateMessagesIntent(val streamName: String, val topicName: String) : MessagesIntents()
    data class InitMessagesIntent(val streamName: String, val topicName: String) : MessagesIntents()
    data class SendMessageIntent(val content: String) : MessagesIntents()
    data class ChangeReactionStateIntent(val reaction: MessageReaction, val msgId: String) : MessagesIntents()
}

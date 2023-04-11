package com.example.homework_2.screens.message

import com.example.homework_2.models.MessageReaction

sealed class MessagesIntents {
    object UpdateMessagesIntent : MessagesIntents()
    object InitMessagesIntent : MessagesIntents()
    data class SendMessageIntent(val content: String) : MessagesIntents()
    data class ChangeReactionStateIntent(val reaction: MessageReaction, val msgId: String) : MessagesIntents()
}

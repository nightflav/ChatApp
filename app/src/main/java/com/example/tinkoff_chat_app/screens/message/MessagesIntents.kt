package com.example.tinkoff_chat_app.screens.message

import com.example.tinkoff_chat_app.models.MessageReaction

sealed class MessagesIntents {
    object UpdateMessagesIntent : MessagesIntents()
    data class InitMessagesIntent(val streamName: String, val topicName: String, val streamId: String) : MessagesIntents()
    data class SendMessageIntent(val content: String) : MessagesIntents()
    data class ChangeReactionStateIntent(val reaction: MessageReaction, val msgId: String) : MessagesIntents()
    data class LoadMessagesIntent(val amount: Int, val lastMsgId: Int?) : MessagesIntents()
    object SaveMessagesIntent : MessagesIntents()
}

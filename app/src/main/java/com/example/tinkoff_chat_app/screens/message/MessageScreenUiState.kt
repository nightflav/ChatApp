package com.example.tinkoff_chat_app.screens.message

import com.example.tinkoff_chat_app.models.ui_models.MessageModel

data class MessageScreenUiState(
    val topic: String? = null,
    val stream: String? = null,
    val streamId: String? = null,
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val messages: List<MessageModel>? = null,
    val isNewMessagesLoading: Boolean = false,
    val allMessagesLoaded: Boolean = false,
    val allTopics: Boolean = false
)
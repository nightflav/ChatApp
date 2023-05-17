package com.example.tinkoff_chat_app.screens.message

import com.example.tinkoff_chat_app.models.ui_models.MessageModel
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.StreamModel

data class MessageScreenUiState(
    val topic: String? = null,
    val stream: StreamModel? = null,
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val messages: List<MessageModel>? = null,
    val isNewMessagesLoading: Boolean = false,
    val allMessagesLoaded: Boolean = false,
    val allTopics: Boolean = false
)
package com.example.tinkoff_chat_app.screens.stream

import com.example.tinkoff_chat_app.models.stream_screen_models.StreamScreenItem

data class StreamScreenUiState(
    val isStreamsLoading: Boolean = true,
    val streams: List<StreamScreenItem>? = null,
    val error: Throwable? = null,
    val showSubs: Boolean = true,
    val request: String = ""
)
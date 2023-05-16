package com.example.tinkoff_chat_app.screens.stream

import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.StreamModel

sealed class StreamIntents {
    object InitStreamsIntent : StreamIntents()

    data class SearchForStreamIntent(val request: String, val onError: () -> Unit) : StreamIntents()

    data class ShowCurrentStreamsIntent(val showSubscribed: Boolean, val onError: () -> Unit) :
        StreamIntents()

    data class ShowCurrentStreamTopicsIntent(val stream: StreamModel, val onError: () -> Unit) :
        StreamIntents()

    data class CreateNewStreamIntent(
        val name: String,
        val description: String?,
        val announce: Boolean,
        val onError: () -> Unit
    ) : StreamIntents()
}
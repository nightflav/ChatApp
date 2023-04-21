package com.example.tinkoff_chat_app.screens.stream

import com.example.tinkoff_chat_app.models.stream_screen_models.StreamModel

sealed class StreamIntents {
    data class SearchForStreamIntent(val request: String) : StreamIntents()
    data class ShowCurrentStreamsIntent(val showSubscribed: Boolean): StreamIntents()
    data class UpdateStreamSelectedStateIntent(val stream: StreamModel) : StreamIntents()
}

package com.example.homework_2.screens.stream

import com.example.homework_2.models.stream_screen_models.StreamModel

sealed class StreamIntents {
    data class SearchForStreamIntent(val request: String) : StreamIntents()
    data class ShowCurrentStreamsIntent(val showSubscribed: Boolean): StreamIntents()
    data class UpdateStreamSelectedStateIntent(val stream: StreamModel) : StreamIntents()
}

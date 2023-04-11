package com.example.homework_2.screens.stream

import com.example.homework_2.models.streamScreenModels.StreamModel

sealed class StreamIntents {
    object InitStreams : StreamIntents()
    data class SearchForStreamIntent(val request: String, val showSubscribed: Boolean) : StreamIntents()
    data class ChangeSubscribedStreamsState(val showSubscribed: Boolean): StreamIntents()
    data class UpdateStreamSelectedState(val stream: StreamModel, val showSubscribed: Boolean) : StreamIntents()
}

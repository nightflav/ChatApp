package com.example.homework_2.screens.stream

import com.example.homework_2.models.streamScreenModels.StreamScreenItem

sealed class StreamScreenState {
    object Error : StreamScreenState()
    object Loading : StreamScreenState()
    object Init : StreamScreenState()
    class Success(val streams: List<StreamScreenItem>, val showSubscribed: Boolean) : StreamScreenState()
}

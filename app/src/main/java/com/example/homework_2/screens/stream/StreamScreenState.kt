package com.example.homework_2.screens.stream

sealed class StreamScreenState {
    object Error : StreamScreenState()
    object Loading : StreamScreenState()
    object Init : StreamScreenState()
    class Data(val streams: List<Any>) : StreamScreenState()
}

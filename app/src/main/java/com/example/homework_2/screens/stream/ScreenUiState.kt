package com.example.homework_2.screens.stream

import com.example.homework_2.models.stream_screen_models.StreamScreenItem

data class ScreenUiState(
    val isLoading: Boolean = true,
    val streams: List<StreamScreenItem>? = null,
    val error: Throwable? = null,
    val showSubs: Boolean = true,
    val request: String = ""
)
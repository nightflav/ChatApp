package com.example.homework_2.models.stream_screen_models

data class StreamModel  (
    val name: String = "",
    var isSelected: Boolean = false,
    val topics: List<TopicModel> = emptyList(),
    val id: String
): StreamScreenItem()
package com.example.homework_2.models.streamScreenModels

data class StreamModel  (
    val name: String = "",
    var isSelected: Boolean = false,
    val topics: List<TopicModel> = emptyList(),
    val id: String
): StreamScreenItem()
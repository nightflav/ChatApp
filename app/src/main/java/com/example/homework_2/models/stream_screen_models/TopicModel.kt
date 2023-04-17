package com.example.homework_2.models.stream_screen_models

data class TopicModel(
    val name: String = "",
    var msgCount: Int = 0,
    val parentId: String,
    val id: String,
    val parentName: String
) : StreamScreenItem()
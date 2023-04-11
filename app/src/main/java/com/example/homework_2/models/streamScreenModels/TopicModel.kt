package com.example.homework_2.models.streamScreenModels

data class TopicModel(
    val name: String = "",
    var msgCount: Int = 0,
    val parentId: String,
    val id: String,
    val parentName: String
) : StreamScreenItem()
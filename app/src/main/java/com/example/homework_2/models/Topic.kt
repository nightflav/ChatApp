package com.example.homework_2.models

data class Topic(
    val name: String = "",
    var msgCount: Int = 0,
    val parentId: String,
    val id: String,
    val parentName: String
)

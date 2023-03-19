package com.example.homework_2.models

data class Topic(
    val name: String = "",
    val msgCount: Int = 0,
    val parentId: String,
    val id: String
)

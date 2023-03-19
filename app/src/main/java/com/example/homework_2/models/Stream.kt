package com.example.homework_2.models

data class Stream(
    val name: String = "",
    var isSelected: Boolean = false,
    val topics: List<Topic> = emptyList(),
    val id: String,
    val isSubscribed: Boolean = false
)
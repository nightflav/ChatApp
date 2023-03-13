package com.example.homework_2.models

import com.example.homework_2.Datasource

data class Reaction(
    val reaction: Datasource.EmojiNCS,
    var count: Int,
    var isSelected: Boolean
)
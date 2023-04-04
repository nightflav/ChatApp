package com.example.homework_2.models

import com.example.homework_2.datasource.MessageDatasource

data class MessageReaction(
    val reaction: MessageDatasource.EmojiNCS,
    var count: Int,
    var isSelected: Boolean
)
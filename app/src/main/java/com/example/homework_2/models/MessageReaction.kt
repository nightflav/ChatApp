package com.example.homework_2.models

import com.example.homework_2.utils.Emojis

data class MessageReaction(
    val reaction: Emojis.EmojiNCS,
    var count: Int,
    var isSelected: Boolean
)
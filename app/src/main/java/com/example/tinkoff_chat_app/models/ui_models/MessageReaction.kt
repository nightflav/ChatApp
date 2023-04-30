package com.example.tinkoff_chat_app.models.ui_models

import com.example.tinkoff_chat_app.utils.Emojis

data class MessageReaction(
    val reaction: Emojis.EmojiNCS,
    var count: Int,
    var isSelected: Boolean
)
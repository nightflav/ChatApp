package com.example.tinkoff_chat_app.network.network_models.messages

import com.squareup.moshi.Json

data class Reaction(
    @Json(name = "emoji_code")
    val emojiCode: String,
    @Json(name = "emoji_name")
    val emojiName: String,
    @Json(name = "user_id")
    val userId: Int
)
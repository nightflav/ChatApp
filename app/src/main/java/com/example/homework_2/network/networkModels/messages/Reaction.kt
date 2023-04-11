package com.example.homework_2.network.networkModels.messages

import com.squareup.moshi.Json

data class Reaction(
    @Json(name = "emoji_code")
    val emojiCode: String,
    @Json(name = "emoji_name")
    val emojiName: String,
    @Json(name = "user_id")
    val userId: Int
)
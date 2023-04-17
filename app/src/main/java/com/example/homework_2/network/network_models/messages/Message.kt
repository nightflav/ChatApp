package com.example.homework_2.network.network_models.messages

import com.squareup.moshi.Json

data class Message(
    @Json(name = "avatar_url")
    val avatarUrl: String?,
    @Json(name = "content")
    val content: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "reactions")
    val reactions: List<Reaction>,
    @Json(name = "sender_id")
    val senderId: Int,
    @Json(name = "stream_id")
    val streamId: Int,
    @Json(name = "timestamp")
    val timestamp: Int,
    @Json(name = "sender_full_name")
    val senderFullName: String
)
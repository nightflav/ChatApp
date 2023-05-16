package com.example.tinkoff_chat_app.models.network_models.events_queue.messages

import com.example.tinkoff_chat_app.models.network_models.messages.Message
import com.squareup.moshi.Json

data class MessageScreenEvent(
    @Json(name = "id")
    val id: Int,
    @Json(name = "emoji_code")
    val emojiCode: String?,
    @Json(name = "emoji_name")
    val emojiName: String?,
    @Json(name = "user_id")
    val userId: Int?,
    @Json(name = "message_id")
    val messageId: Int?,
    @Json(name = "message")
    val message: Message?,
    @Json(name = "type")
    val type: String
)
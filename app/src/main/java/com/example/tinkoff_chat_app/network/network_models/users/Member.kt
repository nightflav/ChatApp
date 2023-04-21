package com.example.tinkoff_chat_app.network.network_models.users

import com.squareup.moshi.Json

data class Member(
    @Json(name = "avatar_url")
    val avatarUrl: String?,
    @Json(name = "email")
    val email: String,
    @Json(name = "full_name")
    val fullName: String,
    @Json(name = "user_id")
    val userId: Int,
    @Json(name = "is_bot")
    val isBot: Boolean?,
    @Json(name = "is_active")
    val isActive: Boolean
)
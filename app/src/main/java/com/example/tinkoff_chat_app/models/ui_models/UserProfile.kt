package com.example.tinkoff_chat_app.models.ui_models

data class UserProfile(
    val id: Int,
    val fullName: String,
    val status: String?,
    val avatarSource: String = "",
    val email: String = ""
)

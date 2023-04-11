package com.example.homework_2.models

data class UserProfile(
    val id: Int,
    val fullName: String,
    val status: String,
    val avatarSource: String = "",
    val email: String = ""
)

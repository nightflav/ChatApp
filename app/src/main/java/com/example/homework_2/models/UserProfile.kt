package com.example.homework_2.models

import androidx.annotation.DrawableRes

data class UserProfile(
    val fullName: String,
    val isActive: Boolean,
    val avatarSource: String = "",
    @DrawableRes var tmpProfilePhoto: Int?,
    val meetingStatus: String,
    val email: String = ""
)

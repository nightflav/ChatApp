package com.example.homework_2.models

import android.graphics.drawable.Drawable

data class UserProfile(
    val fullName: String,
    val isActive: Boolean,
    val avatarSource: String = "",
    var tmpProfilePhoto: Drawable?,
    val meetingStatus: String,
    val email: String = ""
)

package com.example.homework_2.models

import androidx.annotation.DrawableRes
import com.example.homework_2.R

data class UserProfile(
    val fullName: String,
    val status: String,
    val avatarSource: String = "",
    @DrawableRes var tmpProfilePhoto: Int? = R.drawable.ic_launcher_foreground,
    val email: String = ""
)

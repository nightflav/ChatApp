package com.example.homework_2.network.networkModels.users.presence

data class UserPresenceResponse(
    val msg: String,
    val presence: Presence,
    val result: String
)
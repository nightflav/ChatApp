package com.example.tinkoff_chat_app.network.network_models.users

import com.squareup.moshi.Json

data class UsersResponse(
    @Json(name = "members")
    val members: List<Member>,
    @Json(name = "msg")
    val msg: String,
    @Json(name = "result")
    val result: String
)
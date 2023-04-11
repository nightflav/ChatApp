package com.example.homework_2.network.networkModels.users

import com.squareup.moshi.Json

data class UsersResponse(
    @Json(name = "members")
    val members: List<Member>,
    @Json(name = "msg")
    val msg: String,
    @Json(name = "result")
    val result: String
)
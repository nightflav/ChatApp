package com.example.homework_2.network.networkModels.users

data class UsersResponse(
    val members: List<Member>,
    val msg: String,
    val result: String
)
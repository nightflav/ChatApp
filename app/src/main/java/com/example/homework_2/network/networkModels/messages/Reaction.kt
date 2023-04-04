package com.example.homework_2.network.networkModels.messages

data class Reaction(
    val emoji_code: String,
    val emoji_name: String,
    val reaction_type: String,
    val user: User,
    val user_id: Int
)
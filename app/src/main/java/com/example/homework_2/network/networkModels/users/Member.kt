package com.example.homework_2.network.networkModels.users

data class Member(
    val avatar_url: String?,
    val avatar_version: Int,
    val bot_owner_id: Int?,
    val bot_type: Int?,
    val date_joined: String,
    val delivery_email: String?,
    val email: String,
    val full_name: String,
    val is_active: Boolean,
    val is_admin: Boolean,
    val is_billing_admin: Boolean,
    val is_bot: Boolean?,
    val is_guest: Boolean,
    val is_owner: Boolean,
    val role: Int,
    val timezone: String,
    val user_id: Int
)
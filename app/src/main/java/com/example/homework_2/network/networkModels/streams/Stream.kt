package com.example.homework_2.network.networkModels.streams

data class Stream(
    val can_remove_subscribers_group_id: Int?,
    val date_created: Int?,
    val description: String?,
    val first_message_id: Int?,
    val history_public_to_subscribers: Boolean?,
    val invite_only: Boolean?,
    val is_announcement_only: Boolean?,
    val is_web_public: Boolean?,
    val message_retention_days: Int?,
    val name: String = "empty",
    val rendered_description: String?,
    val stream_id: Int = 0,
    val stream_post_policy: Int?
)
package com.example.homework_2.network.networkModels.subscriptions

data class Subscription(
    val audible_notifications: Boolean?,
    val can_remove_subscribers_group_id: Int,
    val color: String,
    val date_created: Int,
    val description: String,
    val desktop_notifications: Boolean?,
    val email_address: String,
    val email_notifications: Boolean?,
    val first_message_id: Int,
    val history_public_to_subscribers: Boolean,
    val in_home_view: Boolean,
    val invite_only: Boolean,
    val is_announcement_only: Boolean,
    val is_muted: Boolean,
    val is_web_public: Boolean,
    val message_retention_days: Int?,
    val name: String,
    val pin_to_top: Boolean,
    val push_notifications: Boolean?,
    val rendered_description: String,
    val stream_id: Int,
    val stream_post_policy: Int,
    val stream_weekly_traffic: Int?,
    val wildcard_mentions_notify: Boolean?
)
package com.example.homework_2.network.networkModels.messages

data class Message(
    val avatar_url: String?,
    val client: String,
    val content: String,
    val content_type: String,
    val display_recipient: String,
    val flags: List<String>,
    val id: Int,
    val is_me_message: Boolean,
    val reactions: List<Reaction>,
    val recipient_id: Int,
    val sender_email: String,
    val sender_full_name: String,
    val sender_id: Int,
    val sender_realm_str: String,
    val stream_id: Int,
    val subject: String,
    val submessages: List<Any>,
    val timestamp: Int,
    val topic_links: List<Any>,
    val type: String
)
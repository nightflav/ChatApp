package com.example.tinkoff_chat_app.utils

import com.example.tinkoff_chat_app.models.data_transfer_models.MessageDto
import com.example.tinkoff_chat_app.models.data_transfer_models.ReactionDto
import com.example.tinkoff_chat_app.models.db_models.DatabaseMessageModel

object Consts {
    val badMessageResponse = "{\n" +
            "  \"result\": \"error\",\n" +
            "  \"msg\": \"no data loaded\",\n" +
            "  \"code\": \"REQUEST_VARIABLE_INVALID\"\n" +
            "}"

    val messageResponse = "{\n" +
            "  \"result\": \"success\",\n" +
            "  \"msg\": \"\",\n" +
            "  \"messages\": [\n" +
            "    {\n" +
            "      \"id\": 354875665,\n" +
            "      \"sender_id\": 613790,\n" +
            "      \"content\": \"<p>another message</p>\",\n" +
            "      \"recipient_id\": 1054258,\n" +
            "      \"timestamp\": 1682947277,\n" +
            "      \"client\": \"website\",\n" +
            "      \"subject\": \"justNewTitle\",\n" +
            "      \"topic_links\": [],\n" +
            "      \"is_me_message\": false,\n" +
            "      \"reactions\": [],\n" +
            "      \"submessages\": [],\n" +
            "      \"flags\": [\n" +
            "        \"read\",\n" +
            "        \"historical\"\n" +
            "      ],\n" +
            "      \"sender_full_name\": \"Georgiy Merkulov\",\n" +
            "      \"sender_email\": \"user613790@tinkoff-android-spring-2023.zulipchat.com\",\n" +
            "      \"sender_realm_str\": \"tinkoff-android-spring-2023\",\n" +
            "      \"display_recipient\": \"gegeeg\",\n" +
            "      \"type\": \"stream\",\n" +
            "      \"stream_id\": 382338,\n" +
            "      \"avatar_url\": \"https://secure.gravatar.com/avatar/d5e5c4187d4cbeac5ed626370f0eadb9?d=identicon&version=1\",\n" +
            "      \"content_type\": \"text/html\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"found_anchor\": false,\n" +
            "  \"found_oldest\": true,\n" +
            "  \"found_newest\": true,\n" +
            "  \"history_limited\": false,\n" +
            "  \"anchor\": 10000000000000000\n" +
            "}"

    val testMessage = MessageDto(
        messageId = 354875665,
        senderId = 613790,
        msg = "<p>another message</p>",
        reactions = emptyList(),
        avatarUri = "https://secure.gravatar.com/avatar/d5e5c4187d4cbeac5ed626370f0eadb9?d=identicon&version=1",
        date = 1682947277,
        senderName = "Georgiy Merkulov"
    )

    val testData = listOf(
        testMessage
    )

    val testDBData = listOf(
        DatabaseMessageModel(
            id = 354875665,
            senderId = 613790,
            msg = "<p>another message</p>",
            reactions = emptyList(),
            avatarUri = "https://secure.gravatar.com/avatar/d5e5c4187d4cbeac5ed626370f0eadb9?d=identicon&version=1",
            date = 1682947277,
            senderName = "Georgiy Merkulov",
            streamName = "anyStream",
            topicName = "anyTopic"
        )
    )

    val testEmoji = ReactionDto(
        emojiCode = "smiley_face_code",
        emojiName = "smiley_face",
        userId = -1
    )
}
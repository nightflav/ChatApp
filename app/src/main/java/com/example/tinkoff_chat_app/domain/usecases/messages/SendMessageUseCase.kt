package com.example.tinkoff_chat_app.domain.usecases.messages

import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val msgRepo: MessagesRepository
) {
    suspend operator fun invoke(
        topicName: String,
        content: String,
        streamId: String
    ) = msgRepo.sendMessage(
        topicName = topicName,
        content = content,
        streamId = streamId
    )
}
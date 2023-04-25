package com.example.tinkoff_chat_app.domain.usecases.messages

import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import javax.inject.Inject

class SaveMessagesUseCase @Inject constructor(
    private val msgRepo: MessagesRepository
) {
    suspend operator fun invoke(
        streamName: String,
        topicName: String,
        maxMessagesToSave: Int
    ) {
        msgRepo.saveMessages(
            topicName,
            streamName,
            maxMessagesToSave
        )
    }
}
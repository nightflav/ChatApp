package com.example.tinkoff_chat_app.domain.usecases.messages

import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import com.example.tinkoff_chat_app.models.ui_models.MessageReaction
import javax.inject.Inject

class ChangeReactionSelectedStateUseCase @Inject constructor(
    private val msgRepo: MessagesRepository
) {
    suspend operator fun invoke(
        reaction: MessageReaction, msgId: Int
    ) {
        if (reaction.isSelected) {
            reaction.isSelected = false
            msgRepo.removeReaction(
                msgId = msgId,
                emojiName = reaction.reaction.name
            )
        } else {
            reaction.isSelected = true
            msgRepo.sendReaction(
                msgId = msgId,
                emojiName = reaction.reaction.name
            )
        }
    }
}
package com.example.tinkoff_chat_app.domain.usecases.messages

import android.content.SharedPreferences
import androidx.core.text.HtmlCompat
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessageDto
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import com.example.tinkoff_chat_app.domain.repository.messages_repository.ReactionDto
import com.example.tinkoff_chat_app.models.MessageModel
import com.example.tinkoff_chat_app.models.MessageReaction
import com.example.tinkoff_chat_app.models.UserProfile
import com.example.tinkoff_chat_app.utils.Emojis
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_FULLNAME
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_ID
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_STATUS
import com.example.tinkoff_chat_app.utils.MessageTypes
import com.example.tinkoff_chat_app.utils.parseDate
import com.example.tinkoff_chat_app.utils.toDate
import javax.inject.Inject

class InitMessagesUseCase @Inject constructor(
    private val msgRepo: MessagesRepository,
    spProfile: SharedPreferences
) {
    private val user = UserProfile(
        fullName = spProfile.getString(SP_PROFILE_FULLNAME, "unknown")!!,
        id = spProfile.getInt(SP_PROFILE_ID, -1),
        status = spProfile.getString(SP_PROFILE_STATUS, "offline")
    )

    suspend operator fun invoke(
        streamName: String,
        topicName: String
    ): List<MessageModel>? {
        return msgRepo.getMessagesByTopicLocal(
            topicName = topicName, streamName = streamName
        ).toMessageModelList()?.addDateSeparators()
    }

    private fun List<MessageDto>?.toMessageModelList() = this?.map {
        MessageModel(
            senderName = it.senderName,
            msg = HtmlCompat.fromHtml(
                it.msg,
                HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
            ).toString(),
            reactions = it.reactions.toReactionList(),
            user_id = it.senderId.toString(),
            message_id = getMsgTypeId(it.senderId),
            date = it.date.toDate()
        )
    }

    private fun List<ReactionDto>.toReactionList(): MutableList<MessageReaction> {
        val resultReactions = mutableListOf<MessageReaction>()
        for (react in this) {
            val msgReaction = MessageReaction(
                reaction = Emojis.EmojiNCS(
                    name = react.emojiName,
                    code = react.emojiCode
                ),
                count = 1,
                isSelected = react.userId == user.id
            )
            if (msgReaction.reaction.name !in resultReactions.map { it.reaction.name })
                resultReactions.add(msgReaction)
            else {
                val reactInResult = resultReactions.first { it.reaction.name == react.emojiName }
                reactInResult.count++
                if (!reactInResult.isSelected)
                    reactInResult.isSelected = react.userId == user.id
            }
        }
        return resultReactions
    }

    private fun List<MessageModel>.addDateSeparators(): List<MessageModel> {
        if (this.isEmpty()) return emptyList()
        val resultMessages = mutableListOf<MessageModel>()
        var prevDate = this.first().date
        resultMessages.add(
            MessageModel(
                date = prevDate.parseDate(),
                isDataSeparator = true
            )
        )
        for (msg in this) {
            val thisMsgDate = msg.date
            if (thisMsgDate > prevDate) {
                resultMessages.add(
                    MessageModel(
                        date = thisMsgDate.parseDate(),
                        isDataSeparator = true
                    )
                )
                prevDate = thisMsgDate
            }
            resultMessages.add(msg)
        }
        return resultMessages
    }

    private fun getMsgTypeId(senderId: Int): String =
        when (user.id) {
            senderId -> MessageTypes.SENDER
            else -> MessageTypes.RECEIVER
        }
}
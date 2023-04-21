package com.example.tinkoff_chat_app.domain.usecases.messages

import androidx.core.text.HtmlCompat
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import com.example.tinkoff_chat_app.domain.repository.profile_repository.ProfileRepository
import com.example.tinkoff_chat_app.domain.repository.profile_repository.ProfileRepositoryImpl
import com.example.tinkoff_chat_app.domain.repository.streams_repository.StreamsRepository
import com.example.tinkoff_chat_app.domain.repository.streams_repository.StreamsRepositoryImpl
import com.example.tinkoff_chat_app.models.MessageReaction
import com.example.tinkoff_chat_app.models.SingleMessage
import com.example.tinkoff_chat_app.models.UserProfile
import com.example.tinkoff_chat_app.network.network_models.messages.Message
import com.example.tinkoff_chat_app.network.network_models.messages.Reaction
import com.example.tinkoff_chat_app.utils.Emojis
import com.example.tinkoff_chat_app.utils.MessageTypes
import com.example.tinkoff_chat_app.utils.parseDate
import com.example.tinkoff_chat_app.utils.toDate
import javax.inject.Inject

class LoadMessagesByTopicUseCase @Inject constructor(
    private val msgRepo: MessagesRepository,
    private val profileRepo: ProfileRepository,
    private val streamsRepo: StreamsRepository
) {

    suspend operator fun invoke(
        topicName: String,
        streamName: String
    ): List<SingleMessage> {
        val networkMessages =
            msgRepo.getMessagesByTopic(topicName, streamName).toSingleMessageList()

        (streamsRepo as StreamsRepositoryImpl).setTopicMsgCount(
            topicName = topicName,
            count = networkMessages.size,
            streamName = streamName
        )

        return networkMessages.addDateSeparators()
    }

    private suspend fun List<Message>.toSingleMessageList(): List<SingleMessage> {
        val resultMessages = mutableListOf<SingleMessage>()
        for (msg in this) {
            resultMessages.add(
                SingleMessage(
                    senderName = msg.senderFullName,
                    msg = HtmlCompat.fromHtml(
                        msg.content,
                        HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                    ).toString(),
                    reactions = msg.reactions.toReactionList(),
                    user_id = getMsgTypeId(msg.senderId),
                    message_id = msg.id.toString(),
                    date = msg.timestamp.toDate()
                )
            )
        }
        return resultMessages
    }

    private suspend fun getMsgTypeId(senderId: Int): String =
        when ((profileRepo as ProfileRepositoryImpl).getProfile().id) {
            senderId -> MessageTypes.SENDER
            else -> MessageTypes.RECEIVER
        }

    private suspend fun List<Reaction>.toReactionList(): MutableList<MessageReaction> {
        val user: UserProfile = (profileRepo as ProfileRepositoryImpl).getProfile()
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

    private fun List<SingleMessage>.addDateSeparators(): List<SingleMessage> {
        val resultMessages = mutableListOf<SingleMessage>()
        var prevDate = this.first().date
        resultMessages.add(
            SingleMessage(
                date = prevDate.parseDate(),
                isDataSeparator = true
            )
        )
        for (msg in this) {
            val thisMsgDate = msg.date
            if (thisMsgDate > prevDate) {
                resultMessages.add(
                    SingleMessage(
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
}
package com.example.homework_2.domain.repository.messages_repository

import android.util.Log
import androidx.core.text.HtmlCompat
import com.example.homework_2.models.MessageReaction
import com.example.homework_2.models.SingleMessage
import com.example.homework_2.models.UserProfile
import com.example.homework_2.network.ChatApi
import com.example.homework_2.network.narrow.NarrowItem
import com.example.homework_2.network.network_models.messages.Message
import com.example.homework_2.network.network_models.messages.Reaction
import com.example.homework_2.domain.repository.profile_repository.ProfileRepository
import com.example.homework_2.domain.repository.profile_repository.ProfileRepositoryImpl
import com.example.homework_2.screens.message.MessagesScreenState
import com.example.homework_2.utils.Emojis
import com.example.homework_2.utils.MessageTypes
import com.example.homework_2.utils.addDateSeparators
import com.example.homework_2.utils.toDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val profileRepo: ProfileRepository
) : MessagesRepository {

    private val messagesByTopic: MutableMap<String, List<SingleMessage>?> = mutableMapOf()

    override suspend fun getMessagesByTopic(
        topicName: String,
        streamName: String
    ): Flow<MessagesScreenState> = flow {
        emit(MessagesScreenState.Loading)
        if (messagesByTopic[topicName]?.isEmpty() == true || messagesByTopic[topicName] == null) {
            try {
                loadMessagesByTopic(topicName, streamName)
                emit(MessagesScreenState.Success(messagesByTopic[topicName]!!))
            } catch (e: Exception) {
                Log.d("TAGTAGTAG", "$e")
                emit(MessagesScreenState.Error)
            }
        } else {
            emit(MessagesScreenState.Success(messagesByTopic[topicName]!!))
        }
    }

    suspend fun loadMessagesByTopic(
        topicName: String,
        streamName: String
    ) {
        val narrow = listOf(
            NarrowItem(
                operand = streamName,
                operator = "stream"
            ),
            NarrowItem(
                operand = topicName,
                operator = "topic"
            )
        )
        val networkMessages = chatApi.getMessages(
            narrow = narrow.toString(),
            numBefore = 1000,
            numAfter = 0
        ).body()?.messages?.toSingleMessageList()

        messagesByTopic[topicName] = networkMessages.addDateSeparators()
    }

    override suspend fun sendMessage(
        topicName: String,
        content: String,
        streamId: String
    ): Flow<MessagesScreenState> = flow {
        try {
            chatApi.sendMessage(
                to = streamId,
                content = content,
                topic = topicName
            )
            emit(MessagesScreenState.Success(messagesByTopic[topicName]!!))
        } catch (e: Exception) {
            emit(MessagesScreenState.Error)
        }
    }

    override suspend fun sendReaction(
        msgId: String,
        emojiName: String
    ): Flow<MessagesScreenState> = flow {
        try {
            chatApi.sendReaction(
                msgId = msgId,
                emojiName = emojiName
            )
            emit(MessagesScreenState.Init)
        } catch (e: Exception) {
            emit(MessagesScreenState.Error)
        }
    }

    override suspend fun removeReaction(
        msgId: String,
        emojiName: String
    ): Flow<MessagesScreenState> = flow {
        try {
            chatApi.removeReaction(
                msgId = msgId,
                emojiName = emojiName
            )
            emit(MessagesScreenState.Init)
        } catch (e: Exception) {
            emit(MessagesScreenState.Error)
        }
    }

    private suspend fun getMsgTypeId(senderId: Int): String =
        when ((profileRepo as ProfileRepositoryImpl).getProfile().id) {
            senderId -> MessageTypes.SENDER
            else -> MessageTypes.RECEIVER
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
}
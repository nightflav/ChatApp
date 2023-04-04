package com.example.homework_2.datasource

import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
import com.example.homework_2.MessageTypes.RECEIVER
import com.example.homework_2.MessageTypes.SENDER
import com.example.homework_2.datasource.ProfilesDatasource.getProfile
import com.example.homework_2.datasource.StreamDatasource.setTopicMsgCount
import com.example.homework_2.emojiSetNCS
import com.example.homework_2.models.MessageReaction
import com.example.homework_2.models.SingleMessage
import com.example.homework_2.network.RetrofitInstance.Companion.chatApi
import com.example.homework_2.network.narrow.NarrowItem
import com.example.homework_2.network.networkModels.messages.Reaction
import java.util.*

object MessageDatasource {
    data class EmojiNCS(
        val name: String,
        val code: String
    ) {
        fun getCodeString() = String(Character.toChars(code.toInt(16)))
    }

    private val messagesByTopic: MutableMap<String, List<SingleMessage>> = mutableMapOf()

    suspend fun getMessagesByTopic(topicName: String, streamName: String): List<SingleMessage> =
        if (messagesByTopic[topicName]?.isEmpty() == true || messagesByTopic[topicName] == null) {
            loadMessagesByTopic(topicName, streamName)
            messagesByTopic[topicName]!!
        } else
            messagesByTopic[topicName]!!

    suspend fun loadMessagesByTopic(topicName: String, streamName: String) {
        val resultMessages = mutableListOf<SingleMessage>()

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

        val messagesNetwork = chatApi.getMessages(
            narrow = narrow.toString(),
            numBefore = 100,
            numAfter = 0
        )
        Log.d("testestest", "${messagesNetwork.body()?.messages}")

        if (messagesNetwork.isSuccessful)
            for (msg in messagesNetwork.body()!!.messages) {
                Log.d("testestest", "${msg.content} and ${msg.reactions}")
                resultMessages.add(
                    SingleMessage(
                        senderName = msg.sender_full_name,
                        msg = HtmlCompat.fromHtml(
                            msg.content,
                            FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                        ).toString(),
                        reactions = msg.reactions.toReactionList(),
                        user_id = getMsgTypeId(msg.sender_id),
                        message_id = msg.id.toString(),
                        date = msg.timestamp.toDate()
                    )
                )
            }

        setTopicMsgCount(topicName, resultMessages.size, streamName)
        messagesByTopic[topicName] = resultMessages
    }

    private suspend fun getMsgTypeId(senderId: Int): String {
        val user = getProfile().body()
        return when (user?.user_id) {
            senderId -> SENDER
            else -> RECEIVER
        }
    }

    fun getEmojis(): List<String> {
        return emojiSetNCS.map { it.getCodeString() }
    }

    suspend fun addMessage(topicName: String, msg: String, streamId: String) {
        chatApi.sendMessage(
            to = streamId,
            content = msg,
            topic = topicName
        )
    }

    fun getReactions(msgId: String, topicName: String): List<MessageReaction> {
        val message = messagesByTopic[topicName]?.first { it.message_id == msgId }
        Log.d("reactionAdd", "message is $message")
        Log.d("reactionAdd", "topicName is $topicName")
        Log.d("reactionAdd", "sgByTopic is ${messagesByTopic.keys} to ${messagesByTopic.values}")
        return message?.reactions ?: emptyList()
    }

    suspend fun sendReaction(msgId: String, emojiName: String) {
        chatApi.sendReaction(
            msgId = msgId,
            emojiName = emojiName
        )
    }

    suspend fun removeReaction(msgId: String, emojiName: String) {
        chatApi.removeReaction(
            msgId = msgId,
            emojiName = emojiName
        )
    }

}

private fun Int.toDate(): String = Date(this * 1000L).toString()

private suspend fun List<Reaction>.toReactionList(): MutableList<MessageReaction> {
    val user = getProfile().body()
    val resultReactions = mutableListOf<MessageReaction>()

    for (react in this) {
        val msgReaction = MessageReaction(
            reaction = MessageDatasource.EmojiNCS(
                name = react.emoji_name,
                code = react.emoji_code
            ),
            count = 1,
            isSelected = react.user_id == user?.user_id
        )
        if(msgReaction.reaction.name !in resultReactions.map { it.reaction.name })
            resultReactions.add(msgReaction)
        else {
            val reactInResult = resultReactions.first { it.reaction.name == react.emoji_name }
            reactInResult.count++
            if(!reactInResult.isSelected)
                reactInResult.isSelected = react.user_id == user?.user_id
        }
    }

    return resultReactions
}

package com.example.homework_2.datasource

import com.example.homework_2.emojiSetNCS
import com.example.homework_2.messages
import com.example.homework_2.models.Reaction
import com.example.homework_2.models.SingleMessage
import kotlinx.coroutines.delay

object MessageDatasource {

    data class EmojiNCS(
        val name: String,
        val code: String
    ) {
        fun getCodeString() = String(Character.toChars(code.toInt(16)))
    }

    suspend fun getMessages(topicId: String): List<SingleMessage> {
        delay(500L)
        return messages[topicId]!!
    }

    fun getEmojis(): List<String> {
        return emojiSetNCS.map { it.getCodeString() }
    }

    suspend fun addMessage(topicId: String, msg: SingleMessage) {
        delay(100L)
        messages[topicId]!!.add(msg)
    }

    fun changeReactionSelectedState(reaction: EmojiNCS, msgId: String, topicId: String) {
        val msg = getMessageByMessageId(msgId, topicId)
        val react = msg.reactions.firstOrNull { it.reaction == reaction }
        if (react != null) {
            react.isSelected = !react.isSelected
            if (react.isSelected)
                react.count++
            else
                react.count--
            if (react.count == 0)
                msg.reactions.remove(react)
        }
    }

    fun getReactions(msgId: String, topicId: String): MutableList<Reaction> =
        getMessageByMessageId(msgId, topicId).reactions.filter { it.count > 0 }.toMutableList()

    fun addReaction(reaction: Reaction, msgId: String, topicId: String) {
        val msg = getMessageByMessageId(msgId, topicId)
        if (reaction.reaction !in msg.reactions.map { it.reaction })
            msg.reactions.add(
                Reaction(
                    reaction = reaction.reaction,
                    count = 1,
                    isSelected = true
                )
            )
    }

    private fun getMessageByMessageId(msgId: String, topicId: String): SingleMessage {
        for (msg in messages[topicId]!!)
            if (msg.message_id == msgId)
                return msg
        return emptyMessage
    }

    private val emptyMessage = SingleMessage("", "", mutableListOf(), "", "")

}
package com.example.homework_2

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.example.homework_2.models.*


object Datasource {

    var showSubscribed = true

    private val streamsToShow
        get() = if (showSubscribed) editedSubscribedStreams else editedStreams

    data class EmojiNCS(
        val name: String,
        val code: String
    ) {
        fun getCodeString() = String(Character.toChars(code.toInt(16)))
    }

    fun getMessages(topicId: String): List<SingleMessage> {
        return messages[topicId]!!
    }

    fun getEmojis(): List<String> {
        return emojiSetNCS.map { it.getCodeString() }
    }

    fun getStreams() = streamsToShow

    fun getProfile(context: Context) =
        UserProfile(
            fullName = "Boris Godunov",
            isActive = false,
            tmpProfilePhoto = getProfilePhoto(context),
            meetingStatus = "In a meeting"
        )

    fun getContacts() = contacts

    fun addMessage(topicId: String, msg: SingleMessage) {
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

    fun changeStreamSelectedState(streamId: String) {
        val streamToChange = streams.first { it.id == streamId }
        streamToChange.isSelected = !streamToChange.isSelected
        if (streamToChange.isSelected)
            addTopics(
                streamToChange,
                streamsToShow
            )
        else
            deleteTopics(
                streamToChange,
                streamsToShow
            )
    }

    fun containsTopic(topicId: String) = messages.containsKey(topicId)

    private fun deleteTopics(streamToChange: Stream, from: MutableList<Any>) {
        val parentId = streamToChange.id
        val tmpStreams =
            from.filter { if (it is Topic) it.parentId != parentId else true }.toMutableList()
        from.clear()
        from.addAll(tmpStreams)
    }

    private fun addTopics(stream: Stream, from: MutableList<Any>) {
        val topicsToAdd = stream.topics
        val indexOfCurrTopic = from.indexOf(stream)
        from.addAll(indexOfCurrTopic + 1, topicsToAdd)
    }

    private fun getMessageByMessageId(msgId: String, topicId: String): SingleMessage {
        for (msg in messages[topicId]!!)
            if (msg.message_id == msgId)
                return msg
        return emptyMessage
    }

    private val emptyMessage = SingleMessage("", "", mutableListOf(), "", "")

    private val editedStreams: MutableList<Any> by lazy { streams.toMutableList() }

    private val editedSubscribedStreams: MutableList<Any> by lazy {
        streams.filter { it.isSubscribed }.toMutableList()
    }

    private fun getProfilePhoto(context: Context): Drawable =
        AppCompatResources.getDrawable(context, R.drawable.ic_launcher_foreground)!!

    fun renewStreamsOpenState() {
        for (stream in streams) {
            stream.isSelected = false
            deleteTopics(stream, streamsToShow)
        }
    }
}
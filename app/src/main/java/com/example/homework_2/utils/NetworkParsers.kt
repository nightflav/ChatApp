package com.example.homework_2.utils

import androidx.core.text.HtmlCompat
import com.example.homework_2.models.MessageReaction
import com.example.homework_2.models.SingleMessage
import com.example.homework_2.models.UserProfile
import com.example.homework_2.models.stream_screen_models.TopicModel
import com.example.homework_2.network.RetrofitInstance
import com.example.homework_2.network.network_models.messages.Message
import com.example.homework_2.network.network_models.messages.Reaction
import com.example.homework_2.network.network_models.users.Member
import com.example.homework_2.repository.messages_repository.MessageRepositoryImpl
import com.example.homework_2.repository.profile_repository.ProfileRepositoryImpl
import retrofit2.Response

suspend fun Response<Member>.toUserProfile(): UserProfile? {
    val member = body()
    return if (this.isSuccessful && member != null) {
        val currUserPresence = RetrofitInstance.chatApi.getUserPresence(member.email).body()
        UserProfile(
            fullName = member.fullName,
            status = currUserPresence!!.presence.aggregated.status,
            avatarSource = member.avatarUrl
                ?: "https://www.freeiconspng.com/thumbs/no-image-icon/no-image-icon-6.png",
            email = member.email,
            id = member.userId
        )
    } else null
}

suspend fun List<Reaction>.toReactionList(): MutableList<MessageReaction> {
    val user = ProfileRepositoryImpl().getProfile()
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

suspend fun List<Message>.toSingleMessageList(): List<SingleMessage> {
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
                user_id = MessageRepositoryImpl().getMsgTypeId(msg.senderId),
                message_id = msg.id.toString(),
                date = msg.timestamp.toDate()
            )
        )
    }
    return resultMessages
}

fun List<com.example.homework_2.network.network_models.topics.Topic>.toTopicList(
    streamId: String,
    streamName: String
): List<TopicModel> = map {
    TopicModel(
        name = it.name,
        msgCount = 0,
        parentId = streamId,
        id = streamId + it.name,
        parentName = streamName
    )
}

package com.example.tinkoff_chat_app.domain.usecases.messages

import android.content.SharedPreferences
import android.util.Log
import androidx.core.text.HtmlCompat
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import com.example.tinkoff_chat_app.domain.repository.profile_repository.ProfileRepository
import com.example.tinkoff_chat_app.domain.repository.profile_repository.ProfileRepositoryImpl
import com.example.tinkoff_chat_app.models.data_transfer_models.MessageDto
import com.example.tinkoff_chat_app.models.data_transfer_models.ReactionDto
import com.example.tinkoff_chat_app.models.ui_models.MessageModel
import com.example.tinkoff_chat_app.models.ui_models.MessageReaction
import com.example.tinkoff_chat_app.models.ui_models.UserProfile
import com.example.tinkoff_chat_app.utils.*
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_FULLNAME
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_ID
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_STATUS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.first
import kotlin.collections.map
import kotlin.collections.mutableListOf

class SubscribeForMessagesUseCase @Inject constructor(
    private val messagesRepo: MessagesRepository,
    spProfile: SharedPreferences
) {
    private val user = UserProfile(
        fullName = spProfile.getString(SP_PROFILE_FULLNAME, "unknown")!!,
        id = spProfile.getInt(SP_PROFILE_ID, -1),
        status = spProfile.getString(SP_PROFILE_STATUS, "offline")
    )

    operator fun invoke(
    ): Flow<Resource<List<MessageModel>>> {
        Log.d("TAGTAGTAG", "$user")
        return messagesRepo.currentMessages.map {
            when(it) {
                is Resource.Error -> {
                    Resource.Error(it.error)
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Success -> {
                    Resource.Success(
                        data = it.data.toMessageModelList()!!
                            .addDateSeparators()
                            .transformTextToEmojis()
                    )
                }
            }
        }
    }

    private fun List<MessageDto>?.toMessageModelList() = this?.map {
        MessageModel(
            senderName = it.senderName,
            msg = HtmlCompat.fromHtml(
                it.msg,
                HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
            ).toString(),
            reactions = it.reactions.toReactionList(),
            user_id = getMsgTypeId(it.senderId),
            message_id = it.messageId.toString(),
            date = it.date.toDate(),
            avatarUri = it.avatarUri
        )
    }

    private fun getMsgTypeId(senderId: Int): String =
        when (user.id) {
            senderId -> MessageTypes.SENDER
            else -> MessageTypes.RECEIVER
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
        if(this.isEmpty()) return this
        val resultMessages = mutableListOf<MessageModel>()
        var prevDate = this.first().date
        resultMessages.add(
            MessageModel(
                date = prevDate.parseDate(),
                isDataSeparator = true,
                avatarUri = this.first().avatarUri
            )
        )
        for (msg in this) {
            val thisMsgDate = msg.date
            if (thisMsgDate > prevDate) {
                resultMessages.add(
                    MessageModel(
                        date = thisMsgDate.parseDate(),
                        isDataSeparator = true,
                        avatarUri = msg.avatarUri
                    )
                )
                prevDate = thisMsgDate
            }
            resultMessages.add(msg)
        }
        return resultMessages
    }

    private fun List<MessageModel>.transformTextToEmojis() = this.map {
        it.copy(
            msg = it.msg.replaceWithEmojis()
        )
    }

    private fun String.replaceWithEmojis(): String {
        val regex = Regex(pattern = ":[A-Za-z_]+:")
        val newMessage = this.replace(regex) {
            val emojiName = it.value.removePrefix(":").removeSuffix(":")
            if (emojiName in Emojis.getEmojisName()) {
                Emojis.getEmojiByName(emojiName)
            } else {
                it.value
            }
        }
        return newMessage
    }
}
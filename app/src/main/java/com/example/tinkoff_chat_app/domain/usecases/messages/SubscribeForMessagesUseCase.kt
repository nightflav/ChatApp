package com.example.tinkoff_chat_app.domain.usecases.messages

import android.content.SharedPreferences
import androidx.core.text.HtmlCompat
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import com.example.tinkoff_chat_app.domain.repository.profile_repository.ProfileRepository
import com.example.tinkoff_chat_app.models.data_transfer_models.MessageDto
import com.example.tinkoff_chat_app.models.data_transfer_models.ReactionDto
import com.example.tinkoff_chat_app.models.ui_models.MessageModel
import com.example.tinkoff_chat_app.models.ui_models.MessageReaction
import com.example.tinkoff_chat_app.models.ui_models.UserProfile
import com.example.tinkoff_chat_app.utils.*
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_FULLNAME
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_ID
import com.example.tinkoff_chat_app.utils.LocalData.SP_PROFILE_STATUS
import com.example.tinkoff_chat_app.utils.Network.BASE_URL_FILES_UPLOAD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubscribeForMessagesUseCase @Inject constructor(
    private val messagesRepo: MessagesRepository,
    spProfile: SharedPreferences,
    private val userRepo: ProfileRepository
) {
    private var user = UserProfile(
        fullName = spProfile.getString(SP_PROFILE_FULLNAME, "unknown")!!,
        id = spProfile.getInt(SP_PROFILE_ID, -1),
        status = spProfile.getString(SP_PROFILE_STATUS, "offline")
    )

    operator fun invoke(
        allTopics: Boolean
    ): Flow<Resource<List<MessageModel>>> {
        return messagesRepo.currentMessages.map {
            when (it) {
                is Resource.Error -> {
                    Resource.Error(it.error)
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Success -> {
                    Resource.Success(
                        data = it.data.toMessageModelList()!!
                            .transformTextToEmojis()
                            .transformMessagesToImages()
                            .reformatFromHtml()
                            .addDateSeparatorsAndTopics(allTopics)
                    )
                }
            }
        }
    }

    private suspend fun List<MessageDto>?.toMessageModelList() = this?.map {
        MessageModel(
            senderName = it.senderName,
            msg = it.msg,
            reactions = it.reactions.toReactionList(),
            user_id = getMsgTypeId(it.senderId),
            message_id = it.messageId,
            date = it.date.toDate(),
            avatarUri = it.avatarUri,
            topic = it.topic
        )
    }

    private suspend fun getMsgTypeId(senderId: Int): String {
        if (user.fullName == "unknown")
            user = userRepo.getProfile()
        return when (user.id) {
            senderId -> MessageTypes.SENDER
            else -> MessageTypes.RECEIVER
        }
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

    private fun List<MessageModel>.addDateSeparatorsAndTopics(allTopics: Boolean): List<MessageModel> {
        if (this.isEmpty()) return this
        val resultMessages = mutableListOf<MessageModel>()
        var prevDate = this.first().date
        var prevTopic = this.first().topic
        if (allTopics)
            resultMessages.add(
                MessageModel(
                    msg = this.first().msg.replace("<p>", "").replace("</p>", "\n"),
                    date = prevDate.parseDate(),
                    avatarUri = this.first().avatarUri,
                    topic = prevTopic,
                    isTopicSeparator = true
                )
            )
        resultMessages.add(
            MessageModel(
                msg = this.first().msg.replace("<p>", "").replace("</p>", "\n"),
                date = prevDate.parseDate(),
                isDataSeparator = true,
                avatarUri = this.first().avatarUri,
                topic = "data separator"
            )
        )

        for (msg in this) {
            val thisMsgDate = msg.date
            val thisMsgTopic = msg.topic
            if (allTopics)
                if (thisMsgTopic != prevTopic) {
                    resultMessages.add(
                        MessageModel(
                            date = thisMsgDate.parseDate(),
                            avatarUri = msg.avatarUri,
                            topic = msg.topic,
                            isTopicSeparator = true
                        )
                    )
                    prevTopic = msg.topic
                }
            if (thisMsgDate > prevDate) {
                resultMessages.add(
                    MessageModel(
                        date = thisMsgDate.parseDate(),
                        isDataSeparator = true,
                        avatarUri = msg.avatarUri,
                        topic = msg.topic
                    )
                )
                prevDate = thisMsgDate
            }
            resultMessages.add(
                msg.copy(
                    msg = msg.msg.replace("<p>", "").replace("</p>", "\n").trim()
                )
            )
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

    private fun List<MessageModel>.transformMessagesToImages(): List<MessageModel> {
        val result = mutableListOf<MessageModel>()
        for (msg in this) {
            if (msg.msg.containsLink()) {
                try {
                    val links = msg.msg.extractLinks()
                    for (probableLink in links) {
                        if (probableLink.isImageLink()) {
                            result.add(
                                msg.copy(
                                    containsImage = true,
                                    attachedImageUrl = probableLink,
                                    attachedFilename = probableLink.extractFilename()
                                )
                            )
                        } else if (probableLink.isDocsLink()) {
                            result.add(
                                msg.copy(
                                    containsDoc = true,
                                    attachedDocUrl = probableLink,
                                    attachedFilename = probableLink.extractFilename()
                                )
                            )
                        } else result.add(msg)
                    }
                } catch (_: Exception) {

                }
            } else result.add(msg)
        }
        return result
    }

    private fun String.extractFilename(): String = this.substringAfterLast('/')

    private fun String.containsLink(): Boolean = this.contains("href")

    private fun String.extractLinks(): List<String> {
        val links = mutableListOf<String>()
        val noParagraphsContent = this.replace("<p>", "")
            .replace("</p>", "\n")
        val lines = noParagraphsContent.split("\n")
        for (line in lines) {
            if (line.contains("href")) {
                if (line.contains("div|span".toRegex())) continue
                links.add(
                    BASE_URL_FILES_UPLOAD + if (line.first() == '/') "" else "/" + line
                        .substring(line.indexOf("href=\"") + 6, line.indexOf("\">"))
                )
            } else if (line.matches("https://tinkoff-android-spring-2023\\.zulipchat\\.com/user_uploads/[A-Za-z\\d]+/[A-Za-z\\d-_+]+/[A-Za-z\\d-_+.]+".toRegex()))
                links.add(line)
        }
        return links
    }

    private fun String.isImageLink(): Boolean = this.contains("jpg|png|bmp".toRegex())

    private fun String.isDocsLink(): Boolean = this.contains("pdf|doc|txt".toRegex())

    private fun List<MessageModel>.reformatFromHtml() = this.map {
        it.copy(
            msg = HtmlCompat.fromHtml(
                it.msg, HtmlCompat.FROM_HTML_MODE_COMPACT
            ).toString().trim()
        )
    }
}
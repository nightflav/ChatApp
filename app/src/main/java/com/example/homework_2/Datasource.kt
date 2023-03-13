package com.example.homework_2

import com.example.homework_2.models.Reaction
import com.example.homework_2.models.SingleMessage
import java.time.LocalDate


object Datasource {

    data class EmojiNCS(
        val name: String,
        val code: String
    ) {
        fun getCodeString() = String(Character.toChars(code.toInt(16)))
    }

    fun getMessages(): List<SingleMessage> {
        return messages
    }

    fun getEmojis(): List<String> {
        return emojiSetNCS.map { it.getCodeString() }
    }

    fun addMessage(msg: SingleMessage) {
        messages.add(msg)
    }

    fun changeReactionSelectedState(reaction: EmojiNCS, msgId: String) {
        val msg = getMessageByMessageId(msgId)
        val react = msg.reactions.firstOrNull { it.reaction == reaction }
        if(react != null) {
            react.isSelected = !react.isSelected
            if (react.isSelected)
                react.count++
            else
                react.count--
            if (react.count == 0)
                msg.reactions.remove(react)
        }
    }

    fun getReactions(msgId: String): MutableList<Reaction> =
        getMessageByMessageId(msgId).reactions.filter { it.count > 0 }.toMutableList()

    fun addReaction(reaction: Reaction, msgId: String) {
        val msg = getMessageByMessageId(msgId)
        if (reaction.reaction !in msg.reactions.map { it.reaction })
            msg.reactions.add(
                Reaction(
                    reaction = reaction.reaction,
                    count = 1,
                    isSelected = true
                )
            )
    }

    private fun getMessageByMessageId(msgId: String): SingleMessage {
        for (msg in messages)
            if (msg.message_id == msgId)
                return msg
        return emptyMessage
    }

    private val emptyMessage = SingleMessage("", "", mutableListOf(), "", "")

    val emojiSetNCS = listOf(
        EmojiNCS("grinning", "1f600"),
        EmojiNCS("smiley", "1f603"),
        EmojiNCS("big_smile", "1f604"),
        EmojiNCS("grinning_face_with_smiling_eyes", "1f601"),
        EmojiNCS("laughing", "1f606"),
        EmojiNCS("sweat_smile", "1f605"),
        EmojiNCS("rolling_on_the_floor_laughing", "1f923"),
        EmojiNCS("joy", "1f602"),
        EmojiNCS("smile", "1f642"),
        EmojiNCS("upside_down", "1f643"),
        EmojiNCS("wink", "1f609"),
        EmojiNCS("blush", "1f60a"),
        EmojiNCS("innocent", "1f607"),
        EmojiNCS("heart_eyes", "1f60d"),
        EmojiNCS("heart_kiss", "1f618"),
        EmojiNCS("kiss", "1f617"),
        EmojiNCS("smiling_face", "263a"),
        EmojiNCS("kiss_with_blush", "1f61a"),
        EmojiNCS("kiss_smiling_eyes", "1f619"),
        EmojiNCS("yum", "1f60b"),
        EmojiNCS("stuck_out_tongue", "1f61b"),
        EmojiNCS("stuck_out_tongue_wink", "1f61c"),
        EmojiNCS("stuck_out_tongue_closed_eyes", "1f61d"),
        EmojiNCS("money_face", "1f911"),
        EmojiNCS("hug", "1f917"),
        EmojiNCS("thinking", "1f914"),
        EmojiNCS("silence", "1f910"),
        EmojiNCS("neutral", "1f610"),
        EmojiNCS("expressionless", "1f611"),
        EmojiNCS("speechless", "1f636"),
        EmojiNCS("smirk", "1f60f"),
        EmojiNCS("unamused", "1f612"),
        EmojiNCS("rolling_eyes", "1f644"),
        EmojiNCS("grimacing", "1f62c"),
        EmojiNCS("lying", "1f925"),
        EmojiNCS("relieved", "1f60c"),
        EmojiNCS("pensive", "1f614"),
        EmojiNCS("sleepy", "1f62a"),
        EmojiNCS("drooling", "1f924"),
        EmojiNCS("sleeping", "1f634"),
        EmojiNCS("cant_talk", "1f637"),
        EmojiNCS("sick", "1f912"),
        EmojiNCS("hurt", "1f915"),
        EmojiNCS("nauseated", "1f922"),
        EmojiNCS("sneezing", "1f927"),
        EmojiNCS("dizzy", "1f635"),
        EmojiNCS("cowboy", "1f920"),
        EmojiNCS("sunglasses", "1f60e"),
        EmojiNCS("nerd", "1f913"),
        EmojiNCS("oh_no", "1f615"),
        EmojiNCS("worried", "1f61f"),
        EmojiNCS("frown", "1f641"),
        EmojiNCS("sad", "2639"),
        EmojiNCS("open_mouth", "1f62e"),
        EmojiNCS("hushed", "1f62f"),
        EmojiNCS("astonished", "1f632"),
        EmojiNCS("flushed", "1f633"),
        EmojiNCS("frowning", "1f626"),
        EmojiNCS("anguished", "1f627"),
        EmojiNCS("fear", "1f628"),
        EmojiNCS("cold_sweat", "1f630"),
        EmojiNCS("exhausted", "1f625"),
        EmojiNCS("cry", "1f622"),
        EmojiNCS("sob", "1f62d"),
        EmojiNCS("scream", "1f631"),
        EmojiNCS("confounded", "1f616"),
        EmojiNCS("persevere", "1f623"),
        EmojiNCS("disappointed", "1f61e"),
        EmojiNCS("sweat", "1f613"),
        EmojiNCS("weary", "1f629"),
        EmojiNCS("anguish", "1f62b"),
        EmojiNCS("triumph", "1f624"),
        EmojiNCS("rage", "1f621"),
        EmojiNCS("angry", "1f620"),
        EmojiNCS("smiling_devil", "1f608"),
        EmojiNCS("devil", "1f47f"),
        EmojiNCS("skull", "1f480"),
        EmojiNCS("skull_and_crossbones", "2620"),
        EmojiNCS("poop", "1f4a9"),
        EmojiNCS("clown", "1f921"),
        EmojiNCS("ogre", "1f479"),
        EmojiNCS("goblin", "1f47a"),
        EmojiNCS("ghost", "1f47b"),
        EmojiNCS("alien", "1f47d"),
        EmojiNCS("space_invader", "1f47e"),
        EmojiNCS("robot", "1f916"),
        EmojiNCS("smiley_cat", "1f63a"),
        EmojiNCS("smile_cat", "1f638"),
        EmojiNCS("joy_cat", "1f639"),
        EmojiNCS("heart_eyes_cat", "1f63b"),
        EmojiNCS("smirk_cat", "1f63c"),
        EmojiNCS("kissing_cat", "1f63d"),
        EmojiNCS("scream_cat", "1f640"),
        EmojiNCS("crying_cat", "1f63f"),
        EmojiNCS("angry_cat", "1f63e"),
        EmojiNCS("see_no_evil", "1f648"),
        EmojiNCS("hear_no_evil", "1f649"),
        EmojiNCS("speak_no_evil", "1f64a"),
        EmojiNCS("lipstick_kiss", "1f48b"),
        EmojiNCS("love_letter", "1f48c"),
        EmojiNCS("cupid", "1f498"),
        EmojiNCS("gift_heart", "1f49d"),
        EmojiNCS("sparkling_heart", "1f496"),
        EmojiNCS("heart_pulse", "1f497"),
        EmojiNCS("heartbeat", "1f493"),
        EmojiNCS("revolving_hearts", "1f49e"),
        EmojiNCS("two_hearts", "1f495"),
        EmojiNCS("heart_box", "1f49f"),
        EmojiNCS("heart_exclamation", "2763"),
        EmojiNCS("broken_heart", "1f494"),
        EmojiNCS("heart", "2764"),
        EmojiNCS("yellow_heart", "1f49b"),
        EmojiNCS("green_heart", "1f49a"),
        EmojiNCS("blue_heart", "1f499"),
        EmojiNCS("purple_heart", "1f49c"),
        EmojiNCS("black_heart", "1f5a4"),
        EmojiNCS("100", "1f4af"),
        EmojiNCS("anger", "1f4a2"),
        EmojiNCS("boom", "1f4a5"),
        EmojiNCS("seeing_stars", "1f4ab"),
        EmojiNCS("sweat_drops", "1f4a6"),
        EmojiNCS("dash", "1f4a8"),
        EmojiNCS("hole", "1f573"),
        EmojiNCS("bomb", "1f4a3"),
        EmojiNCS("umm", "1f4ac"),
        EmojiNCS("speech_bubble", "1f5e8"),
        EmojiNCS("anger_bubble", "1f5ef"),
        EmojiNCS("thought", "1f4ad"),
        EmojiNCS("zzz", "1f4a4"),
    )

    private val messages = mutableListOf(
        SingleMessage(
            isDataSeparator = true,
            date = parseDate(LocalDate.now())
        ),
        SingleMessage(
            msg = "Hello!",
            reactions = mutableListOf(
                Reaction(emojiSetNCS[0], 1, true)
            ),
            senderName = "Sender Name",
            user_id = "user_2",
            message_id = "0"
        ),
        SingleMessage(
            msg = "Hi!",
            senderName = "Receiver Name",
            user_id = "user_1",
            message_id = "1"
        ),
        SingleMessage(
            msg = "How are you?",
            reactions = mutableListOf(
                Reaction(emojiSetNCS[2], 3, false)
            ),
            senderName = "Sender Name",
            user_id = "user_2",
            message_id = "2"
        ),
        SingleMessage(
            msg = "I'm fine, thanks!",
            reactions = mutableListOf(
                Reaction(emojiSetNCS[3], 1, true)
            ),
            senderName = "Receiver Name",
            user_id = "user_1",
            message_id = "3"
        ),
        SingleMessage(
            msg = "And you?",
            reactions = mutableListOf(
                Reaction(emojiSetNCS[4], 6, true)
            ),
            senderName = "Sender Name",
            user_id = "user_1",
            message_id = "4"
        ),
        SingleMessage(
            isDataSeparator = true,
            date = parseDate(LocalDate.ofYearDay(2023, 130))
        ),
        SingleMessage(
            msg = "Hello?",
            reactions = mutableListOf(
                Reaction(emojiSetNCS[5], 1, false)
            ),
            senderName = "Sender Name",
            user_id = "user_1",
            message_id = "5"
        ),
        SingleMessage(
            msg = "Ah, yeah... I just wanted to tell you \n" +
                    "Я вас любил: любовь еще, быть может,\n" +
                    "В душе моей угасла не совсем;\n" +
                    "Но пусть она вас больше не тревожит;\n" +
                    "Я не хочу печалить вас ничем.\n" +
                    "Я вас любил безмолвно, безнадежно,\n" +
                    "То робостью, то ревностью томим;\n" +
                    "Я вас любил так искренно, так нежно,\n" +
                    "Как дай вам Бог любимой быть другим.",
            reactions = mutableListOf(
                Reaction(emojiSetNCS[9], 1, true)
            ),
            senderName = "Sender Name",
            user_id = "user_2",
            message_id = "6"
        ),
        SingleMessage(
            msg = "Could you please never write me again?) Thanks, buy..",
            reactions = mutableListOf(
                Reaction(emojiSetNCS[22], 1, true)
            ),
            senderName = "Sender Name",
            user_id = "user_1",
            message_id = "7",
            date = parseDate(LocalDate.now())
        ),
    )

    private fun parseDate(date: LocalDate): String {
        val currDayOfMonth = date.dayOfMonth
        val currMonth = when(date.month.value) {
            0 -> "Jan"
            1 -> "Feb"
            2 -> "Mar"
            3 -> "Apr"
            4 -> "May"
            5 -> "Jun"
            6 -> "Jul"
            7 -> "Aug"
            8 -> "Spt"
            9 -> "Oct"
            10 -> "Nov"
            else -> "Dec"
        }
        return "$currDayOfMonth $currMonth"
    }
}
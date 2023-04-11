package com.example.homework_2.utils

import okhttp3.Credentials

object Network{
    private const val USER_EMAIL = "iamsoalivenow@gmail.com"
    private const val API_KEY = "wKUjjObJDaru9ErHEf9ryU3yZ7NFvikA"
    const val BASE_URL = "https://tinkoff-android-spring-2023.zulipchat.com/api/v1/"
    val AUTH_KEY: String = Credentials.basic(USER_EMAIL, API_KEY)
}

object MessageTypes {
    const val SENDER = "sender"
    const val RECEIVER = "receiver"
    const val DATA_SEPARATOR = "data separator"
}

object Status {
    const val ACTIVE = "active"
    const val IDLE = "idle"
    const val OFFLINE = "offline"
}

object Emojis{
    data class EmojiNCS(
        val name: String,
        val code: String
    ) {
        fun getCodeString() = String(Character.toChars(code.toInt(16)))
    }

    fun getEmojis(): List<String> {
        return emojiSetNCS.map { it.getCodeString() }
    }
}

val emojiSetNCS = listOf(
    Emojis.EmojiNCS("grinning", "1f600"),
    Emojis.EmojiNCS("smiley", "1f603"),
    Emojis.EmojiNCS("big_smile", "1f604"),
    Emojis.EmojiNCS("grinning_face_with_smiling_eyes", "1f601"),
    Emojis.EmojiNCS("laughing", "1f606"),
    Emojis.EmojiNCS("sweat_smile", "1f605"),
    Emojis.EmojiNCS("rolling_on_the_floor_laughing", "1f923"),
    Emojis.EmojiNCS("joy", "1f602"),
    Emojis.EmojiNCS("smile", "1f642"),
    Emojis.EmojiNCS("upside_down", "1f643"),
    Emojis.EmojiNCS("wink", "1f609"),
    Emojis.EmojiNCS("blush", "1f60a"),
    Emojis.EmojiNCS("innocent", "1f607"),
    Emojis.EmojiNCS("heart_eyes", "1f60d"),
    Emojis.EmojiNCS("heart_kiss", "1f618"),
    Emojis.EmojiNCS("kiss", "1f617"),
    Emojis.EmojiNCS("smiling_face", "263a"),
    Emojis.EmojiNCS("kiss_with_blush", "1f61a"),
    Emojis.EmojiNCS("kiss_smiling_eyes", "1f619"),
    Emojis.EmojiNCS("yum", "1f60b"),
    Emojis.EmojiNCS("stuck_out_tongue", "1f61b"),
    Emojis.EmojiNCS("stuck_out_tongue_wink", "1f61c"),
    Emojis.EmojiNCS("stuck_out_tongue_closed_eyes", "1f61d"),
    Emojis.EmojiNCS("money_face", "1f911"),
    Emojis.EmojiNCS("hug", "1f917"),
    Emojis.EmojiNCS("thinking", "1f914"),
    Emojis.EmojiNCS("silence", "1f910"),
    Emojis.EmojiNCS("neutral", "1f610"),
    Emojis.EmojiNCS("expressionless", "1f611"),
    Emojis.EmojiNCS("speechless", "1f636"),
    Emojis.EmojiNCS("smirk", "1f60f"),
    Emojis.EmojiNCS("unamused", "1f612"),
    Emojis.EmojiNCS("rolling_eyes", "1f644"),
    Emojis.EmojiNCS("grimacing", "1f62c"),
    Emojis.EmojiNCS("lying", "1f925"),
    Emojis.EmojiNCS("relieved", "1f60c"),
    Emojis.EmojiNCS("pensive", "1f614"),
    Emojis.EmojiNCS("sleepy", "1f62a"),
    Emojis.EmojiNCS("drooling", "1f924"),
    Emojis.EmojiNCS("sleeping", "1f634"),
    Emojis.EmojiNCS("cant_talk", "1f637"),
    Emojis.EmojiNCS("sick", "1f912"),
    Emojis.EmojiNCS("hurt", "1f915"),
    Emojis.EmojiNCS("nauseated", "1f922"),
    Emojis.EmojiNCS("sneezing", "1f927"),
    Emojis.EmojiNCS("dizzy", "1f635"),
    Emojis.EmojiNCS("cowboy", "1f920"),
    Emojis.EmojiNCS("sunglasses", "1f60e"),
    Emojis.EmojiNCS("nerd", "1f913"),
    Emojis.EmojiNCS("oh_no", "1f615"),
    Emojis.EmojiNCS("worried", "1f61f"),
    Emojis.EmojiNCS("frown", "1f641"),
    Emojis.EmojiNCS("sad", "2639"),
    Emojis.EmojiNCS("open_mouth", "1f62e"),
    Emojis.EmojiNCS("hushed", "1f62f"),
    Emojis.EmojiNCS("astonished", "1f632"),
    Emojis.EmojiNCS("flushed", "1f633"),
    Emojis.EmojiNCS("frowning", "1f626"),
    Emojis.EmojiNCS("anguished", "1f627"),
    Emojis.EmojiNCS("fear", "1f628"),
    Emojis.EmojiNCS("cold_sweat", "1f630"),
    Emojis.EmojiNCS("exhausted", "1f625"),
    Emojis.EmojiNCS("cry", "1f622"),
    Emojis.EmojiNCS("sob", "1f62d"),
    Emojis.EmojiNCS("scream", "1f631"),
    Emojis.EmojiNCS("confounded", "1f616"),
    Emojis.EmojiNCS("persevere", "1f623"),
    Emojis.EmojiNCS("disappointed", "1f61e"),
    Emojis.EmojiNCS("sweat", "1f613"),
    Emojis.EmojiNCS("weary", "1f629"),
    Emojis.EmojiNCS("anguish", "1f62b"),
    Emojis.EmojiNCS("triumph", "1f624"),
    Emojis.EmojiNCS("rage", "1f621"),
    Emojis.EmojiNCS("angry", "1f620"),
    Emojis.EmojiNCS("smiling_devil", "1f608"),
    Emojis.EmojiNCS("devil", "1f47f"),
    Emojis.EmojiNCS("skull", "1f480"),
    Emojis.EmojiNCS("skull_and_crossbones", "2620"),
    Emojis.EmojiNCS("poop", "1f4a9"),
    Emojis.EmojiNCS("clown", "1f921"),
    Emojis.EmojiNCS("ogre", "1f479"),
    Emojis.EmojiNCS("goblin", "1f47a"),
    Emojis.EmojiNCS("ghost", "1f47b"),
    Emojis.EmojiNCS("alien", "1f47d"),
    Emojis.EmojiNCS("space_invader", "1f47e"),
    Emojis.EmojiNCS("robot", "1f916"),
    Emojis.EmojiNCS("smiley_cat", "1f63a"),
    Emojis.EmojiNCS("smile_cat", "1f638"),
    Emojis.EmojiNCS("joy_cat", "1f639"),
    Emojis.EmojiNCS("heart_eyes_cat", "1f63b"),
    Emojis.EmojiNCS("smirk_cat", "1f63c"),
    Emojis.EmojiNCS("kissing_cat", "1f63d"),
    Emojis.EmojiNCS("scream_cat", "1f640"),
    Emojis.EmojiNCS("crying_cat", "1f63f"),
    Emojis.EmojiNCS("angry_cat", "1f63e"),
    Emojis.EmojiNCS("see_no_evil", "1f648"),
    Emojis.EmojiNCS("hear_no_evil", "1f649"),
    Emojis.EmojiNCS("speak_no_evil", "1f64a"),
    Emojis.EmojiNCS("lipstick_kiss", "1f48b"),
    Emojis.EmojiNCS("love_letter", "1f48c"),
    Emojis.EmojiNCS("cupid", "1f498"),
    Emojis.EmojiNCS("gift_heart", "1f49d"),
    Emojis.EmojiNCS("sparkling_heart", "1f496"),
    Emojis.EmojiNCS("heart_pulse", "1f497"),
    Emojis.EmojiNCS("heartbeat", "1f493"),
    Emojis.EmojiNCS("revolving_hearts", "1f49e"),
    Emojis.EmojiNCS("two_hearts", "1f495"),
    Emojis.EmojiNCS("heart_box", "1f49f"),
    Emojis.EmojiNCS("heart_exclamation", "2763"),
    Emojis.EmojiNCS("broken_heart", "1f494"),
    Emojis.EmojiNCS("heart", "2764"),
    Emojis.EmojiNCS("yellow_heart", "1f49b"),
    Emojis.EmojiNCS("green_heart", "1f49a"),
    Emojis.EmojiNCS("blue_heart", "1f499"),
    Emojis.EmojiNCS("purple_heart", "1f49c"),
    Emojis.EmojiNCS("black_heart", "1f5a4"),
    Emojis.EmojiNCS("100", "1f4af"),
    Emojis.EmojiNCS("anger", "1f4a2"),
    Emojis.EmojiNCS("boom", "1f4a5"),
    Emojis.EmojiNCS("seeing_stars", "1f4ab"),
    Emojis.EmojiNCS("sweat_drops", "1f4a6"),
    Emojis.EmojiNCS("dash", "1f4a8"),
    Emojis.EmojiNCS("hole", "1f573"),
    Emojis.EmojiNCS("bomb", "1f4a3"),
    Emojis.EmojiNCS("umm", "1f4ac"),
    Emojis.EmojiNCS("speech_bubble", "1f5e8"),
    Emojis.EmojiNCS("anger_bubble", "1f5ef"),
    Emojis.EmojiNCS("thought", "1f4ad"),
    Emojis.EmojiNCS("zzz", "1f4a4"),
)
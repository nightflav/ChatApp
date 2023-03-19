package com.example.homework_2

import com.example.homework_2.models.*
import java.time.LocalDate

val contacts = listOf<UserProfile>(
    UserProfile(
        fullName = "Agatha Christie",
        isActive = false,
        meetingStatus = "Away for a bit...",
        tmpProfilePhoto = null,
        email = "123@gmail.com"
    ),
    UserProfile(
        fullName = "Arthur Conan Doyle",
        isActive = true,
        meetingStatus = "Looking for dinosaurs",
        tmpProfilePhoto = null,
        email = "321@liamg.moc"
    ),
)

val emojiSetNCS = listOf(
    Datasource.EmojiNCS("grinning", "1f600"),
    Datasource.EmojiNCS("smiley", "1f603"),
    Datasource.EmojiNCS("big_smile", "1f604"),
    Datasource.EmojiNCS("grinning_face_with_smiling_eyes", "1f601"),
    Datasource.EmojiNCS("laughing", "1f606"),
    Datasource.EmojiNCS("sweat_smile", "1f605"),
    Datasource.EmojiNCS("rolling_on_the_floor_laughing", "1f923"),
    Datasource.EmojiNCS("joy", "1f602"),
    Datasource.EmojiNCS("smile", "1f642"),
    Datasource.EmojiNCS("upside_down", "1f643"),
    Datasource.EmojiNCS("wink", "1f609"),
    Datasource.EmojiNCS("blush", "1f60a"),
    Datasource.EmojiNCS("innocent", "1f607"),
    Datasource.EmojiNCS("heart_eyes", "1f60d"),
    Datasource.EmojiNCS("heart_kiss", "1f618"),
    Datasource.EmojiNCS("kiss", "1f617"),
    Datasource.EmojiNCS("smiling_face", "263a"),
    Datasource.EmojiNCS("kiss_with_blush", "1f61a"),
    Datasource.EmojiNCS("kiss_smiling_eyes", "1f619"),
    Datasource.EmojiNCS("yum", "1f60b"),
    Datasource.EmojiNCS("stuck_out_tongue", "1f61b"),
    Datasource.EmojiNCS("stuck_out_tongue_wink", "1f61c"),
    Datasource.EmojiNCS("stuck_out_tongue_closed_eyes", "1f61d"),
    Datasource.EmojiNCS("money_face", "1f911"),
    Datasource.EmojiNCS("hug", "1f917"),
    Datasource.EmojiNCS("thinking", "1f914"),
    Datasource.EmojiNCS("silence", "1f910"),
    Datasource.EmojiNCS("neutral", "1f610"),
    Datasource.EmojiNCS("expressionless", "1f611"),
    Datasource.EmojiNCS("speechless", "1f636"),
    Datasource.EmojiNCS("smirk", "1f60f"),
    Datasource.EmojiNCS("unamused", "1f612"),
    Datasource.EmojiNCS("rolling_eyes", "1f644"),
    Datasource.EmojiNCS("grimacing", "1f62c"),
    Datasource.EmojiNCS("lying", "1f925"),
    Datasource.EmojiNCS("relieved", "1f60c"),
    Datasource.EmojiNCS("pensive", "1f614"),
    Datasource.EmojiNCS("sleepy", "1f62a"),
    Datasource.EmojiNCS("drooling", "1f924"),
    Datasource.EmojiNCS("sleeping", "1f634"),
    Datasource.EmojiNCS("cant_talk", "1f637"),
    Datasource.EmojiNCS("sick", "1f912"),
    Datasource.EmojiNCS("hurt", "1f915"),
    Datasource.EmojiNCS("nauseated", "1f922"),
    Datasource.EmojiNCS("sneezing", "1f927"),
    Datasource.EmojiNCS("dizzy", "1f635"),
    Datasource.EmojiNCS("cowboy", "1f920"),
    Datasource.EmojiNCS("sunglasses", "1f60e"),
    Datasource.EmojiNCS("nerd", "1f913"),
    Datasource.EmojiNCS("oh_no", "1f615"),
    Datasource.EmojiNCS("worried", "1f61f"),
    Datasource.EmojiNCS("frown", "1f641"),
    Datasource.EmojiNCS("sad", "2639"),
    Datasource.EmojiNCS("open_mouth", "1f62e"),
    Datasource.EmojiNCS("hushed", "1f62f"),
    Datasource.EmojiNCS("astonished", "1f632"),
    Datasource.EmojiNCS("flushed", "1f633"),
    Datasource.EmojiNCS("frowning", "1f626"),
    Datasource.EmojiNCS("anguished", "1f627"),
    Datasource.EmojiNCS("fear", "1f628"),
    Datasource.EmojiNCS("cold_sweat", "1f630"),
    Datasource.EmojiNCS("exhausted", "1f625"),
    Datasource.EmojiNCS("cry", "1f622"),
    Datasource.EmojiNCS("sob", "1f62d"),
    Datasource.EmojiNCS("scream", "1f631"),
    Datasource.EmojiNCS("confounded", "1f616"),
    Datasource.EmojiNCS("persevere", "1f623"),
    Datasource.EmojiNCS("disappointed", "1f61e"),
    Datasource.EmojiNCS("sweat", "1f613"),
    Datasource.EmojiNCS("weary", "1f629"),
    Datasource.EmojiNCS("anguish", "1f62b"),
    Datasource.EmojiNCS("triumph", "1f624"),
    Datasource.EmojiNCS("rage", "1f621"),
    Datasource.EmojiNCS("angry", "1f620"),
    Datasource.EmojiNCS("smiling_devil", "1f608"),
    Datasource.EmojiNCS("devil", "1f47f"),
    Datasource.EmojiNCS("skull", "1f480"),
    Datasource.EmojiNCS("skull_and_crossbones", "2620"),
    Datasource.EmojiNCS("poop", "1f4a9"),
    Datasource.EmojiNCS("clown", "1f921"),
    Datasource.EmojiNCS("ogre", "1f479"),
    Datasource.EmojiNCS("goblin", "1f47a"),
    Datasource.EmojiNCS("ghost", "1f47b"),
    Datasource.EmojiNCS("alien", "1f47d"),
    Datasource.EmojiNCS("space_invader", "1f47e"),
    Datasource.EmojiNCS("robot", "1f916"),
    Datasource.EmojiNCS("smiley_cat", "1f63a"),
    Datasource.EmojiNCS("smile_cat", "1f638"),
    Datasource.EmojiNCS("joy_cat", "1f639"),
    Datasource.EmojiNCS("heart_eyes_cat", "1f63b"),
    Datasource.EmojiNCS("smirk_cat", "1f63c"),
    Datasource.EmojiNCS("kissing_cat", "1f63d"),
    Datasource.EmojiNCS("scream_cat", "1f640"),
    Datasource.EmojiNCS("crying_cat", "1f63f"),
    Datasource.EmojiNCS("angry_cat", "1f63e"),
    Datasource.EmojiNCS("see_no_evil", "1f648"),
    Datasource.EmojiNCS("hear_no_evil", "1f649"),
    Datasource.EmojiNCS("speak_no_evil", "1f64a"),
    Datasource.EmojiNCS("lipstick_kiss", "1f48b"),
    Datasource.EmojiNCS("love_letter", "1f48c"),
    Datasource.EmojiNCS("cupid", "1f498"),
    Datasource.EmojiNCS("gift_heart", "1f49d"),
    Datasource.EmojiNCS("sparkling_heart", "1f496"),
    Datasource.EmojiNCS("heart_pulse", "1f497"),
    Datasource.EmojiNCS("heartbeat", "1f493"),
    Datasource.EmojiNCS("revolving_hearts", "1f49e"),
    Datasource.EmojiNCS("two_hearts", "1f495"),
    Datasource.EmojiNCS("heart_box", "1f49f"),
    Datasource.EmojiNCS("heart_exclamation", "2763"),
    Datasource.EmojiNCS("broken_heart", "1f494"),
    Datasource.EmojiNCS("heart", "2764"),
    Datasource.EmojiNCS("yellow_heart", "1f49b"),
    Datasource.EmojiNCS("green_heart", "1f49a"),
    Datasource.EmojiNCS("blue_heart", "1f499"),
    Datasource.EmojiNCS("purple_heart", "1f49c"),
    Datasource.EmojiNCS("black_heart", "1f5a4"),
    Datasource.EmojiNCS("100", "1f4af"),
    Datasource.EmojiNCS("anger", "1f4a2"),
    Datasource.EmojiNCS("boom", "1f4a5"),
    Datasource.EmojiNCS("seeing_stars", "1f4ab"),
    Datasource.EmojiNCS("sweat_drops", "1f4a6"),
    Datasource.EmojiNCS("dash", "1f4a8"),
    Datasource.EmojiNCS("hole", "1f573"),
    Datasource.EmojiNCS("bomb", "1f4a3"),
    Datasource.EmojiNCS("umm", "1f4ac"),
    Datasource.EmojiNCS("speech_bubble", "1f5e8"),
    Datasource.EmojiNCS("anger_bubble", "1f5ef"),
    Datasource.EmojiNCS("thought", "1f4ad"),
    Datasource.EmojiNCS("zzz", "1f4a4"),
)

val messagesMain = mutableListOf(
    SingleMessage(
        isDataSeparator = true,
        date = LocalDate.now().parseDate()
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
        date = LocalDate.ofYearDay(2023, 130).parseDate()
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
        msg = "Could you please never write me again?) Thanks, bye!..",
        reactions = mutableListOf(
            Reaction(emojiSetNCS[22], 1, true)
        ),
        senderName = "Sender Name",
        user_id = "user_1",
        message_id = "7",
        date = LocalDate.now().parseDate()
    ),
)

val messagesWitcher = mutableListOf(
    SingleMessage(
        isDataSeparator = true,
        date = LocalDate.now().parseDate()
    ),
    SingleMessage(
        msg = "I love witcher!",
        reactions = mutableListOf(
            Reaction(emojiSetNCS[0], 1, true)
        ),
        senderName = "Sender Name",
        user_id = "user_2",
        message_id = "0"
    )
)

val messages = mapOf(
    "main" to messagesMain,
    "geralt" to messagesWitcher,
    "classic" to mutableListOf(),
    "non classic" to mutableListOf()
)

val streams = listOf(
    Stream(
        isSubscribed = true,
        name = "#general",
        isSelected = false,
        topics = listOf(
            Topic(
                name = "classic",
                msgCount = 100,
                parentId = "general",
                id = "classic"
            ),
            Topic(
                name = "not classic",
                msgCount = 1000,
                parentId = "general",
                id = "non classic"
            )
        ),
        id = "general"
    ),
    Stream(
        name = "#abc",
        topics = listOf(Topic(name = "main", msgCount = 5, parentId = "abc", id = "main")),
        id = "abc"
    ),
    Stream(
        name = "#witcher",
        topics = listOf(
            Topic(
                name = "Geralt",
                msgCount = 999,
                parentId = "witcher",
                id = "geralt"
            )
        ),
        id = "witcher"
    )
)
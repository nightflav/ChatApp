package com.example.homework_2

import com.example.homework_2.datasource.MessageDatasource
import com.example.homework_2.models.*
import java.time.LocalDate

val contacts = listOf<UserProfile>(
    UserProfile(
        fullName = "Agatha Christie",
        isActive = false,
        meetingStatus = "Away for a bit...",
        tmpProfilePhoto = R.drawable.tmp_agatha_christie,
        email = "123@gmail.com"
    ),
    UserProfile(
        fullName = "Arthur Conan Doyle",
        isActive = true,
        meetingStatus = "Looking for dinosaurs",
        tmpProfilePhoto = R.drawable.tmp_arthur_conan_doyle,
        email = "321@liamg.moc"
    ),
)

val emojiSetNCS = listOf(
    MessageDatasource.EmojiNCS("grinning", "1f600"),
    MessageDatasource.EmojiNCS("smiley", "1f603"),
    MessageDatasource.EmojiNCS("big_smile", "1f604"),
    MessageDatasource.EmojiNCS("grinning_face_with_smiling_eyes", "1f601"),
    MessageDatasource.EmojiNCS("laughing", "1f606"),
    MessageDatasource.EmojiNCS("sweat_smile", "1f605"),
    MessageDatasource.EmojiNCS("rolling_on_the_floor_laughing", "1f923"),
    MessageDatasource.EmojiNCS("joy", "1f602"),
    MessageDatasource.EmojiNCS("smile", "1f642"),
    MessageDatasource.EmojiNCS("upside_down", "1f643"),
    MessageDatasource.EmojiNCS("wink", "1f609"),
    MessageDatasource.EmojiNCS("blush", "1f60a"),
    MessageDatasource.EmojiNCS("innocent", "1f607"),
    MessageDatasource.EmojiNCS("heart_eyes", "1f60d"),
    MessageDatasource.EmojiNCS("heart_kiss", "1f618"),
    MessageDatasource.EmojiNCS("kiss", "1f617"),
    MessageDatasource.EmojiNCS("smiling_face", "263a"),
    MessageDatasource.EmojiNCS("kiss_with_blush", "1f61a"),
    MessageDatasource.EmojiNCS("kiss_smiling_eyes", "1f619"),
    MessageDatasource.EmojiNCS("yum", "1f60b"),
    MessageDatasource.EmojiNCS("stuck_out_tongue", "1f61b"),
    MessageDatasource.EmojiNCS("stuck_out_tongue_wink", "1f61c"),
    MessageDatasource.EmojiNCS("stuck_out_tongue_closed_eyes", "1f61d"),
    MessageDatasource.EmojiNCS("money_face", "1f911"),
    MessageDatasource.EmojiNCS("hug", "1f917"),
    MessageDatasource.EmojiNCS("thinking", "1f914"),
    MessageDatasource.EmojiNCS("silence", "1f910"),
    MessageDatasource.EmojiNCS("neutral", "1f610"),
    MessageDatasource.EmojiNCS("expressionless", "1f611"),
    MessageDatasource.EmojiNCS("speechless", "1f636"),
    MessageDatasource.EmojiNCS("smirk", "1f60f"),
    MessageDatasource.EmojiNCS("unamused", "1f612"),
    MessageDatasource.EmojiNCS("rolling_eyes", "1f644"),
    MessageDatasource.EmojiNCS("grimacing", "1f62c"),
    MessageDatasource.EmojiNCS("lying", "1f925"),
    MessageDatasource.EmojiNCS("relieved", "1f60c"),
    MessageDatasource.EmojiNCS("pensive", "1f614"),
    MessageDatasource.EmojiNCS("sleepy", "1f62a"),
    MessageDatasource.EmojiNCS("drooling", "1f924"),
    MessageDatasource.EmojiNCS("sleeping", "1f634"),
    MessageDatasource.EmojiNCS("cant_talk", "1f637"),
    MessageDatasource.EmojiNCS("sick", "1f912"),
    MessageDatasource.EmojiNCS("hurt", "1f915"),
    MessageDatasource.EmojiNCS("nauseated", "1f922"),
    MessageDatasource.EmojiNCS("sneezing", "1f927"),
    MessageDatasource.EmojiNCS("dizzy", "1f635"),
    MessageDatasource.EmojiNCS("cowboy", "1f920"),
    MessageDatasource.EmojiNCS("sunglasses", "1f60e"),
    MessageDatasource.EmojiNCS("nerd", "1f913"),
    MessageDatasource.EmojiNCS("oh_no", "1f615"),
    MessageDatasource.EmojiNCS("worried", "1f61f"),
    MessageDatasource.EmojiNCS("frown", "1f641"),
    MessageDatasource.EmojiNCS("sad", "2639"),
    MessageDatasource.EmojiNCS("open_mouth", "1f62e"),
    MessageDatasource.EmojiNCS("hushed", "1f62f"),
    MessageDatasource.EmojiNCS("astonished", "1f632"),
    MessageDatasource.EmojiNCS("flushed", "1f633"),
    MessageDatasource.EmojiNCS("frowning", "1f626"),
    MessageDatasource.EmojiNCS("anguished", "1f627"),
    MessageDatasource.EmojiNCS("fear", "1f628"),
    MessageDatasource.EmojiNCS("cold_sweat", "1f630"),
    MessageDatasource.EmojiNCS("exhausted", "1f625"),
    MessageDatasource.EmojiNCS("cry", "1f622"),
    MessageDatasource.EmojiNCS("sob", "1f62d"),
    MessageDatasource.EmojiNCS("scream", "1f631"),
    MessageDatasource.EmojiNCS("confounded", "1f616"),
    MessageDatasource.EmojiNCS("persevere", "1f623"),
    MessageDatasource.EmojiNCS("disappointed", "1f61e"),
    MessageDatasource.EmojiNCS("sweat", "1f613"),
    MessageDatasource.EmojiNCS("weary", "1f629"),
    MessageDatasource.EmojiNCS("anguish", "1f62b"),
    MessageDatasource.EmojiNCS("triumph", "1f624"),
    MessageDatasource.EmojiNCS("rage", "1f621"),
    MessageDatasource.EmojiNCS("angry", "1f620"),
    MessageDatasource.EmojiNCS("smiling_devil", "1f608"),
    MessageDatasource.EmojiNCS("devil", "1f47f"),
    MessageDatasource.EmojiNCS("skull", "1f480"),
    MessageDatasource.EmojiNCS("skull_and_crossbones", "2620"),
    MessageDatasource.EmojiNCS("poop", "1f4a9"),
    MessageDatasource.EmojiNCS("clown", "1f921"),
    MessageDatasource.EmojiNCS("ogre", "1f479"),
    MessageDatasource.EmojiNCS("goblin", "1f47a"),
    MessageDatasource.EmojiNCS("ghost", "1f47b"),
    MessageDatasource.EmojiNCS("alien", "1f47d"),
    MessageDatasource.EmojiNCS("space_invader", "1f47e"),
    MessageDatasource.EmojiNCS("robot", "1f916"),
    MessageDatasource.EmojiNCS("smiley_cat", "1f63a"),
    MessageDatasource.EmojiNCS("smile_cat", "1f638"),
    MessageDatasource.EmojiNCS("joy_cat", "1f639"),
    MessageDatasource.EmojiNCS("heart_eyes_cat", "1f63b"),
    MessageDatasource.EmojiNCS("smirk_cat", "1f63c"),
    MessageDatasource.EmojiNCS("kissing_cat", "1f63d"),
    MessageDatasource.EmojiNCS("scream_cat", "1f640"),
    MessageDatasource.EmojiNCS("crying_cat", "1f63f"),
    MessageDatasource.EmojiNCS("angry_cat", "1f63e"),
    MessageDatasource.EmojiNCS("see_no_evil", "1f648"),
    MessageDatasource.EmojiNCS("hear_no_evil", "1f649"),
    MessageDatasource.EmojiNCS("speak_no_evil", "1f64a"),
    MessageDatasource.EmojiNCS("lipstick_kiss", "1f48b"),
    MessageDatasource.EmojiNCS("love_letter", "1f48c"),
    MessageDatasource.EmojiNCS("cupid", "1f498"),
    MessageDatasource.EmojiNCS("gift_heart", "1f49d"),
    MessageDatasource.EmojiNCS("sparkling_heart", "1f496"),
    MessageDatasource.EmojiNCS("heart_pulse", "1f497"),
    MessageDatasource.EmojiNCS("heartbeat", "1f493"),
    MessageDatasource.EmojiNCS("revolving_hearts", "1f49e"),
    MessageDatasource.EmojiNCS("two_hearts", "1f495"),
    MessageDatasource.EmojiNCS("heart_box", "1f49f"),
    MessageDatasource.EmojiNCS("heart_exclamation", "2763"),
    MessageDatasource.EmojiNCS("broken_heart", "1f494"),
    MessageDatasource.EmojiNCS("heart", "2764"),
    MessageDatasource.EmojiNCS("yellow_heart", "1f49b"),
    MessageDatasource.EmojiNCS("green_heart", "1f49a"),
    MessageDatasource.EmojiNCS("blue_heart", "1f499"),
    MessageDatasource.EmojiNCS("purple_heart", "1f49c"),
    MessageDatasource.EmojiNCS("black_heart", "1f5a4"),
    MessageDatasource.EmojiNCS("100", "1f4af"),
    MessageDatasource.EmojiNCS("anger", "1f4a2"),
    MessageDatasource.EmojiNCS("boom", "1f4a5"),
    MessageDatasource.EmojiNCS("seeing_stars", "1f4ab"),
    MessageDatasource.EmojiNCS("sweat_drops", "1f4a6"),
    MessageDatasource.EmojiNCS("dash", "1f4a8"),
    MessageDatasource.EmojiNCS("hole", "1f573"),
    MessageDatasource.EmojiNCS("bomb", "1f4a3"),
    MessageDatasource.EmojiNCS("umm", "1f4ac"),
    MessageDatasource.EmojiNCS("speech_bubble", "1f5e8"),
    MessageDatasource.EmojiNCS("anger_bubble", "1f5ef"),
    MessageDatasource.EmojiNCS("thought", "1f4ad"),
    MessageDatasource.EmojiNCS("zzz", "1f4a4"),
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
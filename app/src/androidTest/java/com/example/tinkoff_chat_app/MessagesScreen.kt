package com.example.tinkoff_chat_app

import android.view.View
import com.example.tinkoff_chat_app.custom_kviews.KReactionGroup
import com.example.tinkoff_chat_app.screens.message.MessagesFragment
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

class MessagesScreen : KScreen<MessagesScreen>() {

    override val layoutId: Int = R.layout.fragment_messages
    override val viewClass: Class<*> = MessagesFragment::class.java

    val messages = KRecyclerView({ withId(R.id.rv_chat) }, {
        itemType { SentMessage(it) }
        itemType { ReceivedMessage(it) }
        itemType { DateSeparator(it) }
    })

    val errorText = KTextView { withId(R.id.tv_messages_error) }

    val reloadMessagesButton = KButton { withId(R.id.btn_tmp_refresh_messages) }

    val dialogReactions =
        KRecyclerView({ withId(R.id.rv_emoji_bsd) }, { itemType { BSDReaction(it) } })

    class SentMessage(matcher: Matcher<View>) : KRecyclerItem<SentMessage>(matcher) {
        val reactions = KReactionGroup { withId(R.id.react_sent_message) }
    }

    class ReceivedMessage(matcher: Matcher<View>) : KRecyclerItem<ReceivedMessage>(matcher) {
        val reactions = KReactionGroup { withId(R.id.reactions) }
    }

    class DateSeparator(matcher: Matcher<View>) : KRecyclerItem<DateSeparator>(matcher) {
        val date = KTextView { withId(R.id.date_separator) }
    }

    class BSDReaction(matcher: Matcher<View>) : KRecyclerItem<BSDReaction>(matcher) {
        val emoji = KTextView { withId(R.id.emoji_item) }
    }

}
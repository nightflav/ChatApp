package com.example.tinkoff_chat_app

import android.view.View
import com.example.tinkoff_chat_app.screens.stream.StreamFragment
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import org.hamcrest.Matcher

class MainScreen : KScreen<MainScreen>() {

    override val layoutId: Int = R.layout.stream_layout
    override val viewClass: Class<*> = StreamFragment::class.java

    val streamsAndTopics = KRecyclerView({ withId(R.id.rv_streams) }, {
        itemType { Stream(it) }
        itemType { Topic(it) }
    })

    class Stream(matcher: Matcher<View>) : KRecyclerItem<Stream>(matcher) {

    }
    class Topic(matcher: Matcher<View>) : KRecyclerItem<Stream>(matcher) {

    }

}
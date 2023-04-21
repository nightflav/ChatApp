package com.example.tinkoff_chat_app.di.screen_components.messages

import com.example.tinkoff_chat_app.screens.message.MessagesFragment
import dagger.Subcomponent

@MessagesScope
@Subcomponent
interface MessagesSubcomponent {

    fun inject(messagesFragment: MessagesFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): MessagesSubcomponent
    }

}
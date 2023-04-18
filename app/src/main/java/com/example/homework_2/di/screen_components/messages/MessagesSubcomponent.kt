package com.example.homework_2.di.screen_components.messages

import com.example.homework_2.screens.message.MessagesFragment
import dagger.Subcomponent

@MessagesScope
@Subcomponent(
    modules = [MessageRepositoryModule::class]
)
interface MessagesSubcomponent {

    fun inject(messagesFragment: MessagesFragment)

    @Subcomponent.Builder
    interface Builder {
        fun build(): MessagesSubcomponent
    }

}
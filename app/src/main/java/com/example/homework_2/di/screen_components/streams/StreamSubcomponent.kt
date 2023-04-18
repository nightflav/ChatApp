package com.example.homework_2.di.screen_components.streams

import com.example.homework_2.di.screen_components.messages.MessagesSubcomponent
import com.example.homework_2.screens.stream.StreamFragment
import dagger.Subcomponent

@StreamScope
@Subcomponent(
    modules = [StreamRepositoryModule::class]
)
interface StreamSubcomponent {

    fun inject(fragment: StreamFragment)

    fun messageComponent(): MessagesSubcomponent.Builder

    @Subcomponent.Builder
    interface Builder {
        fun build(): StreamSubcomponent
    }
}


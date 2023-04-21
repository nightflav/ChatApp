package com.example.tinkoff_chat_app.di.screen_components.streams

import com.example.tinkoff_chat_app.screens.stream.StreamFragment
import dagger.Subcomponent

@StreamScope
@Subcomponent
interface StreamSubcomponent {

    fun inject(fragment: StreamFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): StreamSubcomponent
    }

}


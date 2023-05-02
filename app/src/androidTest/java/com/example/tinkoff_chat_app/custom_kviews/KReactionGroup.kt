package com.example.tinkoff_chat_app.custom_kviews

import io.github.kakaocup.kakao.common.builders.ViewBuilder
import io.github.kakaocup.kakao.common.views.KBaseView

class KReactionGroup : KBaseView<KReactionGroup>, ReactionGroupActions, ReactionGroupAssertions {
    constructor(function: ViewBuilder.() -> Unit) : super(function)
}
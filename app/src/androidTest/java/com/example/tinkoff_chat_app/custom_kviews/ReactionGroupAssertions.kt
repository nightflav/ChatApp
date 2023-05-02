package com.example.tinkoff_chat_app.custom_kviews

import androidx.test.espresso.assertion.ViewAssertions
import com.example.tinkoff_chat_app.custom_kviews.reactions_assertions.IsCountAtAssertion
import com.example.tinkoff_chat_app.custom_kviews.reactions_assertions.IsEmojiAtAssertion
import com.example.tinkoff_chat_app.custom_kviews.reactions_assertions.IsEmojiNotSelectedAssertion
import com.example.tinkoff_chat_app.custom_kviews.reactions_assertions.IsEmojiSelectedAssertion
import io.github.kakaocup.kakao.common.assertions.BaseAssertions
import io.github.kakaocup.kakao.common.matchers.ChildCountMatcher

interface ReactionGroupAssertions : BaseAssertions {

    fun hasSizeOf(size: Int) {
        view.check(ViewAssertions.matches(ChildCountMatcher(size)))
    }

    fun isEmojiAt(position: Int, emoji: String) {
        view.check(ViewAssertions.matches(IsEmojiAtAssertion(position, emoji)))
    }

    fun isEmojiCountIs(position: Int, count: Int) {
        view.check(ViewAssertions.matches(IsCountAtAssertion(position, count)))
    }

    fun isEmojiSelected(position: Int) {
        view.check(ViewAssertions.matches(IsEmojiSelectedAssertion(position)))
    }

    fun isEmojiNotSelected(position: Int) {
        view.check(ViewAssertions.matches(IsEmojiNotSelectedAssertion(position)))
    }

}
package com.example.tinkoff_chat_app.custom_kviews.reactions_assertions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.size
import com.example.tinkoff_chat_app.views.EmojiView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class IsEmojiAtAssertion(
    private val position: Int,
    private val emoji: String
) : TypeSafeMatcher<View>(View::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("At $position is EmojiView with $emoji")
    }

    override fun matchesSafely(item: View?): Boolean {
        return if(item is ViewGroup && position < item.size && item[position] is EmojiView) {
            (item[position] as EmojiView).emoji == emoji
        } else false
    }

}
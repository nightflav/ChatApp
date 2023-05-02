package com.example.tinkoff_chat_app.custom_kviews.reactions_assertions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.size
import com.example.tinkoff_chat_app.views.EmojiView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class IsEmojiNotSelectedAssertion(
    private val position: Int,
) : TypeSafeMatcher<View>(View::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("At $position is EmojiView and it is not selected")
    }

    override fun matchesSafely(item: View?): Boolean {
        return if(item is ViewGroup && position < item.size && item[position] is EmojiView) {
            !(item[position] as EmojiView).isSelected
        } else false
    }

}
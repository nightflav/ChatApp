package com.example.tinkoff_chat_app.custom_kviews

import android.view.View
import androidx.core.view.get
import androidx.core.view.size
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import com.example.tinkoff_chat_app.views.ReactionsViewGroup
import io.github.kakaocup.kakao.common.actions.BaseActions
import org.hamcrest.Matcher

interface ReactionGroupActions : BaseActions {

    fun onReactionClick(position: Int) {
        view.perform(
            object : ViewAction {
                override fun getDescription(): String {
                    return "Click on reaction at $position"
                }

                override fun getConstraints(): Matcher<View> {
                    return ViewMatchers.isAssignableFrom(ReactionsViewGroup::class.java)
                }

                override fun perform(uiController: UiController?, view: View?) {
                    view?.let { reactions ->
                        if (reactions is ReactionsViewGroup && position < reactions.size)
                            reactions[position].performClick()
                    }
                }

            }
        )
    }

}
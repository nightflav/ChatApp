package com.example.tinkoff_chat_app

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import com.example.tinkoff_chat_app.consts.Consts.createDefArgForFragment
import com.example.tinkoff_chat_app.screens.message.MessagesFragment
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import junit.framework.TestCase.assertTrue
import org.junit.Test

class MessagesFragmentTest : TestCase() {

    @Test
    fun showMessages() = run {
        ActivityScenario.launch(MainActivity::class.java)

        val messageScreen = MessagesScreen()

        step("Проверяем наличие сообщений на экране или пустой экран, если сообщений нету") {
            messageScreen.messages.isDisplayed()
            messageScreen.errorText.isNotDisplayed()
            val messagesCount = messageScreen.messages.getSize()
            assertTrue(0 <= messagesCount)
        }

    }

    @Test
    fun showError() = run {
        val messageScreen = MessagesScreen()
        val streamScreen = MainScreen()

        step("go to message screen") {
            streamScreen.streamsAndTopics.childAt<MainScreen.Stream>(0) {
                click()
            }
            streamScreen.streamsAndTopics.childAt<MainScreen.Topic>(1) {
                click()
            }
        }

        step("Проверяем на отображение ошибки, когда вызывается не существующий топик") {
            messageScreen.messages.isNotDisplayed()
            messageScreen.errorText.isDisplayed()
        }

    }

    @Test
    fun testReactions() = run {
        val fragmentParams = createDefArgForFragment()
        launchFragmentInContainer<MessagesFragment>(fragmentParams)

        val messagesScreen = MessagesScreen()

        step("Добавляем реакцию лонгкликом и обновлем сообщения") {
            messagesScreen.messages.childAt<MessagesScreen.ReceivedMessage>(0) {
                longClick()
            }
            messagesScreen.dialogReactions.childAt<MessagesScreen.BSDReaction>(0) {
                click()
            }
        }
    }

}
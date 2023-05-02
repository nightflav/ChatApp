package com.example.tinkoff_chat_app.domain.repository.messages_repository

import com.example.tinkoff_chat_app.data.messages.MessageDao
import com.example.tinkoff_chat_app.models.data_transfer_models.MessageDto
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.stub.CachedMessageDaoStub
import com.example.tinkoff_chat_app.stub.EmptyMessageDaoStub
import com.example.tinkoff_chat_app.utils.Consts.badMessageResponse
import com.example.tinkoff_chat_app.utils.Consts.messageResponse
import com.example.tinkoff_chat_app.utils.Consts.testData
import com.example.tinkoff_chat_app.utils.Consts.testEmoji
import com.example.tinkoff_chat_app.utils.Consts.testMessage
import com.example.tinkoff_chat_app.utils.Resource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MessagesRepositoryImplTest {

    @get:Rule
    val mockServer = MockWebServer()
    private lateinit var messagesRepository: MessagesRepository
    private lateinit var chatApi: ChatApi
    private lateinit var chatDao: MessageDao

    @Before
    fun setup() {
        mockServer
        chatApi = Retrofit.Builder()
            .baseUrl(mockServer.url("/"))
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .build().create(ChatApi::class.java)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `empty database request no internet`() = runTest {
        chatDao = EmptyMessageDaoStub()
        messagesRepository = MessagesRepositoryImpl(chatApi, chatDao)

        messagesRepository.loadMessagesWhenStart("any", "any", 100, null, shouldFetch = false)

        val actual = messagesRepository.currentMessages.value

        val expected = Resource.Error<List<MessageDto>>(IllegalStateException())

        assertEquals(expected::class, actual::class)
        assertEquals(expected.data, (actual as Resource.Error).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `empty database request with internet bad request`() = runTest {
        chatDao = EmptyMessageDaoStub()
        messagesRepository = MessagesRepositoryImpl(chatApi, chatDao)

        mockServer.enqueue(MockResponse().setBody(badMessageResponse))

        messagesRepository.loadMessagesWhenStart("any", "any", 100, null, shouldFetch = true)

        val actual = messagesRepository.currentMessages.value
        val expected = Resource.Error<List<MessageDto>>(IllegalStateException())

        assertEquals(expected::class, actual::class)
        assertEquals(expected.data, (actual as Resource.Error).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `empty database request with internet`() = runTest {
        chatDao = EmptyMessageDaoStub()
        messagesRepository = MessagesRepositoryImpl(chatApi, chatDao)

        mockServer.enqueue(MockResponse().setBody(messageResponse))

        messagesRepository.loadMessagesWhenStart("any", "any", 100, null, shouldFetch = true)

        val actual = messagesRepository.currentMessages.value

        val expected = Resource.Success(
            data = testData
        )

        assertEquals(expected::class, actual::class)
        assertEquals(expected.data, (actual as Resource.Success).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `database request no internet`() = runTest {
        chatDao = CachedMessageDaoStub()
        messagesRepository = MessagesRepositoryImpl(chatApi, chatDao)

        messagesRepository.loadMessagesWhenStart("any", "any", 100, null, shouldFetch = false)

        val actual = messagesRepository.currentMessages.value

        val expected = Resource.Success(data = testData)

        assertEquals(expected::class, actual::class)
        assertEquals(expected.data, (actual as Resource.Success).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `post reaction request`() = runTest {
        chatDao = CachedMessageDaoStub()
        mockServer.enqueue(MockResponse())
        messagesRepository = MessagesRepositoryImpl(chatApi, chatDao)

        messagesRepository.sendReaction(testMessage.messageId.toString(), testEmoji.emojiName)

        val expected = "POST /messages/${testMessage.messageId}/reactions?emoji_name=${testEmoji.emojiName}"

        val request = mockServer.takeRequest()

        val actual = request.method + ' ' + request.path

        assertEquals(expected, actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete reaction request`() = runTest {
        chatDao = CachedMessageDaoStub()
        mockServer.enqueue(MockResponse())
        messagesRepository = MessagesRepositoryImpl(chatApi, chatDao)

        messagesRepository.removeReaction(testMessage.messageId.toString(), testEmoji.emojiName)

        val expected = "DELETE /messages/${testMessage.messageId}/reactions?emoji_name=${testEmoji.emojiName}"

        val request = mockServer.takeRequest()

        val actual = request.method + ' ' + request.path

        assertEquals(expected, actual)
    }

    @After
    fun clear() {
        mockServer.shutdown()
    }
}
package com.example.tinkoff_chat_app.network

import com.example.tinkoff_chat_app.*
import com.example.tinkoff_chat_app.models.network_models.messages.MessageResponse
import com.example.tinkoff_chat_app.models.network_models.streams.StreamsResponse
import com.example.tinkoff_chat_app.models.network_models.subscriptions.SubscriptionsResponse
import com.example.tinkoff_chat_app.models.network_models.topics.TopicsResponse
import com.example.tinkoff_chat_app.models.network_models.users.Member
import com.example.tinkoff_chat_app.models.network_models.users.UsersResponse
import com.example.tinkoff_chat_app.models.network_models.users.presence.UserPresenceResponse
import com.example.tinkoff_chat_app.models.network_models.users.presence.allUsersPresence.AllUsersPresenceResponse
import retrofit2.Response
import retrofit2.http.*

interface ChatApi {

    @GET("streams")
    suspend fun getStreams(
    ): Response<StreamsResponse>

    @GET("users/me/subscriptions")
    suspend fun getSubscriptions(
    ): Response<SubscriptionsResponse>

    @GET("users/me/{stream_id}/topics")
    suspend fun getTopics(
        @Path("stream_id") streamId: Int,
    ): Response<TopicsResponse>

    @GET("users")
    suspend fun getAllUsers(
    ): Response<UsersResponse>

    @GET("users/{user_id_or_email}/presence")
    suspend fun getUserPresence(
        @Path("user_id_or_email") userId: String,
    ): Response<UserPresenceResponse>

    @GET("realm/presence")
    suspend fun getAllUsersPresence(
    ): Response<AllUsersPresenceResponse>

    @GET("users/me")
    suspend fun getProfile(
    ): Response<Member>

    @GET("messages")
    suspend fun getMessages(
        @Query("anchor") anchor: String = "newest",
        @Query("num_before") numBefore: Int = 1000,
        @Query("num_after") numAfter: Int = 0,
        @Query("narrow") narrow: String,
        @Query("include_anchor") includeAnchor: Boolean = false
    ): Response<MessageResponse>

    @POST("messages")
    suspend fun sendMessage(
        @Query("type") type: String = "stream",
        @Query("to") to: String,
        @Query("content") content: String,
        @Query("topic") topic: String,
    )

    @POST("messages/{msg_id}/reactions")
    suspend fun sendReaction(
        @Path("msg_id") msgId: String,
        @Query("emoji_name") emojiName: String,
    )

    @DELETE("messages/{msg_id}/reactions")
    suspend fun removeReaction(
        @Path("msg_id") msgId: String,
        @Query("emoji_name") emojiName: String,
    )
}
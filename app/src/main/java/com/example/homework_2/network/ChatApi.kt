package com.example.homework_2.network

import com.example.homework_2.*
import com.example.homework_2.network.networkModels.messages.MessageResponse
import com.example.homework_2.network.networkModels.streams.StreamsResponse
import com.example.homework_2.network.networkModels.subscriptions.SubscriptionsResponse
import com.example.homework_2.network.networkModels.topics.TopicsResponse
import com.example.homework_2.network.networkModels.users.Member
import com.example.homework_2.network.networkModels.users.UsersResponse
import com.example.homework_2.network.networkModels.users.presence.UserPresenceResponse
import com.example.homework_2.network.networkModels.users.presence.allUsersPresence.AllUsersPresenceResponse
import com.example.homework_2.utils.Network.AUTH_KEY
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
        @Path("stream_id") streamId: String,
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
        @Query("num_before") numBefore: Int = 10,
        @Query("num_after") numAfter: Int = 0,
        @Query("narrow") narrow: String,
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
package com.example.tinkoff_chat_app.di

import com.example.tinkoff_chat_app.di.screen_components.contacts.ContactsSubcomponent
import com.example.tinkoff_chat_app.di.screen_components.messages.MessagesSubcomponent
import com.example.tinkoff_chat_app.di.screen_components.streams.StreamSubcomponent
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.utils.Network
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module(subcomponents = [StreamSubcomponent::class, MessagesSubcomponent::class, ContactsSubcomponent::class])
class NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val request: Request =
            chain.request().newBuilder().addHeader("Authorization", Network.AUTH_KEY).build()
        chain.proceed(request)
    }.build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi, httpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl(Network.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).client(httpClient).build()

    @Provides
    @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi = retrofit.create(ChatApi::class.java)
}
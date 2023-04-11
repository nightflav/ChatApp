package com.example.homework_2.network

import com.example.homework_2.utils.Network.AUTH_KEY
import com.example.homework_2.utils.Network.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitInstance {
    companion object {

        private val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request: Request =
                    chain.request().newBuilder().addHeader("Authorization", AUTH_KEY).build()
                chain.proceed(request)
            }

        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(httpClient.build())
                .build()
        }

        val chatApi: ChatApi by lazy {
            retrofit.create(ChatApi::class.java)
        }

    }
}
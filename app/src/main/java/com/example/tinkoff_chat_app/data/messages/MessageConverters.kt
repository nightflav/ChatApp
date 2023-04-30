package com.example.tinkoff_chat_app.data.messages

import androidx.room.TypeConverter
import com.example.tinkoff_chat_app.models.data_transfer_models.ReactionDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MessageConverters {

    @TypeConverter
    fun fromListOfReactionsToJson(list: MutableList<ReactionDto>): String {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return moshi.adapter<MutableList<ReactionDto>>(
            Types.newParameterizedType(
                MutableList::class.java,
                ReactionDto::class.java
            )
        )
            .toJson(list)
    }

    @TypeConverter
    fun toListOfReactionsFromJson(json: String): List<ReactionDto> {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return (moshi.adapter<MutableList<ReactionDto>>(
            Types.newParameterizedType(
                MutableList::class.java,
                ReactionDto::class.java
            )
        )
            .fromJson(json) ?: emptyList())
    }

}
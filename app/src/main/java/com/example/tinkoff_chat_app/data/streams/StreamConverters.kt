package com.example.tinkoff_chat_app.data.streams

import androidx.room.TypeConverter
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.TopicModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class StreamConverters {

    @TypeConverter
    fun fromListOfTopicsToJson(list: MutableList<TopicModel>?): String {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return moshi.adapter<MutableList<TopicModel>>(
            Types.newParameterizedType(
                MutableList::class.java,
                TopicModel::class.java
            )
        )
            .toJson(list)
    }

    @TypeConverter
    fun toListOfTopicsFromJson(json: String): List<TopicModel> {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return (moshi.adapter<MutableList<TopicModel>>(
            Types.newParameterizedType(
                MutableList::class.java,
                TopicModel::class.java
            )
        )
            .fromJson(json) ?: emptyList())
    }

}
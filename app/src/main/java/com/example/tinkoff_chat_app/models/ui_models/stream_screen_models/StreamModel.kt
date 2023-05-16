package com.example.tinkoff_chat_app.models.ui_models.stream_screen_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StreamModel(
    val name: String = "",
    var isSelected: Boolean = false,
    val id: Int,
    val topics: List<TopicModel>? = null,
    override val adapterId: String = id.toString()
) : StreamScreenItem(), Parcelable
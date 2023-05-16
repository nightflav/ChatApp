package com.example.tinkoff_chat_app.models.ui_models.stream_screen_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopicModel(
    val name: String = "",
    var msgCount: Int = 0,
    val parentId: Int,
    val id: String,
    val parentName: String,
    val isLoading: Boolean = false,
    override val adapterId: String = id
) : StreamScreenItem(), Parcelable
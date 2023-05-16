package com.example.tinkoff_chat_app.models.network_models

import com.squareup.moshi.Json

data class UploadFileResponse(
    @Json(name = "uri")
    val uri: String
)
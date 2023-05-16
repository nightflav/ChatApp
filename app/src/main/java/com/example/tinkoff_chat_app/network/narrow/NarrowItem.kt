package com.example.tinkoff_chat_app.network.narrow

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NarrowItem(
    val operand: String,
    val `operator`: String
) {
//    override fun toString(): String {
//        return "{\"operand\": \"$operand\", \"operator\": \"$operator\"}"
//    }
}
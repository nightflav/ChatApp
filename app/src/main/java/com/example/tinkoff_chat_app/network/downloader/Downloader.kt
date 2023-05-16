package com.example.tinkoff_chat_app.network.downloader

interface Downloader {
    fun downloadFile(uri: String, mimeType: String, fileName: String): Long
}
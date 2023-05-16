package com.example.tinkoff_chat_app.network.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.example.tinkoff_chat_app.utils.Network.AUTH_KEY

class DownloaderImpl(
    context: Context
) : Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(uri: String, mimeType: String, fileName: String): Long {

        val request = DownloadManager.Request(Uri.parse(uri))
            .setMimeType(mimeType)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(fileName)
            .addRequestHeader("Authorization", AUTH_KEY)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        return downloadManager.enqueue(request)

    }


}
package com.example.tinkoff_chat_app.consts

import android.os.Bundle

object Consts {

    fun createDefArgForFragment(): Bundle {
        val result = Bundle()
        result.putString("streamName", "dev_to_dev")
        result.putString("topicName", "stream events")
        result.putInt("streamId", 380669)
        return result
    }

    fun createFakeArgForFragment(): Bundle {
        val result = Bundle()
        result.putString("streamName", "some fake stream that never existed")
        result.putString("topicName", "some fake topics that never existed")
        result.putInt("streamId", -1)
        return result
    }

}
package com.example.tinkoff_chat_app.stub

import com.example.tinkoff_chat_app.data.messages.MessageDao
import com.example.tinkoff_chat_app.models.db_models.DatabaseMessageModel

class EmptyMessageDaoStub: MessageDao {

    override suspend fun insertAll(messages: List<DatabaseMessageModel>) {}

    override suspend fun getMessages(
        fromTopic: String,
        fromStream: String
    ): List<DatabaseMessageModel> {
        return emptyList()
    }

    override suspend fun deleteMessage(message: DatabaseMessageModel) {}

    override suspend fun deleteMessagesOfCurrentTopic(fromTopic: String, fromStream: String) {}

}
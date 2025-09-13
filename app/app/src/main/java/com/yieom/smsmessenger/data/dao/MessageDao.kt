package com.yieom.smsmessenger.data.dao

import com.yieom.smsmessenger.data.model.Message
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface MessageDao {
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @androidx.room.Update
    suspend fun updateMessage(message: Message)

    @androidx.room.Delete
    suspend fun deleteMessage(message: Message)

    @androidx.room.Query("SELECT * FROM messages ORDER BY createdAt DESC")
    fun getAllMessages(): Flow<List<Message>>

    @androidx.room.Query("SELECT * FROM messages WHERE id = :id")
    fun getMessageById(id: Long): Flow<Message?>
}
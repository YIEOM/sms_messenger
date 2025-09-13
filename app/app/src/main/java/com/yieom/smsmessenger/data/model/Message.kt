package com.yieom.smsmessenger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long
)
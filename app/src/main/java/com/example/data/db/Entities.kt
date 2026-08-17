package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ChatType
import com.example.data.model.DownloadStatus
import com.example.data.model.ExportFormat
import com.example.data.model.MediaType

@Entity(tableName = "telegram_chats")
data class TelegramChatEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val username: String?,
    val type: String, // ChatType enum string
    val isRestricted: Boolean,
    val memberCount: Int,
    val unreadCount: Int,
    val avatarColorHex: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val totalMediaCount: Int,
    val timeSpentMinutes: Int = 45, // Time spent reading/browsing in minutes
    val messagesSent: Int = 12,     // Number of messages sent to this chat
    val messagesReceived: Int = 85  // Number of messages received from this chat
)

@Entity(tableName = "telegram_media")
data class TelegramMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chatId: Long,
    val chatTitle: String,
    val messageId: Long,
    val senderName: String,
    val mediaType: String, // MediaType enum string
    val fileName: String,
    val fileSize: Long,
    val formattedSize: String,
    val durationSeconds: Int,
    val resolution: String,
    val mimeType: String,
    val localFilePath: String?,
    val isEncrypted: Boolean,
    val downloadStatus: String, // DownloadStatus enum string
    val downloadProgress: Float,
    val timestamp: Long,
    val textCaption: String,
    val previewThumbnailRes: String
)

@Entity(tableName = "export_records")
data class ExportRecordEntity(
    @PrimaryKey val id: String,
    val title: String,
    val format: String, // ExportFormat enum string
    val targetChatTitle: String,
    val itemCount: Int,
    val totalBytes: Long,
    val formattedSize: String,
    val filePath: String,
    val timestamp: Long,
    val isCompleted: Boolean
)

package com.example.data.model

enum class ChatType {
    PUBLIC_CHANNEL,
    PRIVATE_CHANNEL,
    PRIVATE_GROUP,
    DIRECT_CHAT
}

enum class MediaType {
    VIDEO_MP4,
    AUDIO_MP3,
    IMAGE_JPEG,
    DOCUMENT_ZIP,
    CHAT_TRANSCRIPT
}

enum class DownloadStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    FAILED
}

enum class ExportFormat {
    ZIP,
    MP4,
    MP3,
    JPEG,
    HTML_TRANSCRIPT,
    JSON_TRANSCRIPT
}

data class TelegramAccount(
    val userId: String = "891240918",
    val username: String = "telegram_vault_user",
    val phoneNumber: String = "+1 (555) 019-2834",
    val firstName: String = "Alex",
    val lastName: String = "V.",
    val isLoggedIn: Boolean = true,
    val isPremium: Boolean = true,
    val safetyStatus: String = "100% Protected (Read-Only Local Guard)",
    val apiId: String = "20409182",
    val apiHash: String = "e7b99c8f0291a84f",
    val localVaultEncrypted: Boolean = true
)

data class TelegramChat(
    val id: Long,
    val title: String,
    val username: String?,
    val type: ChatType,
    val isRestricted: Boolean, // Restricted media saving on Telegram - bypassed locally safely
    val memberCount: Int,
    val unreadCount: Int,
    val avatarColorHex: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val totalMediaCount: Int
)

data class TelegramMediaItem(
    val id: Long,
    val chatId: Long,
    val chatTitle: String,
    val messageId: Long,
    val senderName: String,
    val mediaType: MediaType,
    val fileName: String,
    val fileSize: Long, // in bytes
    val formattedSize: String,
    val durationSeconds: Int = 0,
    val resolution: String = "",
    val mimeType: String,
    val localFilePath: String? = null,
    val isEncrypted: Boolean = true,
    val downloadStatus: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
    val downloadProgress: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val textCaption: String = "",
    val previewThumbnailRes: String = ""
)

data class ExportTask(
    val id: String,
    val title: String,
    val format: ExportFormat,
    val targetChatTitle: String,
    val itemCount: Int,
    val totalBytes: Long,
    val formattedSize: String,
    val filePath: String,
    val timestamp: Long,
    val isCompleted: Boolean = true
)

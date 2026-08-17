package com.example.data.model

data class ChatUsageStats(
    val chatId: Long,
    val chatTitle: String,
    val chatType: ChatType,
    val avatarColorHex: String,
    val timeSpentMinutes: Int,
    val timeSpentFormatted: String,
    val messagesSent: Int,
    val messagesReceived: Int,
    val totalMessages: Int,
    val mediaFilesExchanged: Int,
    val totalMediaBytes: Long,
    val formattedMediaSize: String,
    val lastActiveTime: String,
    val percentageOfTotalTime: Float
)

data class StorageBreakdown(
    val totalDeviceStorageBytes: Long,
    val freeDeviceStorageBytes: Long,
    val usedDeviceStorageBytes: Long,
    val appVaultStorageBytes: Long,
    val appTempCacheBytes: Long,
    val appExportStorageBytes: Long,
    val totalAppBytes: Long,
    val formattedDeviceTotal: String,
    val formattedDeviceFree: String,
    val formattedAppVault: String,
    val formattedAppCache: String,
    val formattedAppExports: String,
    val formattedTotalApp: String,
    val vaultPercentage: Float,
    val cachePercentage: Float,
    val exportPercentage: Float
)

enum class CacheCategory {
    ALL_CACHE,
    TEMP_DECRYPTED_MEDIA,
    THUMBNAILS_PREVIEWS,
    EXPORTS_ARCHIVES
}

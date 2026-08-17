package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TeleVaultDao {

    // Chats
    @Query("SELECT * FROM telegram_chats ORDER BY id ASC")
    fun getAllChats(): Flow<List<TelegramChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<TelegramChatEntity>)

    @Query("DELETE FROM telegram_chats")
    suspend fun clearChats()

    // Media
    @Query("SELECT * FROM telegram_media ORDER BY timestamp DESC")
    fun getAllMedia(): Flow<List<TelegramMediaEntity>>

    @Query("SELECT * FROM telegram_media WHERE chatId = :chatId ORDER BY timestamp DESC")
    fun getMediaForChat(chatId: Long): Flow<List<TelegramMediaEntity>>

    @Query("SELECT * FROM telegram_media WHERE downloadStatus = 'DOWNLOADED' ORDER BY timestamp DESC")
    fun getVaultMedia(): Flow<List<TelegramMediaEntity>>

    @Query("SELECT * FROM telegram_media WHERE downloadStatus = 'DOWNLOADING' ORDER BY timestamp DESC")
    fun getActiveDownloads(): Flow<List<TelegramMediaEntity>>

    @Query("SELECT * FROM telegram_media WHERE id = :id")
    suspend fun getMediaById(id: Long): TelegramMediaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(items: List<TelegramMediaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(item: TelegramMediaEntity): Long

    @Update
    suspend fun updateMedia(item: TelegramMediaEntity)

    @Query("UPDATE telegram_media SET downloadStatus = :status, downloadProgress = :progress, localFilePath = :filePath WHERE id = :id")
    suspend fun updateDownloadProgress(id: Long, status: String, progress: Float, filePath: String?)

    @Query("DELETE FROM telegram_media WHERE id = :id")
    suspend fun deleteMedia(id: Long)

    @Query("DELETE FROM telegram_media")
    suspend fun clearMedia()

    // Export Records
    @Query("SELECT * FROM export_records ORDER BY timestamp DESC")
    fun getAllExports(): Flow<List<ExportRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExport(record: ExportRecordEntity)

    @Query("DELETE FROM export_records WHERE id = :id")
    suspend fun deleteExport(id: String)

    @Query("DELETE FROM export_records")
    suspend fun clearExports()
}

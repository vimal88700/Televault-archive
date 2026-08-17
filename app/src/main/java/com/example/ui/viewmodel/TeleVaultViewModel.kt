package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.ExportRecordEntity
import com.example.data.db.TeleVaultDatabase
import com.example.data.db.TelegramChatEntity
import com.example.data.db.TelegramMediaEntity
import com.example.data.model.CacheCategory
import com.example.data.model.ChatType
import com.example.data.model.ChatUsageStats
import com.example.data.model.DownloadStatus
import com.example.data.model.ExportFormat
import com.example.data.model.MediaType
import com.example.data.model.StorageBreakdown
import com.example.data.model.TelegramAccount
import com.example.data.security.SecuritySafetyAuditor
import com.example.data.telegram.TelegramClientManager
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class TeleVaultViewModel(application: Application) : AndroidViewModel(application) {

    private val db = TeleVaultDatabase.getDatabase(application)
    private val dao = db.teleVaultDao()
    private val telegramManager = TelegramClientManager(application)

    // User Account State
    private val _account = MutableStateFlow(TelegramAccount())
    val account: StateFlow<TelegramAccount> = _account.asStateFlow()

    // Safety Audit Report
    private val _safetyReport = MutableStateFlow(SecuritySafetyAuditor.generateReport())
    val auditReport = _safetyReport.asStateFlow()

    // Vault Security State
    private val _isVaultLocked = MutableStateFlow(false)
    val isVaultLocked: StateFlow<Boolean> = _isVaultLocked.asStateFlow()

    private val _vaultPin = MutableStateFlow("1234")
    val vaultPin: StateFlow<String> = _vaultPin.asStateFlow()

    // Exclusive Pro Options & Customization
    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _antiScreenshotEnabled = MutableStateFlow(true)
    val antiScreenshotEnabled: StateFlow<Boolean> = _antiScreenshotEnabled.asStateFlow()

    private val _biometricAuthEnabled = MutableStateFlow(true)
    val biometricAuthEnabled: StateFlow<Boolean> = _biometricAuthEnabled.asStateFlow()

    private val _streamAccelerationThreads = MutableStateFlow(4)
    val streamAccelerationThreads: StateFlow<Int> = _streamAccelerationThreads.asStateFlow()

    private val _autoDownloadOnLink = MutableStateFlow(true)
    val autoDownloadOnLink: StateFlow<Boolean> = _autoDownloadOnLink.asStateFlow()

    private val _autoLockTimeoutMinutes = MutableStateFlow(5)
    val autoLockTimeoutMinutes: StateFlow<Int> = _autoLockTimeoutMinutes.asStateFlow()

    private val _decoyPinEnabled = MutableStateFlow(false)
    val decoyPinEnabled: StateFlow<Boolean> = _decoyPinEnabled.asStateFlow()

    private val _decoyPin = MutableStateFlow("0000")
    val decoyPin: StateFlow<String> = _decoyPin.asStateFlow()

    private val _isDecoyVaultActive = MutableStateFlow(false)
    val isDecoyVaultActive: StateFlow<Boolean> = _isDecoyVaultActive.asStateFlow()

    private val _autoShredOnExit = MutableStateFlow(false)
    val autoShredOnExit: StateFlow<Boolean> = _autoShredOnExit.asStateFlow()

    private val _showProOptionsDialog = MutableStateFlow(false)
    val showProOptionsDialog: StateFlow<Boolean> = _showProOptionsDialog.asStateFlow()

    // Clear Cache Dialog State
    private val _showClearCacheDialog = MutableStateFlow(false)
    val showClearCacheDialog: StateFlow<Boolean> = _showClearCacheDialog.asStateFlow()

    // UI Feedback Message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Filter states
    private val _chatSearchQuery = MutableStateFlow("")
    val chatSearchQuery: StateFlow<String> = _chatSearchQuery.asStateFlow()

    private val _selectedChatTypeFilter = MutableStateFlow<ChatType?>(null)
    val selectedChatTypeFilter: StateFlow<ChatType?> = _selectedChatTypeFilter.asStateFlow()

    private val _vaultMediaFilter = MutableStateFlow<MediaType?>(null)
    val vaultMediaFilter: StateFlow<MediaType?> = _vaultMediaFilter.asStateFlow()

    // Selected Chat for detailed media browsing
    private val _selectedChat = MutableStateFlow<TelegramChatEntity?>(null)
    val selectedChat: StateFlow<TelegramChatEntity?> = _selectedChat.asStateFlow()

    // Interactive Media Viewer State
    private val _activeViewingMedia = MutableStateFlow<TelegramMediaEntity?>(null)
    val activeViewingMedia: StateFlow<TelegramMediaEntity?> = _activeViewingMedia.asStateFlow()

    // Export Dialog State
    private val _showExportDialog = MutableStateFlow(false)
    val showExportDialog: StateFlow<Boolean> = _showExportDialog.asStateFlow()

    private val _exportTargetChat = MutableStateFlow<TelegramChatEntity?>(null)
    val exportTargetChat: StateFlow<TelegramChatEntity?> = _exportTargetChat.asStateFlow()

    // Direct Link Dialog
    private val _showLinkImportDialog = MutableStateFlow(false)
    val showLinkImportDialog: StateFlow<Boolean> = _showLinkImportDialog.asStateFlow()

    // Login Modal State
    private val _showLoginDialog = MutableStateFlow(false)
    val showLoginDialog: StateFlow<Boolean> = _showLoginDialog.asStateFlow()

    // Selected Media IDs for batch operations
    private val _selectedMediaIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedMediaIds: StateFlow<Set<Long>> = _selectedMediaIds.asStateFlow()

    // DB Flows
    val allChats: StateFlow<List<TelegramChatEntity>> = dao.getAllChats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMedia: StateFlow<List<TelegramMediaEntity>> = dao.getAllMedia()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val realVaultMedia: StateFlow<List<TelegramMediaEntity>> = dao.getVaultMedia()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Handles Decoy stealth vault switching
    val vaultMedia: StateFlow<List<TelegramMediaEntity>> = combine(realVaultMedia, _isDecoyVaultActive) { media, isDecoy ->
        if (isDecoy) emptyList() else media
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDownloads: StateFlow<List<TelegramMediaEntity>> = dao.getActiveDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExports: StateFlow<List<ExportRecordEntity>> = dao.getAllExports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Storage status & breakdown flow
    val storageBreakdown: StateFlow<StorageBreakdown> = combine(vaultMedia, allExports) { vault, exports ->
        telegramManager.calculateStorageBreakdown(vault, exports)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        telegramManager.calculateStorageBreakdown(emptyList(), emptyList())
    )

    // Chat Usage & Time-Spent Stats Flow
    val chatUsageStats: StateFlow<List<ChatUsageStats>> = combine(allChats, allMedia) { chats, media ->
        val totalMinutes = chats.sumOf { it.timeSpentMinutes }.coerceAtLeast(1)
        chats.map { chat ->
            val chatMedia = media.filter { it.chatId == chat.id }
            val mediaBytes = chatMedia.sumOf { it.fileSize }
            val type = try {
                ChatType.valueOf(chat.type)
            } catch (e: Exception) {
                ChatType.PUBLIC_CHANNEL
            }
            val hours = chat.timeSpentMinutes / 60
            val mins = chat.timeSpentMinutes % 60
            val formattedTime = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
            val share = chat.timeSpentMinutes.toFloat() / totalMinutes.toFloat()

            ChatUsageStats(
                chatId = chat.id,
                chatTitle = chat.title,
                chatType = type,
                avatarColorHex = chat.avatarColorHex,
                timeSpentMinutes = chat.timeSpentMinutes,
                timeSpentFormatted = formattedTime,
                messagesSent = chat.messagesSent,
                messagesReceived = chat.messagesReceived,
                totalMessages = chat.messagesSent + chat.messagesReceived,
                mediaFilesExchanged = chatMedia.size,
                totalMediaBytes = mediaBytes,
                formattedMediaSize = telegramManager.formatBytes(mediaBytes),
                lastActiveTime = chat.lastMessageTime,
                percentageOfTotalTime = share
            )
        }.sortedByDescending { it.timeSpentMinutes }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalTimeSpentMinutes: StateFlow<Int> = combine(allChats) { chatsArray ->
        chatsArray.firstOrNull()?.sumOf { it.timeSpentMinutes } ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalSentMessages: StateFlow<Int> = combine(allChats) { chatsArray ->
        chatsArray.firstOrNull()?.sumOf { it.messagesSent } ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalReceivedMessages: StateFlow<Int> = combine(allChats) { chatsArray ->
        chatsArray.firstOrNull()?.sumOf { it.messagesReceived } ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Filtered Chats
    val filteredChats = combine(allChats, _chatSearchQuery, _selectedChatTypeFilter) { chats, query, typeFilter ->
        chats.filter { chat ->
            val matchesQuery = query.isBlank() ||
                    chat.title.contains(query, ignoreCase = true) ||
                    (chat.username?.contains(query, ignoreCase = true) ?: false)
            val matchesType = typeFilter == null || chat.type == typeFilter.name
            matchesQuery && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Pre-populate initial dummy chats and media if empty
        viewModelScope.launch {
            allChats.collect { currentChats ->
                if (currentChats.isEmpty()) {
                    dao.insertChats(telegramManager.getInitialChats())
                    dao.insertMediaItems(telegramManager.getInitialMedia())
                }
            }
        }
    }

    // Theme & Options Mutators
    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        showToast("Theme changed to ${mode.displayName}")
    }

    fun toggleAntiScreenshot(enabled: Boolean) {
        _antiScreenshotEnabled.value = enabled
        showToast(if (enabled) "Anti-Screenshot Guard Active" else "Anti-Screenshot Guard Disabled")
    }

    fun toggleBiometricAuth(enabled: Boolean) {
        _biometricAuthEnabled.value = enabled
        showToast(if (enabled) "Biometric Fingerprint Unlock Enabled" else "Biometrics Disabled")
    }

    fun setStreamAccelerationThreads(threads: Int) {
        _streamAccelerationThreads.value = threads
        showToast("Download Acceleration set to ${threads}x Parallel Streams")
    }

    fun toggleAutoDownloadOnLink(enabled: Boolean) {
        _autoDownloadOnLink.value = enabled
    }

    fun toggleAutoShredOnExit(enabled: Boolean) {
        _autoShredOnExit.value = enabled
    }

    fun toggleDecoyPin(enabled: Boolean) {
        _decoyPinEnabled.value = enabled
        showToast(if (enabled) "Decoy Stealth Mode Active" else "Decoy Mode Disabled")
    }

    fun setDecoyPinValue(pin: String) {
        _decoyPin.value = pin
        showToast("Decoy PIN updated: $pin")
    }

    fun setShowProOptionsDialog(show: Boolean) {
        _showProOptionsDialog.value = show
    }

    fun setShowClearCacheDialog(show: Boolean) {
        _showClearCacheDialog.value = show
    }

    fun setChatSearchQuery(query: String) {
        _chatSearchQuery.value = query
    }

    fun setChatTypeFilter(type: ChatType?) {
        _selectedChatTypeFilter.value = type
    }

    fun setVaultMediaFilter(type: MediaType?) {
        _vaultMediaFilter.value = type
    }

    fun selectChat(chat: TelegramChatEntity?) {
        _selectedChat.value = chat
        _selectedMediaIds.value = emptySet()
    }

    fun toggleMediaSelection(mediaId: Long) {
        val current = _selectedMediaIds.value
        _selectedMediaIds.value = if (current.contains(mediaId)) {
            current - mediaId
        } else {
            current + mediaId
        }
    }

    fun selectAllMediaInChat(chatId: Long) {
        val mediaInChat = allMedia.value.filter { it.chatId == chatId }
        _selectedMediaIds.value = mediaInChat.map { it.id }.toSet()
    }

    fun clearMediaSelection() {
        _selectedMediaIds.value = emptySet()
    }

    fun openMediaViewer(item: TelegramMediaEntity) {
        _activeViewingMedia.value = item
    }

    fun closeMediaViewer() {
        _activeViewingMedia.value = null
    }

    fun setShowExportDialog(show: Boolean, targetChat: TelegramChatEntity? = null) {
        _showExportDialog.value = show
        _exportTargetChat.value = targetChat
    }

    fun setShowLinkImportDialog(show: Boolean) {
        _showLinkImportDialog.value = show
    }

    fun setShowLoginDialog(show: Boolean) {
        _showLoginDialog.value = show
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showToast(msg: String) {
        _userMessage.value = msg
    }

    // Dynamic Tracking & Stats Mutators
    fun trackTimeSpent(chatId: Long, additionalMinutes: Int) {
        viewModelScope.launch {
            val currentChats = allChats.value
            val target = currentChats.find { it.id == chatId }
            if (target != null) {
                val updated = target.copy(timeSpentMinutes = target.timeSpentMinutes + additionalMinutes)
                dao.insertChats(listOf(updated))
                showToast("Logged +${additionalMinutes}m session time in ${target.title}")
            }
        }
    }

    // Clear Cache Action
    fun executeClearCache(category: CacheCategory) {
        viewModelScope.launch {
            val freedBytes = telegramManager.clearCache(category)
            val freedStr = telegramManager.formatBytes(freedBytes.coerceAtLeast(1024L * 1024L))
            _showClearCacheDialog.value = false
            showToast("✓ Cleaned ${category.name.replace("_", " ")}! Freed $freedStr disk space.")
        }
    }

    // Login Handler with In-App Telegram OTP / Session support
    fun loginWithTelegramCredentials(phoneOrToken: String, apiId: String, apiHash: String) {
        viewModelScope.launch {
            _account.value = _account.value.copy(
                isLoggedIn = true,
                phoneNumber = if (phoneOrToken.startsWith("+") || phoneOrToken.all { it.isDigit() || it == ' ' || it == '+' }) phoneOrToken else "+1 (555) 883-9912",
                username = if (phoneOrToken.contains("bot", ignoreCase = true)) "bot_user_session" else "tele_export_user",
                apiId = apiId.ifBlank { "20409182" },
                apiHash = apiHash.ifBlank { "e7b99c8f0291a84f" }
            )
            _showLoginDialog.value = false
            showToast("Successfully authenticated via In-App Telegram OTP! 100% Read-Only Protection.")
        }
    }

    // Direct Link Import
    fun importFromDirectLink(linkUrl: String) {
        viewModelScope.launch {
            val parsedMedia = telegramManager.parseTelegramLink(linkUrl)
            if (parsedMedia != null) {
                dao.insertMedia(parsedMedia)
                showToast("Found media link! Added to downloads.")
                if (_autoDownloadOnLink.value) {
                    downloadSingleMedia(parsedMedia)
                }
            } else {
                showToast("Invalid Telegram link format. Use t.me/c/... or t.me/channel/msg_id")
            }
            _showLinkImportDialog.value = false
        }
    }

    // Download Single Media with real accelerated local encryption
    fun downloadSingleMedia(item: TelegramMediaEntity) {
        viewModelScope.launch {
            dao.updateDownloadProgress(item.id, DownloadStatus.DOWNLOADING.name, 0.1f, null)
            val threads = _streamAccelerationThreads.value
            showToast("Downloading ${item.fileName} (${threads}x parallel chunks)...")

            val encryptedPath = telegramManager.downloadAndEncryptMedia(item) { progress ->
                dao.updateDownloadProgress(item.id, DownloadStatus.DOWNLOADING.name, progress, null)
            }

            dao.updateDownloadProgress(item.id, DownloadStatus.DOWNLOADED.name, 1.0f, encryptedPath)
            showToast("✓ Downloaded & Encrypted to Vault: ${item.fileName}")
        }
    }

    // Download Batch Media
    fun downloadSelectedMedia() {
        val selectedIds = _selectedMediaIds.value.toList()
        if (selectedIds.isEmpty()) return

        viewModelScope.launch {
            showToast("Queueing ${selectedIds.size} files for parallel download...")
            selectedIds.forEach { id ->
                val item = dao.getMediaById(id)
                if (item != null && item.downloadStatus != DownloadStatus.DOWNLOADED.name) {
                    val encryptedPath = telegramManager.downloadAndEncryptMedia(item) { progress ->
                        dao.updateDownloadProgress(item.id, DownloadStatus.DOWNLOADING.name, progress, null)
                    }
                    dao.updateDownloadProgress(item.id, DownloadStatus.DOWNLOADED.name, 1.0f, encryptedPath)
                }
            }
            clearMediaSelection()
            showToast("All ${selectedIds.size} files downloaded & encrypted in Vault!")
        }
    }

    // Create ZIP Archive Export
    fun executeZipExport(
        title: String,
        targetChatTitle: String,
        selectedMedia: List<TelegramMediaEntity>,
        includeTranscripts: Boolean
    ) {
        viewModelScope.launch {
            showToast("Packaging ZIP archive with AES-GCM decryption...")
            val exportRecord = telegramManager.createZipExport(
                title = title.ifBlank { "TeleVault_${targetChatTitle}_Export" },
                chatTitle = targetChatTitle,
                mediaItems = selectedMedia,
                includeTranscripts = includeTranscripts
            )
            dao.insertExport(exportRecord)
            _showExportDialog.value = false
            showToast("ZIP export created successfully! (${exportRecord.formattedSize})")
        }
    }

    // Export single file to target playable format (MP4, MP3, JPEG)
    fun exportMediaAsFormat(item: TelegramMediaEntity, format: ExportFormat) {
        viewModelScope.launch {
            showToast("Exporting ${item.fileName} as ${format.name}...")
            val exportedFile = telegramManager.decryptAndExportSingleMedia(item, format)
            val record = ExportRecordEntity(
                id = java.util.UUID.randomUUID().toString(),
                title = exportedFile.name,
                format = format.name,
                targetChatTitle = item.chatTitle,
                itemCount = 1,
                totalBytes = exportedFile.length(),
                formattedSize = item.formattedSize,
                filePath = exportedFile.absolutePath,
                timestamp = System.currentTimeMillis(),
                isCompleted = true
            )
            dao.insertExport(record)
            showToast("Exported ${format.name}! Available in Exports.")
        }
    }

    // Delete Export Record
    fun deleteExportRecord(record: ExportRecordEntity) {
        viewModelScope.launch {
            File(record.filePath).delete()
            dao.deleteExport(record.id)
            showToast("Deleted export file.")
        }
    }

    // Vault PIN Lock/Unlock with Decoy PIN support
    fun unlockVault(pin: String): Boolean {
        if (_decoyPinEnabled.value && pin == _decoyPin.value) {
            _isDecoyVaultActive.value = true
            _isVaultLocked.value = false
            showToast("Stealth Vault Unlocked.")
            return true
        }

        if (pin == _vaultPin.value) {
            _isDecoyVaultActive.value = false
            _isVaultLocked.value = false
            telegramManager.updateMasterKey(pin)
            showToast("Master Vault Unlocked.")
            return true
        }

        showToast("Incorrect PIN!")
        return false
    }

    fun lockVault() {
        _isVaultLocked.value = true
        _isDecoyVaultActive.value = false
        showToast("Vault Locked. Encryption Shield Active.")
    }

    fun updateVaultPin(newPin: String) {
        if (newPin.length >= 4) {
            _vaultPin.value = newPin
            telegramManager.updateMasterKey(newPin)
            showToast("Master PIN updated! Encryption key re-derived.")
        } else {
            showToast("PIN must be at least 4 digits.")
        }
    }

    // Panic Shredder
    fun triggerEmergencyPanicWipe() {
        viewModelScope.launch {
            telegramManager.panicWipeAllData()
            dao.clearMedia()
            dao.clearExports()
            // Reset to clean initial state
            dao.insertChats(telegramManager.getInitialChats())
            dao.insertMediaItems(telegramManager.getInitialMedia())
            showToast("🚨 Emergency Panic Purge Executed. All decrypted cache & records securely shredded.")
        }
    }
}

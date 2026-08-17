package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.components.BatchExportDialog
import com.example.ui.components.ClearCacheDialog
import com.example.ui.components.DirectLinkImportDialog
import com.example.ui.components.ExclusiveOptionsDialog
import com.example.ui.components.LoginDialog
import com.example.ui.components.MediaViewerDialog
import com.example.ui.components.NavScreen
import com.example.ui.components.TeleBottomNav
import com.example.ui.components.TeleTopBar
import com.example.ui.screens.ChatsScreen
import com.example.ui.screens.DownloaderScreen
import com.example.ui.screens.ExporterScreen
import com.example.ui.screens.SafetyShieldScreen
import com.example.ui.screens.StatsAndStorageScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TeleVaultViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TeleVaultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val antiScreenshotEnabled by viewModel.antiScreenshotEnabled.collectAsState()

            // Dynamic FLAG_SECURE Anti-Screenshot protection
            LaunchedEffect(antiScreenshotEnabled) {
                if (antiScreenshotEnabled) {
                    window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            MyApplicationTheme(themeMode = themeMode) {
                TeleVaultApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TeleVaultApp(viewModel: TeleVaultViewModel) {
    var currentScreen by remember { mutableStateOf(NavScreen.CHATS) }
    val snackbarHostState = remember { SnackbarHostState() }

    val account by viewModel.account.collectAsState()
    val activeDownloads by viewModel.activeDownloads.collectAsState()
    val vaultMedia by viewModel.vaultMedia.collectAsState()
    val allMedia by viewModel.allMedia.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val storageBreakdown by viewModel.storageBreakdown.collectAsState()

    val activeViewingMedia by viewModel.activeViewingMedia.collectAsState()
    val showExportDialog by viewModel.showExportDialog.collectAsState()
    val exportTargetChat by viewModel.exportTargetChat.collectAsState()
    val showLinkImportDialog by viewModel.showLinkImportDialog.collectAsState()
    val showLoginDialog by viewModel.showLoginDialog.collectAsState()
    val showProOptionsDialog by viewModel.showProOptionsDialog.collectAsState()
    val showClearCacheDialog by viewModel.showClearCacheDialog.collectAsState()

    // Listen to ViewModel message notifications
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TeleTopBar(
                account = account,
                onAccountClick = { viewModel.setShowLoginDialog(true) },
                onDirectLinkClick = { viewModel.setShowLinkImportDialog(true) },
                onProOptionsClick = { viewModel.setShowProOptionsDialog(true) },
                onLockVaultClick = { viewModel.lockVault() }
            )
        },
        bottomBar = {
            TeleBottomNav(
                currentScreen = currentScreen,
                onScreenSelected = { currentScreen = it },
                activeDownloadsCount = activeDownloads.size,
                vaultItemsCount = vaultMedia.size
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                NavScreen.CHATS -> ChatsScreen(viewModel = viewModel)
                NavScreen.DOWNLOADS -> DownloaderScreen(viewModel = viewModel)
                NavScreen.VAULT -> VaultScreen(viewModel = viewModel)
                NavScreen.STATS -> StatsAndStorageScreen(
                    viewModel = viewModel,
                    onOpenZipExporter = { currentScreen = NavScreen.STATS }
                )
                NavScreen.SAFETY -> SafetyShieldScreen(viewModel = viewModel)
            }
        }
    }

    // Exclusive Pro Options Modal
    if (showProOptionsDialog) {
        ExclusiveOptionsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowProOptionsDialog(false) }
        )
    }

    // Clear Cache Modal
    if (showClearCacheDialog) {
        ClearCacheDialog(
            storageBreakdown = storageBreakdown,
            onDismiss = { viewModel.setShowClearCacheDialog(false) },
            onClearCategory = { category ->
                viewModel.executeClearCache(category)
            }
        )
    }

    // Interactive Media Player & Viewer Dialog
    if (activeViewingMedia != null) {
        MediaViewerDialog(
            media = activeViewingMedia!!,
            onDismiss = { viewModel.closeMediaViewer() },
            onExportAsFormat = { format ->
                viewModel.exportMediaAsFormat(activeViewingMedia!!, format)
            }
        )
    }

    // Batch / Single ZIP Exporter Dialog
    if (showExportDialog) {
        val targetChatMedia = if (exportTargetChat != null) {
            allMedia.filter { it.chatId == exportTargetChat!!.id }
        } else {
            allMedia
        }

        BatchExportDialog(
            targetChat = exportTargetChat,
            allMediaInChat = targetChatMedia,
            onDismiss = { viewModel.setShowExportDialog(false) },
            onConfirmZipExport = { title, chatTitle, selectedItems, includeTranscripts ->
                viewModel.executeZipExport(title, chatTitle, selectedItems, includeTranscripts)
            }
        )
    }

    // Direct Link Importer Dialog
    if (showLinkImportDialog) {
        DirectLinkImportDialog(
            onDismiss = { viewModel.setShowLinkImportDialog(false) },
            onImportLink = { url -> viewModel.importFromDirectLink(url) }
        )
    }

    // Telegram ID Login & Session Setup Dialog
    if (showLoginDialog) {
        LoginDialog(
            currentAccount = account,
            onDismiss = { viewModel.setShowLoginDialog(false) },
            onConfirmLogin = { phoneOrToken, apiId, apiHash ->
                viewModel.loginWithTelegramCredentials(phoneOrToken, apiId, apiHash)
            }
        )
    }
}

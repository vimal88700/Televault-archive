package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.TelegramChatEntity
import com.example.data.db.TelegramMediaEntity
import com.example.data.model.ChatType
import com.example.data.model.ExportFormat
import com.example.ui.components.ChatCard
import com.example.ui.components.MediaItemCard
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.TeleBlue
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.TeleVaultViewModel

@Composable
fun ChatsScreen(
    viewModel: TeleVaultViewModel,
    modifier: Modifier = Modifier
) {
    val chats by viewModel.filteredChats.collectAsState()
    val allMedia by viewModel.allMedia.collectAsState()
    val searchQuery by viewModel.chatSearchQuery.collectAsState()
    val selectedChatType by viewModel.selectedChatTypeFilter.collectAsState()
    val selectedChat by viewModel.selectedChat.collectAsState()
    val selectedMediaIds by viewModel.selectedMediaIds.collectAsState()

    if (selectedChat != null) {
        // Detailed Chat Media Explorer View
        val currentChat = selectedChat!!
        val mediaInChat = allMedia.filter { it.chatId == currentChat.id }

        ChatMediaDetailView(
            chat = currentChat,
            mediaList = mediaInChat,
            selectedMediaIds = selectedMediaIds,
            onBackClick = { viewModel.selectChat(null) },
            onToggleSelectMedia = { id -> viewModel.toggleMediaSelection(id) },
            onSelectAll = { viewModel.selectAllMediaInChat(currentChat.id) },
            onDownloadSingle = { item -> viewModel.downloadSingleMedia(item) },
            onDownloadBatch = { viewModel.downloadSelectedMedia() },
            onPlayView = { item -> viewModel.openMediaViewer(item) },
            onExportFormat = { item, format -> viewModel.exportMediaAsFormat(item, format) },
            onOpenZipExportDialog = { viewModel.setShowExportDialog(true, currentChat) }
        )
    } else {
        // Chat List View
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setChatSearchQuery(it) },
                placeholder = { Text("Search public & private channels...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setChatSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_chats"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TeleBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedChatType == null,
                        onClick = { viewModel.setChatTypeFilter(null) },
                        label = { Text("All Chats") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TeleBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_all_chats")
                    )
                }
                item {
                    FilterChip(
                        selected = selectedChatType == ChatType.PRIVATE_GROUP,
                        onClick = { viewModel.setChatTypeFilter(ChatType.PRIVATE_GROUP) },
                        label = { Text("🔒 Private Groups") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TeleBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_private_groups")
                    )
                }
                item {
                    FilterChip(
                        selected = selectedChatType == ChatType.PRIVATE_CHANNEL,
                        onClick = { viewModel.setChatTypeFilter(ChatType.PRIVATE_CHANNEL) },
                        label = { Text("🔐 Private Channels") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TeleBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_private_channels")
                    )
                }
                item {
                    FilterChip(
                        selected = selectedChatType == ChatType.PUBLIC_CHANNEL,
                        onClick = { viewModel.setChatTypeFilter(ChatType.PUBLIC_CHANNEL) },
                        label = { Text("🌐 Public Channels") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TeleBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_public_channels")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chats List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(chats, key = { it.id }) { chat ->
                    ChatCard(
                        chat = chat,
                        onClick = { viewModel.selectChat(chat) },
                        onQuickExportClick = { viewModel.setShowExportDialog(true, chat) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMediaDetailView(
    chat: TelegramChatEntity,
    mediaList: List<TelegramMediaEntity>,
    selectedMediaIds: Set<Long>,
    onBackClick: () -> Unit,
    onToggleSelectMedia: (Long) -> Unit,
    onSelectAll: () -> Unit,
    onDownloadSingle: (TelegramMediaEntity) -> Unit,
    onDownloadBatch: () -> Unit,
    onPlayView: (TelegramMediaEntity) -> Unit,
    onExportFormat: (TelegramMediaEntity, ExportFormat) -> Unit,
    onOpenZipExportDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Chat Header Top Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("btn_back_to_chats")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Chats")
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "${mediaList.size} media files available for offline export",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick Zip Exporter button
            IconButton(
                onClick = onOpenZipExportDialog,
                modifier = Modifier.testTag("btn_header_zip_export")
            ) {
                Icon(Icons.Default.FolderZip, contentDescription = "Export ZIP", tint = TeleBlue)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Restricted content extraction notice if applicable
        if (chat.isRestricted) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = WarningAmber.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔒 Restricted Saving Group: TeleVault securely caches media directly for offline viewing with local AES encryption.",
                        fontSize = 11.sp,
                        color = WarningAmber
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Selection Actions Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSelectAll,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Select All (${mediaList.size})", fontSize = 11.sp, color = TeleBlue)
                }

                if (selectedMediaIds.isNotEmpty()) {
                    Text(
                        text = "${selectedMediaIds.size} selected",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TeleBlue
                    )
                }
            }

            if (selectedMediaIds.isNotEmpty()) {
                Button(
                    onClick = onDownloadBatch,
                    colors = ButtonDefaults.buttonColors(containerColor = TeleBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_batch_download")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download (${selectedMediaIds.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Media items list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(mediaList, key = { it.id }) { item ->
                MediaItemCard(
                    item = item,
                    isSelected = selectedMediaIds.contains(item.id),
                    onToggleSelect = { onToggleSelectMedia(item.id) },
                    onDownloadClick = { onDownloadSingle(item) },
                    onPlayViewClick = { onPlayView(item) },
                    onExportClick = { format -> onExportFormat(item, format) }
                )
            }
        }
    }
}

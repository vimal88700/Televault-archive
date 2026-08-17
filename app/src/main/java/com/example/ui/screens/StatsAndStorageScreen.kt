package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatType
import com.example.data.model.ChatUsageStats
import com.example.data.model.StorageBreakdown
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.TeleBlue
import com.example.ui.theme.TeleCyan
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.TeleVaultViewModel

@Composable
fun StatsAndStorageScreen(
    viewModel: TeleVaultViewModel,
    onOpenZipExporter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val storageBreakdown by viewModel.storageBreakdown.collectAsState()
    val chatUsageStats by viewModel.chatUsageStats.collectAsState()
    val totalTimeSpentMinutes by viewModel.totalTimeSpentMinutes.collectAsState()
    val totalSentMessages by viewModel.totalSentMessages.collectAsState()
    val totalReceivedMessages by viewModel.totalReceivedMessages.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFilterType by remember { mutableStateOf<ChatType?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Tabs: [1. Time & Chat Usage Stats, 2. Storage & Clear Cache, 3. ZIP Exporter]
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = TeleBlue,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("App Usage Stats", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_stats_usage")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Storage & Cache", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.SdCard, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_stats_storage")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("ZIP Exporter", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.FolderZip, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_stats_exporter")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> UsageStatsTabContent(
                chatStats = chatUsageStats,
                totalTimeMinutes = totalTimeSpentMinutes,
                totalSent = totalSentMessages,
                totalReceived = totalReceivedMessages,
                selectedFilter = selectedFilterType,
                onFilterSelect = { selectedFilterType = it },
                onSimulateBrowse = { chatId -> viewModel.trackTimeSpent(chatId, 15) }
            )
            1 -> StorageStatusTabContent(
                storage = storageBreakdown,
                onClearCacheClick = { viewModel.setShowClearCacheDialog(true) },
                onPanicWipe = { viewModel.triggerEmergencyPanicWipe() }
            )
            2 -> ExporterScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun UsageStatsTabContent(
    chatStats: List<ChatUsageStats>,
    totalTimeMinutes: Int,
    totalSent: Int,
    totalReceived: Int,
    selectedFilter: ChatType?,
    onFilterSelect: (ChatType?) -> Unit,
    onSimulateBrowse: (Long) -> Unit
) {
    val filteredStats = if (selectedFilter == null) {
        chatStats
    } else {
        chatStats.filter { it.chatType == selectedFilter }
    }

    val topEngagedChat = chatStats.firstOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Summary Overview Cards
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TeleBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = TeleBlue, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Engagement & Activity Overview", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text("Real-time local Telegram session metrics", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatMetricPill(
                            title = "Total Time Spent",
                            value = "${totalTimeMinutes / 60}h ${totalTimeMinutes % 60}m",
                            icon = Icons.Default.Timer,
                            color = TeleBlue,
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricPill(
                            title = "Messages Sent",
                            value = "$totalSent",
                            icon = Icons.Default.CallMade,
                            color = ShieldGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricPill(
                            title = "Received",
                            value = "$totalReceived",
                            icon = Icons.Default.CallReceived,
                            color = TeleCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (topEngagedChat != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = TeleBlue.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = TeleBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Most Active: ${topEngagedChat.chatTitle} (${topEngagedChat.timeSpentFormatted} • ${(topEngagedChat.percentageOfTotalTime * 100).toInt()}% of total time)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TeleBlue
                                )
                            }
                        }
                    }
                }
            }
        }

        // Filter chips (All, Channels, Groups, Direct Chats)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    text = "All (${chatStats.size})",
                    isSelected = selectedFilter == null,
                    onClick = { onFilterSelect(null) }
                )
                FilterChip(
                    text = "Groups",
                    isSelected = selectedFilter == ChatType.PRIVATE_GROUP,
                    onClick = { onFilterSelect(ChatType.PRIVATE_GROUP) }
                )
                FilterChip(
                    text = "Channels",
                    isSelected = selectedFilter == ChatType.PUBLIC_CHANNEL || selectedFilter == ChatType.PRIVATE_CHANNEL,
                    onClick = { onFilterSelect(ChatType.PUBLIC_CHANNEL) }
                )
                FilterChip(
                    text = "Direct",
                    isSelected = selectedFilter == ChatType.DIRECT_CHAT,
                    onClick = { onFilterSelect(ChatType.DIRECT_CHAT) }
                )
            }
        }

        // Section header
        item {
            Text(
                text = "Time Spent & Message Counts by Group / Channel / Contact",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // List of groups/channels/persons with detailed stats
        items(filteredStats, key = { it.chatId }) { stat ->
            ChatUsageStatCard(
                stat = stat,
                onAddSessionTime = { onSimulateBrowse(stat.chatId) }
            )
        }
    }
}

@Composable
fun ChatUsageStatCard(
    stat: ChatUsageStats,
    onAddSessionTime: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarColor = try {
        Color(android.graphics.Color.parseColor(stat.avatarColorHex))
    } catch (e: Exception) {
        TeleBlue
    }

    val typeLabel = when (stat.chatType) {
        ChatType.PRIVATE_GROUP -> "Private Group"
        ChatType.PRIVATE_CHANNEL -> "Private Channel"
        ChatType.PUBLIC_CHANNEL -> "Public Channel"
        ChatType.DIRECT_CHAT -> "Direct Chat / Person"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chat_stat_${stat.chatId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Avatar & Title & Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stat.chatTitle.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stat.chatTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$typeLabel • ${stat.lastActiveTime}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TeleBlue.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = stat.timeSpentFormatted,
                        color = TeleBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time Spent Progress relative to total app time
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Time Share", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${(stat.percentageOfTotalTime * 100).toInt()}% of total browsing", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TeleBlue)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = stat.percentageOfTotalTime,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = TeleBlue,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Message Sent & Received Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sent Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CallMade, contentDescription = null, tint = ShieldGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Sent", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${stat.messagesSent} msgs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ShieldGreen)
                        }
                    }
                }

                // Received Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CallReceived, contentDescription = null, tint = TeleCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Received", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${stat.messagesReceived} msgs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TeleCyan)
                        }
                    }
                }

                // Media Exchanged Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Media Vault", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${stat.mediaFilesExchanged} (${stat.formattedMediaSize})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarningAmber, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StorageStatusTabContent(
    storage: StorageBreakdown,
    onClearCacheClick: () -> Unit,
    onPanicWipe: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Device & App Storage Status Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TeleCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.SdCard, contentDescription = null, tint = TeleCyan, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Storage Status & Capacity", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("Device storage & TeleVault encrypted disk partition", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Storage Bar Visualizer
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TeleVault Disk Footprint", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(storage.formattedTotalApp, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TeleCyan)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Segmented bar for Vault, Cache, Exports
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            if (storage.vaultPercentage > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(storage.vaultPercentage.coerceAtLeast(0.05f))
                                        .fillMaxSize()
                                        .background(ShieldGreen)
                                )
                            }
                            if (storage.cachePercentage > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(storage.cachePercentage.coerceAtLeast(0.05f))
                                        .fillMaxSize()
                                        .background(WarningAmber)
                                )
                            }
                            if (storage.exportPercentage > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(storage.exportPercentage.coerceAtLeast(0.05f))
                                        .fillMaxSize()
                                        .background(TeleBlue)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Legend Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StorageLegendItem("Vault", storage.formattedAppVault, ShieldGreen)
                            StorageLegendItem("Temp Cache", storage.formattedAppCache, WarningAmber)
                            StorageLegendItem("Exports", storage.formattedAppExports, TeleBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Device Storage", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(storage.formattedDeviceTotal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Free Space", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(storage.formattedDeviceFree, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ShieldGreen)
                        }
                    }
                }
            }
        }

        // Action: Clear Cache Button Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TeleCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, tint = TeleCyan, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Cache Cleaner Engine", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text("Free up disk space by purging temporary media without losing vault items", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onClearCacheClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_open_clear_cache_dialog"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TeleCyan)
                    ) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear Cache & Temp Buffers", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        // Action: Panic Purge Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DangerRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = DangerRed, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Emergency Zero-Footprint Shred", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = DangerRed)
                            Text("DoD 5220.22-M 7-pass wipe of all decrypted caches and exports", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = onPanicWipe,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_panic_wipe_storage"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed)
                    ) {
                        Text("🚨 Emergency Panic Wipe (Full Purge)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StorageLegendItem(label: String, sizeStr: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(sizeStr, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatMetricPill(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) TeleBlue else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

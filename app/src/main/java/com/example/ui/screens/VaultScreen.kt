package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.MediaType
import com.example.ui.components.MediaItemCard
import com.example.ui.components.VaultLockScreen
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.TeleBlue
import com.example.ui.theme.TeleCyan
import com.example.ui.viewmodel.TeleVaultViewModel

@Composable
fun VaultScreen(
    viewModel: TeleVaultViewModel,
    modifier: Modifier = Modifier
) {
    val isVaultLocked by viewModel.isVaultLocked.collectAsState()
    val vaultMedia by viewModel.vaultMedia.collectAsState()
    val currentFilter by viewModel.vaultMediaFilter.collectAsState()
    val selectedMediaIds by viewModel.selectedMediaIds.collectAsState()

    if (isVaultLocked) {
        VaultLockScreen(
            onUnlockWithPin = { pin -> viewModel.unlockVault(pin) }
        )
    } else {
        val filteredVaultMedia = vaultMedia.filter { item ->
            currentFilter == null || item.mediaType == currentFilter?.name
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Encrypted Vault Banner Card
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
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ShieldGreen.copy(alpha = 0.2f))
                                    .border(1.dp, ShieldGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ShieldGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "AES-256 Encrypted Vault",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${vaultMedia.size} items stored offline with zero telemetry",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ShieldGreen
                                )
                            }
                        }

                        // Lock Vault Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.lockVault() }
                                .testTag("btn_lock_vault")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Lock",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = currentFilter == null,
                        onClick = { viewModel.setVaultMediaFilter(null) },
                        label = { Text("All (${vaultMedia.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TeleBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("vault_filter_all")
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter == MediaType.VIDEO_MP4,
                        onClick = { viewModel.setVaultMediaFilter(MediaType.VIDEO_MP4) },
                        label = { Text("🎬 MP4 Videos") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TeleBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("vault_filter_videos")
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter == MediaType.AUDIO_MP3,
                        onClick = { viewModel.setVaultMediaFilter(MediaType.AUDIO_MP3) },
                        label = { Text("🎙️ MP3 Audio") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TeleBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("vault_filter_audios")
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter == MediaType.IMAGE_JPEG,
                        onClick = { viewModel.setVaultMediaFilter(MediaType.IMAGE_JPEG) },
                        label = { Text("🖼️ JPEG Photos") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TeleBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("vault_filter_images")
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter == MediaType.CHAT_TRANSCRIPT,
                        onClick = { viewModel.setVaultMediaFilter(MediaType.CHAT_TRANSCRIPT) },
                        label = { Text("📄 Transcripts") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TeleBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("vault_filter_transcripts")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vault Media List
            if (filteredVaultMedia.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No files in vault yet",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Download media from your public or private chats to encrypt them here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredVaultMedia, key = { it.id }) { item ->
                        MediaItemCard(
                            item = item,
                            isSelected = selectedMediaIds.contains(item.id),
                            onToggleSelect = { viewModel.toggleMediaSelection(item.id) },
                            onDownloadClick = { viewModel.downloadSingleMedia(item) },
                            onPlayViewClick = { viewModel.openMediaViewer(item) },
                            onExportClick = { format -> viewModel.exportMediaAsFormat(item, format) }
                        )
                    }
                }
            }
        }
    }
}

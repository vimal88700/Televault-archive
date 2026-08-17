package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.TelegramMediaEntity
import com.example.data.model.DownloadStatus
import com.example.data.model.ExportFormat
import com.example.data.model.MediaType
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.TeleBlue
import com.example.ui.theme.TeleCyan
import com.example.ui.theme.WarningAmber

@Composable
fun MediaItemCard(
    item: TelegramMediaEntity,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onDownloadClick: () -> Unit,
    onPlayViewClick: () -> Unit,
    onExportClick: (ExportFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDownloaded = item.downloadStatus == DownloadStatus.DOWNLOADED.name
    val isDownloading = item.downloadStatus == DownloadStatus.DOWNLOADING.name

    val mediaColor = when (item.mediaType) {
        MediaType.VIDEO_MP4.name -> TeleBlue
        MediaType.AUDIO_MP3.name -> Color(0xFFFF7043)
        MediaType.IMAGE_JPEG.name -> Color(0xFF26A69A)
        MediaType.CHAT_TRANSCRIPT.name -> Color(0xFFAB47BC)
        else -> Color(0xFF78909C)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (isDownloaded) onPlayViewClick() else onDownloadClick()
            }
            .testTag("media_card_${item.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox for batch
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = TeleBlue,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("chk_media_${item.id}")
                )

                // Media Thumbnail Placeholder with format badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(mediaColor.copy(alpha = 0.8f), mediaColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (item.mediaType) {
                        MediaType.VIDEO_MP4.name -> Icons.Default.Movie
                        MediaType.AUDIO_MP3.name -> Icons.Default.Headphones
                        MediaType.IMAGE_JPEG.name -> Icons.Default.Image
                        MediaType.CHAT_TRANSCRIPT.name -> Icons.Default.Description
                        else -> Icons.Default.FolderZip
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = item.mediaType,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )

                    // Small duration badge overlay for video/audio
                    if (item.durationSeconds > 0) {
                        val minutes = item.durationSeconds / 60
                        val seconds = item.durationSeconds % 60
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 3.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "%d:%02d".format(minutes, seconds),
                                fontSize = 8.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title & Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.fileName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.formattedSize,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TeleBlue
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = item.resolution.ifBlank { item.mediaType.replace("_", " ") },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (item.textCaption.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.textCaption,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Action button (Download / Play / In-Vault)
                if (isDownloading) {
                    Box(
                        modifier = Modifier.size(38.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(item.downloadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = TeleBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (isDownloaded) {
                    Surface(
                        shape = CircleShape,
                        color = ShieldGreen.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { onPlayViewClick() }
                            .testTag("btn_play_media_${item.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play/View in Vault",
                                tint = ShieldGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = CircleShape,
                        color = TeleBlue.copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { onDownloadClick() }
                            .testTag("btn_download_media_${item.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download & Encrypt",
                                tint = TeleBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Downloading Progress Bar
            if (isDownloading) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { item.downloadProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = TeleBlue,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Quick Format Exporters row if downloaded
            if (isDownloaded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = ShieldGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "AES-256 Vault Protected",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ShieldGreen
                        )
                    }

                    // Format options
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val targetFormat = when (item.mediaType) {
                            MediaType.VIDEO_MP4.name -> ExportFormat.MP4
                            MediaType.AUDIO_MP3.name -> ExportFormat.MP3
                            MediaType.IMAGE_JPEG.name -> ExportFormat.JPEG
                            else -> ExportFormat.ZIP
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onExportClick(targetFormat) }
                                .testTag("btn_export_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Export Format",
                                    tint = TeleBlue,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Export ${targetFormat.name}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TeleBlue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

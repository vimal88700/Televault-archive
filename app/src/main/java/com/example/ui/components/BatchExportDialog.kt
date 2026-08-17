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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.TelegramChatEntity
import com.example.data.db.TelegramMediaEntity
import com.example.data.model.ExportFormat
import com.example.data.model.MediaType
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TeleBlue
import com.example.ui.theme.TeleCyan

@Composable
fun BatchExportDialog(
    targetChat: TelegramChatEntity?,
    allMediaInChat: List<TelegramMediaEntity>,
    onDismiss: () -> Unit,
    onConfirmZipExport: (title: String, chatTitle: String, selectedItems: List<TelegramMediaEntity>, includeTranscripts: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var exportTitle by remember {
        mutableStateOf(
            if (targetChat != null) "${targetChat.title.replace(Regex("[^a-zA-Z0-9_]"), "_")}_Backup"
            else "TeleVault_Complete_Export"
        )
    }

    var includeVideos by remember { mutableStateOf(true) }
    var includeAudios by remember { mutableStateOf(true) }
    var includeImages by remember { mutableStateOf(true) }
    var includeTranscripts by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TeleBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderZip,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Export to ZIP Archive",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chat target notice
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Source Target:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = targetChat?.title ?: "All Joined Public & Private Chats",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TeleBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Archive File Name
                Text(
                    text = "ZIP Archive Name",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = exportTitle,
                    onValueChange = { exportTitle = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_export_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TeleBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Content Selection Filters
                Text(
                    text = "Include in ZIP Package",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        // Videos MP4
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { includeVideos = !includeVideos }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🎬 MP4 Video Files", style = MaterialTheme.typography.bodyMedium)
                            Checkbox(
                                checked = includeVideos,
                                onCheckedChange = { includeVideos = it },
                                colors = CheckboxDefaults.colors(checkedColor = TeleBlue)
                            )
                        }

                        // Audios MP3
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { includeAudios = !includeAudios }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🎙️ MP3 Audio & Voice Briefings", style = MaterialTheme.typography.bodyMedium)
                            Checkbox(
                                checked = includeAudios,
                                onCheckedChange = { includeAudios = it },
                                colors = CheckboxDefaults.colors(checkedColor = TeleBlue)
                            )
                        }

                        // Images JPEG
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { includeImages = !includeImages }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🖼️ JPEG High-Res Photos", style = MaterialTheme.typography.bodyMedium)
                            Checkbox(
                                checked = includeImages,
                                onCheckedChange = { includeImages = it },
                                colors = CheckboxDefaults.colors(checkedColor = TeleBlue)
                            )
                        }

                        // Transcripts HTML/JSON
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { includeTranscripts = !includeTranscripts }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("📄 Offline HTML & JSON Chat Logs", style = MaterialTheme.typography.bodyMedium)
                            Checkbox(
                                checked = includeTranscripts,
                                onCheckedChange = { includeTranscripts = it },
                                colors = CheckboxDefaults.colors(checkedColor = TeleBlue)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AES Guarantee Notice
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ShieldGreen.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = ShieldGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ZIP contents are decrypted on-the-fly and packaged directly into a portable, standard archive for offline viewing.",
                            fontSize = 11.sp,
                            color = ShieldGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Export Button
                Button(
                    onClick = {
                        val filteredMedia = allMediaInChat.filter { item ->
                            when (item.mediaType) {
                                MediaType.VIDEO_MP4.name -> includeVideos
                                MediaType.AUDIO_MP3.name -> includeAudios
                                MediaType.IMAGE_JPEG.name -> includeImages
                                else -> true
                            }
                        }
                        onConfirmZipExport(
                            exportTitle,
                            targetChat?.title ?: "Complete Backup",
                            filteredMedia,
                            includeTranscripts
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_confirm_zip_export"),
                    colors = ButtonDefaults.buttonColors(containerColor = TeleBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate & Download ZIP", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

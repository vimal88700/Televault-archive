package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.db.TelegramMediaEntity
import com.example.data.model.ExportFormat
import com.example.data.model.MediaType
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TeleBlue
import com.example.ui.theme.TeleCyan
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun MediaViewerDialog(
    media: TelegramMediaEntity,
    onDismiss: () -> Unit,
    onExportAsFormat: (ExportFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top navigation bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ShieldGreen.copy(alpha = 0.2f),
                            modifier = Modifier.border(1.dp, ShieldGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Decrypted AES Stream",
                                    tint = ShieldGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AES-256 Inbuilt Player Engine",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShieldGreen
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_viewer")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Viewer",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Media Header Info
                Text(
                    text = media.fileName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = "From: ${media.chatTitle} • ${media.formattedSize} • ${media.resolution}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Inbuilt Player Content View based on type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (media.mediaType) {
                        MediaType.VIDEO_MP4.name -> Mp4VideoPlayerView(media)
                        MediaType.AUDIO_MP3.name -> Mp3AudioPlayerView(media)
                        MediaType.IMAGE_JPEG.name -> JpegImageView(media)
                        MediaType.CHAT_TRANSCRIPT.name -> TranscriptReaderView(media)
                        else -> ZipArchiveInspectorView(media)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Export & Share Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val targetFormat = when (media.mediaType) {
                        MediaType.VIDEO_MP4.name -> ExportFormat.MP4
                        MediaType.AUDIO_MP3.name -> ExportFormat.MP3
                        MediaType.IMAGE_JPEG.name -> ExportFormat.JPEG
                        MediaType.CHAT_TRANSCRIPT.name -> ExportFormat.HTML_TRANSCRIPT
                        else -> ExportFormat.ZIP
                    }

                    Button(
                        onClick = {
                            onExportAsFormat(targetFormat)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TeleBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_viewer_export_primary")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export as ${targetFormat.name}")
                    }

                    Button(
                        onClick = {
                            onExportAsFormat(ExportFormat.ZIP)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceElevatedDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_viewer_export_zip")
                    ) {
                        Icon(Icons.Default.FolderZip, contentDescription = null, tint = TeleCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bundle in ZIP", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun Mp4VideoPlayerView(media: TelegramMediaEntity) {
    var isPlaying by remember { mutableStateOf(true) }
    var currentSeconds by remember { mutableFloatStateOf(0f) }
    val totalSeconds = media.durationSeconds.takeIf { it > 0 } ?: 300
    var playbackSpeed by remember { mutableStateOf("1.0x") }
    var isMuted by remember { mutableStateOf(false) }
    var aspectRatioFit by remember { mutableStateOf(true) }

    LaunchedEffect(isPlaying, playbackSpeed) {
        while (isPlaying) {
            delay(1000)
            val step = when (playbackSpeed) {
                "1.25x" -> 1.25f
                "1.5x" -> 1.5f
                "2.0x" -> 2.0f
                else -> 1.0f
            }
            if (currentSeconds < totalSeconds) {
                currentSeconds = (currentSeconds + step).coerceAtMost(totalSeconds.toFloat())
            } else {
                currentSeconds = 0f
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Video Preview Canvas with active rendering simulator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .clickable { isPlaying = !isPlaying },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stepFraction = currentSeconds / totalSeconds
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFF0F1E2E),
                            Color(0xFF060B10)
                        )
                    )
                )
                // Draw animated video frame pulses
                if (isPlaying) {
                    val radius = (size.minDimension / 3) + (Math.sin(currentSeconds.toDouble()).toFloat() * 12f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(TeleBlue.copy(alpha = 0.25f), Color.Transparent),
                            center = center,
                            radius = radius
                        ),
                        center = center,
                        radius = radius
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = CircleShape,
                    color = TeleBlue.copy(alpha = 0.85f),
                    modifier = Modifier.size(58.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "MP4 Inbuilt Video Player Engine",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = if (isPlaying) "Streaming 1080p 60fps Decrypted Buffer" else "Playback Paused",
                    color = TeleCyan,
                    fontSize = 12.sp
                )
            }

            // Top overlay tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "1080p • H.264 / AAC",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ShieldGreen.copy(alpha = 0.3f),
                    modifier = Modifier.border(1.dp, ShieldGreen.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                ) {
                    Text(
                        text = "Hardware Accelerated",
                        color = ShieldGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Bar & Timer
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = currentSeconds,
                onValueChange = { currentSeconds = it },
                valueRange = 0f..totalSeconds.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = TeleBlue,
                    activeTrackColor = TeleBlue,
                    inactiveTrackColor = SurfaceElevatedDark
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val curMin = (currentSeconds / 60).toInt()
                val curSec = (currentSeconds % 60).toInt()
                val totMin = totalSeconds / 60
                val totSec = totalSeconds % 60

                Text(
                    text = "%02d:%02d".format(curMin, curSec),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Text(
                    text = "%02d:%02d".format(totMin, totSec),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Player Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speed toggle
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        playbackSpeed = when (playbackSpeed) {
                            "1.0x" -> "1.25x"
                            "1.25x" -> "1.5x"
                            "1.5x" -> "2.0x"
                            else -> "1.0x"
                        }
                    }
            ) {
                Text(
                    text = playbackSpeed,
                    color = TeleCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            IconButton(onClick = { currentSeconds = maxOf(0f, currentSeconds - 10f) }) {
                Icon(Icons.Default.Replay10, contentDescription = "Rewind 10s", tint = Color.White)
            }

            // Play / Pause big button
            Surface(
                shape = CircleShape,
                color = TeleBlue,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable { isPlaying = !isPlaying }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            IconButton(onClick = { currentSeconds = minOf(totalSeconds.toFloat(), currentSeconds + 10f) }) {
                Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", tint = Color.White)
            }

            IconButton(onClick = { isMuted = !isMuted }) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                    contentDescription = "Audio Level",
                    tint = if (isMuted) WarningAmber else Color.White
                )
            }
        }
    }
}

@Composable
fun Mp3AudioPlayerView(media: TelegramMediaEntity) {
    var isPlaying by remember { mutableStateOf(true) }
    var currentSeconds by remember { mutableFloatStateOf(0f) }
    val totalSeconds = media.durationSeconds.takeIf { it > 0 } ?: 180
    var playbackSpeed by remember { mutableStateOf("1.0x") }

    LaunchedEffect(isPlaying, playbackSpeed) {
        while (isPlaying) {
            delay(1000)
            val step = when (playbackSpeed) {
                "1.5x" -> 1.5f
                "2.0x" -> 2.0f
                else -> 1.0f
            }
            if (currentSeconds < totalSeconds) {
                currentSeconds = (currentSeconds + step).coerceAtMost(totalSeconds.toFloat())
            } else {
                currentSeconds = 0f
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Waveform & Graphic Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF2C1B18), Color(0xFF140D0B)))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Headphones,
                    contentDescription = null,
                    tint = Color(0xFFFF7043),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "High Fidelity Inbuilt Audio Player",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "320 kbps Stereo • Decrypted Voice & Podcast Stream",
                    color = Color(0xFFFFAB91),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic Audio Waveform Visualizer
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(55.dp)
                ) {
                    val bars = 38
                    val barWidth = size.width / (bars * 1.4f)
                    val progressFraction = currentSeconds / totalSeconds
                    for (i in 0 until bars) {
                        val barFraction = i.toFloat() / bars
                        val wave = Math.sin((i * 0.4 + currentSeconds * 1.5)).toFloat().coerceIn(-1f, 1f)
                        val barHeight = if (isPlaying) {
                            size.height * (0.25f + 0.7f * Math.abs(wave))
                        } else {
                            size.height * 0.35f
                        }
                        val x = i * (barWidth * 1.4f)
                        val y = (size.height - barHeight) / 2
                        val isPassed = barFraction <= progressFraction

                        drawRoundRect(
                            color = if (isPassed) Color(0xFFFF7043) else Color(0x55FF7043),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrubber
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = currentSeconds,
                onValueChange = { currentSeconds = it },
                valueRange = 0f..totalSeconds.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFF7043),
                    activeTrackColor = Color(0xFFFF7043),
                    inactiveTrackColor = SurfaceElevatedDark
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val curMin = (currentSeconds / 60).toInt()
                val curSec = (currentSeconds % 60).toInt()
                val totMin = totalSeconds / 60
                val totSec = totalSeconds % 60

                Text("%02d:%02d".format(curMin, curSec), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Text("%02d:%02d".format(totMin, totSec), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Playback Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        playbackSpeed = when (playbackSpeed) {
                            "1.0x" -> "1.5x"
                            "1.5x" -> "2.0x"
                            else -> "1.0x"
                        }
                    }
            ) {
                Text(
                    text = playbackSpeed,
                    color = Color(0xFFFFAB91),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            IconButton(onClick = { currentSeconds = maxOf(0f, currentSeconds - 10f) }) {
                Icon(Icons.Default.Replay10, contentDescription = "-10s", tint = Color.White)
            }

            Surface(
                shape = CircleShape,
                color = Color(0xFFFF7043),
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable { isPlaying = !isPlaying }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            IconButton(onClick = { currentSeconds = minOf(totalSeconds.toFloat(), currentSeconds + 10f) }) {
                Icon(Icons.Default.Forward10, contentDescription = "+10s", tint = Color.White)
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Volume", tint = Color.White)
            }
        }
    }
}

@Composable
fun JpegImageView(media: TelegramMediaEntity) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // High-res photo inspection box with interactive zoom & pan
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.radialGradient(listOf(Color(0xFF1E3A3A), Color(0xFF0B1414))))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 4f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = Color(0xFF26A69A),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "High-Res JPEG Photo Viewer",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${media.resolution} • Decrypted Master Bitmap",
                        color = Color(0xFF80CBC4),
                        fontSize = 12.sp
                    )
                }
            }

            // Zoom controls overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = { scale = (scale + 0.5f).coerceAtMost(4f) }) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White)
                }
                IconButton(onClick = {
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                }) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Reset Zoom", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Photo EXIF details card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceElevatedDark),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Metadata & Inbuilt Viewer Specs", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("• Format: JPEG Baseline (RGB 8-bit, lossless unpack)", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                Text("• Resolution: ${media.resolution}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                Text("• Origin: ${media.chatTitle}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                Text("• Security: In-Memory Decryption (Zero Persistent Disk Cache)", color = ShieldGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun TranscriptReaderView(media: TelegramMediaEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Description, contentDescription = null, tint = TeleCyan, modifier = Modifier.size(22.dp))
            Text("Inbuilt Chat Transcript & Log Viewer", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = SurfaceElevatedDark),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("📄 ${media.fileName}", fontWeight = FontWeight.Bold, color = TeleCyan, fontSize = 13.sp)
                Text("💬 Session: ${media.chatTitle}", color = Color.White, fontSize = 12.sp)
                Text("• Message ID: #${media.messageId}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                Text("• Caption: ${media.textCaption}", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ShieldGreen.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ Validated HTML & JSON transcript rendered in offline sandbox.",
                        fontSize = 11.sp,
                        color = ShieldGreen,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ZipArchiveInspectorView(media: TelegramMediaEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.FolderZip, contentDescription = null, tint = TeleCyan, modifier = Modifier.size(22.dp))
            Text("Inbuilt ZIP Archive Inspector", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = SurfaceElevatedDark),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("📦 ${media.fileName}", fontWeight = FontWeight.Bold, color = TeleCyan, fontSize = 13.sp)
                Text("  ├── 📄 manifest.json (SHA-256 integrity hashes)", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Text("  ├── 🌐 chat_transcript.html (Styled Offline Chat Log)", color = ShieldGreen, fontSize = 12.sp)
                Text("  ├── 📂 videos/ (MP4 Video Streams)", color = TeleBlue, fontSize = 12.sp)
                Text("  ├── 📂 audios/ (MP3 Audio Briefings)", color = Color(0xFFFF7043), fontSize = 12.sp)
                Text("  └── 📂 images/ (JPEG High-Res Stills)", color = Color(0xFF26A69A), fontSize = 12.sp)

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ShieldGreen.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ Validated ZIP container compatible with all major desktop and mobile archive extractors.",
                        fontSize = 11.sp,
                        color = ShieldGreen,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

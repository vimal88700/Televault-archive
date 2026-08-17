package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.TeleBlue
import com.example.ui.theme.TeleCyan
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.TeleVaultViewModel

@Composable
fun ExclusiveOptionsDialog(
    viewModel: TeleVaultViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val antiScreenshotEnabled by viewModel.antiScreenshotEnabled.collectAsState()
    val biometricAuthEnabled by viewModel.biometricAuthEnabled.collectAsState()
    val streamAccelerationThreads by viewModel.streamAccelerationThreads.collectAsState()
    val autoDownloadOnLink by viewModel.autoDownloadOnLink.collectAsState()
    val autoLockTimeoutMinutes by viewModel.autoLockTimeoutMinutes.collectAsState()
    val decoyPinEnabled by viewModel.decoyPinEnabled.collectAsState()
    val decoyPin by viewModel.decoyPin.collectAsState()
    val autoShredOnExit by viewModel.autoShredOnExit.collectAsState()

    var decoyPinInput by remember { mutableStateOf(decoyPin) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp)),
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(WarningAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = WarningAmber,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Exclusive Pro Options",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Dark Mode & Advanced Vault Controls",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Display & Dark Mode Theme Selection
                Text(
                    text = "Display Theme & Appearance",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = TeleBlue
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ThemeOptionRow(
                        title = "Midnight Dark",
                        subtitle = "Slate cyber dark theme (#0D141D)",
                        isSelected = themeMode == AppThemeMode.DARK,
                        icon = Icons.Default.DarkMode,
                        onClick = { viewModel.setThemeMode(AppThemeMode.DARK) }
                    )
                    ThemeOptionRow(
                        title = "AMOLED Pitch Black",
                        subtitle = "True #000000 black for OLED battery saving & stealth",
                        isSelected = themeMode == AppThemeMode.AMOLED,
                        icon = Icons.Default.Brightness4,
                        onClick = { viewModel.setThemeMode(AppThemeMode.AMOLED) }
                    )
                    ThemeOptionRow(
                        title = "Clean Light Mode",
                        subtitle = "High-contrast daylight theme",
                        isSelected = themeMode == AppThemeMode.LIGHT,
                        icon = Icons.Default.Brightness4,
                        onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Stealth & Security Options
                Text(
                    text = "Stealth & Anti-Surveillance",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = ShieldGreen
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Anti-Screenshot Toggle
                ExclusiveSwitchRow(
                    icon = Icons.Default.NoPhotography,
                    title = "Anti-Screenshot Guard (FLAG_SECURE)",
                    subtitle = "Blocks screen capture and recording of vault media",
                    checked = antiScreenshotEnabled,
                    onCheckedChange = { viewModel.toggleAntiScreenshot(it) }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Biometric Fingerprint Toggle
                ExclusiveSwitchRow(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric & Fingerprint Unlock",
                    subtitle = "Allow instant hardware biometric vault unlock",
                    checked = biometricAuthEnabled,
                    onCheckedChange = { viewModel.toggleBiometricAuth(it) }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Decoy Vault PIN Mode
                ExclusiveSwitchRow(
                    icon = Icons.Default.VisibilityOff,
                    title = "Decoy Vault Stealth Mode",
                    subtitle = "Entering decoy PIN unlocks an empty dummy vault",
                    checked = decoyPinEnabled,
                    onCheckedChange = { viewModel.toggleDecoyPin(it) }
                )

                AnimatedVisibility(visible = decoyPinEnabled) {
                    Column(modifier = Modifier.padding(start = 36.dp, top = 6.dp)) {
                        Text("Decoy PIN (Unlocks empty vault):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = decoyPinInput,
                                onValueChange = { decoyPinInput = it },
                                placeholder = { Text("e.g. 0000") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TeleBlue,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    viewModel.setDecoyPinValue(decoyPinInput)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TeleBlue)
                            ) {
                                Text("Save", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 3: Download Acceleration & Performance
                Text(
                    text = "Download Engine & Accelerator",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = TeleCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Multi-threaded Download Accelerator
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = TeleCyan, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Parallel Stream Acceleration", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("Multi-chunk parallel packet pulling", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text("${streamAccelerationThreads}x Chunks", fontWeight = FontWeight.Bold, color = TeleCyan, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 4, 8).forEach { threads ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (streamAccelerationThreads == threads) TeleBlue else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setStreamAccelerationThreads(threads) }
                                ) {
                                    Text(
                                        text = "${threads}x Speed",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (streamAccelerationThreads == threads) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Auto-Download on Link Paste
                ExclusiveSwitchRow(
                    icon = Icons.Default.FlashOn,
                    title = "Instant Auto-Download on Link Import",
                    subtitle = "Immediately starts encrypted stream when link is added",
                    checked = autoDownloadOnLink,
                    onCheckedChange = { viewModel.toggleAutoDownloadOnLink(it) }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Auto-Shred temp files on app exit
                ExclusiveSwitchRow(
                    icon = Icons.Default.Shield,
                    title = "Zero-Footprint Auto-Shred on Exit",
                    subtitle = "Purges temporary exported media when app minimized",
                    checked = autoShredOnExit,
                    onCheckedChange = { viewModel.toggleAutoShredOnExit(it) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TeleBlue)
                ) {
                    Text("Apply & Save Preferences", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) TeleBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                if (isSelected) TeleBlue else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) TeleBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = if (isSelected) TeleBlue else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = TeleBlue, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun ExclusiveSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) ShieldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ShieldGreen,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }
    }
}

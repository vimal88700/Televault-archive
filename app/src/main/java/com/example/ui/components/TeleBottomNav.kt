package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TeleBlue

enum class NavScreen(val labelRes: Int, val icon: ImageVector, val tag: String) {
    CHATS(R.string.tab_chats, Icons.Default.Chat, "nav_chats"),
    DOWNLOADS(R.string.tab_downloads, Icons.Default.Download, "nav_downloads"),
    VAULT(R.string.tab_vault, Icons.Default.Lock, "nav_vault"),
    STATS(R.string.tab_stats, Icons.Default.BarChart, "nav_stats"),
    SAFETY(R.string.tab_safety, Icons.Default.Security, "nav_safety")
}

@Composable
fun TeleBottomNav(
    currentScreen: NavScreen,
    onScreenSelected: (NavScreen) -> Unit,
    activeDownloadsCount: Int = 0,
    vaultItemsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("tele_bottom_navigation"),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavScreen.values().forEach { screen ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelected(screen) },
                modifier = Modifier.testTag(screen.tag),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = TeleBlue,
                    indicatorColor = TeleBlue,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                icon = {
                    when (screen) {
                        NavScreen.DOWNLOADS -> {
                            if (activeDownloadsCount > 0) {
                                BadgedBox(badge = {
                                    Badge(containerColor = TeleBlue) {
                                        Text("$activeDownloadsCount")
                                    }
                                }) {
                                    Icon(screen.icon, contentDescription = stringResource(screen.labelRes))
                                }
                            } else {
                                Icon(screen.icon, contentDescription = stringResource(screen.labelRes))
                            }
                        }
                        NavScreen.VAULT -> {
                            if (vaultItemsCount > 0) {
                                BadgedBox(badge = {
                                    Badge(containerColor = ShieldGreen) {
                                        Text("$vaultItemsCount")
                                    }
                                }) {
                                    Icon(screen.icon, contentDescription = stringResource(screen.labelRes))
                                }
                            } else {
                                Icon(screen.icon, contentDescription = stringResource(screen.labelRes))
                            }
                        }
                        else -> {
                            Icon(screen.icon, contentDescription = stringResource(screen.labelRes))
                        }
                    }
                },
                label = {
                    Text(
                        text = stringResource(screen.labelRes),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
        }
    }
}

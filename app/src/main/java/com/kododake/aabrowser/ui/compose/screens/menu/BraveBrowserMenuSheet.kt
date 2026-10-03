/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.ui.compose.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kododake.aabrowser.R
import com.kododake.aabrowser.ui.compose.components.ExpressiveBottomSheetContainer
import com.kododake.aabrowser.ui.compose.components.bouncyClickable

private val BraveMenuBg = Color(0xFF1B1D21)
private val BraveDivider = Color(0xFF2E3035)
private val BraveItemText = Color(0xFFE8E8E8)
private val BraveSubText = Color(0xFF9A9DA3)
private val BraveAccent = Color(0xFFFB542B)

@Composable
fun BraveBrowserMenuSheet(
    stateHolder: MenuStateHolder,
    actions: MenuActions,
    modifier: Modifier = Modifier
) {
    ExpressiveBottomSheetContainer(
        isVisible = stateHolder.isMenuVisible,
        onDismissRequest = actions.onClose,
        onDismissFinished = actions.onDismissFinished,
        onProgress = actions.onProgress,
        modifier = modifier
    ) {
        val chromeBar = dimensionResource(R.dimen.browser_chrome_bottom_bar_height)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BraveMenuBg)
                .padding(bottom = chromeBar)
        ) {
            when (stateHolder.menuSubscreen) {
                MenuSubscreen.MAIN -> BraveMainMenu(actions, stateHolder)
                MenuSubscreen.HISTORY -> BraveHistoryScreen(actions, stateHolder)
                MenuSubscreen.DOWNLOADS -> BraveDownloadsScreen(actions, stateHolder)
            }
        }
    }
}

@Composable
private fun BraveMainMenu(actions: MenuActions, stateHolder: MenuStateHolder) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        BraveMenuItem(Icons.Rounded.Add, stringResource(R.string.menu_new_tab)) {
            actions.onNewTab()
        }
        BraveDividerLine()

        BraveMenuItem(Icons.Rounded.DarkMode, stringResource(R.string.menu_new_private_tab)) {
            actions.onNewPrivateTab()
        }
        BraveDividerLine()

        BraveMenuItem(Icons.Rounded.GridView, stringResource(R.string.tab_group_with_active)) {
            actions.onTabs()
        }
        BraveSectionDivider()

        BraveMenuItem(Icons.Rounded.History, stringResource(R.string.menu_history)) {
            stateHolder.menuSubscreen = MenuSubscreen.HISTORY
        }
        BraveDividerLine()

        BraveMenuItem(Icons.Rounded.Download, stringResource(R.string.menu_downloads)) {
            stateHolder.menuSubscreen = MenuSubscreen.DOWNLOADS
        }
        BraveDividerLine()

        BraveMenuItem(Icons.Rounded.Bookmark, stringResource(R.string.menu_bookmarks)) {
            actions.onBookmarks()
        }
        BraveSectionDivider()

        BraveMenuItem(Icons.Rounded.Settings, stringResource(R.string.menu_settings)) {
            actions.onSettings()
        }
        BraveSectionDivider()

        BraveToggleItem(
            icon = Icons.Rounded.DesktopWindows,
            label = stringResource(R.string.menu_desktop_mode),
            checked = stateHolder.isDesktopMode,
            onCheckedChange = { actions.onDesktopToggle(it) }
        )
        BraveDividerLine()

        BraveToggleItem(
            icon = Icons.Rounded.Fullscreen,
            label = stringResource(R.string.menu_fullscreen_mode),
            checked = stateHolder.isFullscreenMode,
            onCheckedChange = { actions.onFullscreenToggle(it) }
        )
        BraveSectionDivider()

        BraveBottomActionBar(actions, stateHolder)

        Spacer(Modifier.height(4.dp))
        BraveMenuFooter(stateHolder.versionName)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun BraveHistoryScreen(actions: MenuActions, stateHolder: MenuStateHolder) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        BraveSubScreenHeader(stringResource(R.string.menu_history)) {
            stateHolder.menuSubscreen = MenuSubscreen.MAIN
        }

        if (stateHolder.historyEntries.isEmpty()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.history_empty), color = BraveSubText, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            stateHolder.historyEntries.take(50).forEach { entry ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bouncyClickable {
                            actions.onNavigate(entry.url)
                            actions.onClose()
                        }
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(entry.title, color = BraveItemText, maxLines = 1, style = MaterialTheme.typography.bodyMedium)
                    Text(entry.url, color = BraveSubText, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
                BraveDividerLine()
            }
        }
    }
}

@Composable
private fun BraveDownloadsScreen(actions: MenuActions, stateHolder: MenuStateHolder) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        BraveSubScreenHeader(stringResource(R.string.menu_downloads)) {
            stateHolder.menuSubscreen = MenuSubscreen.MAIN
        }

        if (stateHolder.downloadEntries.isEmpty()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.downloads_empty), color = BraveSubText, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            stateHolder.downloadEntries.take(30).forEach { url ->
                Text(
                    text = url,
                    color = BraveItemText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .bouncyClickable {
                            actions.onNavigate(url)
                            actions.onClose()
                        }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium
                )
                BraveDividerLine()
            }
        }
    }
}

@Composable
private fun BraveSubScreenHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "← ",
            color = BraveAccent,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.bouncyClickable(onClick = onBack)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = title,
            color = BraveItemText,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )
    }
    BraveSectionDivider()
}

@Composable
private fun BraveMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bouncyClickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = BraveItemText, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(18.dp))
        Text(
            text = label,
            color = BraveItemText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun BraveToggleItem(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bouncyClickable(onClick = { onCheckedChange(!checked) })
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = BraveItemText, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(18.dp))
        Text(
            text = label,
            color = BraveItemText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = BraveAccent,
                uncheckedTrackColor = Color(0xFF3A3D42)
            )
        )
    }
}

@Composable
private fun BraveBottomActionBar(actions: MenuActions, stateHolder: MenuStateHolder) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BraveActionIcon(
            icon = Icons.AutoMirrored.Rounded.ArrowForward,
            enabled = stateHolder.canGoForward,
            onClick = { actions.onForward(); actions.onClose() }
        )
        BraveActionIcon(
            icon = Icons.Rounded.QrCode2,
            enabled = stateHolder.canQrCode,
            onClick = { actions.onQrCode() }
        )
        BraveActionIcon(
            icon = Icons.Rounded.Download,
            enabled = true,
            onClick = { stateHolder.menuSubscreen = MenuSubscreen.DOWNLOADS }
        )
        BraveActionIcon(
            icon = Icons.Rounded.Refresh,
            enabled = stateHolder.canReload,
            onClick = { actions.onReload() }
        )
    }
}

@Composable
private fun BraveActionIcon(icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    val alpha = if (enabled) 1f else 0.35f
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF2B2D31))
            .then(if (enabled) Modifier.bouncyClickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = BraveItemText.copy(alpha = alpha), modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun BraveDividerLine() {
    HorizontalDivider(
        color = BraveDivider,
        thickness = 0.5.dp,
        modifier = Modifier.padding(start = 60.dp)
    )
}

@Composable
private fun BraveSectionDivider() {
    HorizontalDivider(color = BraveDivider, thickness = 0.5.dp)
}

@Composable
private fun BraveMenuFooter(versionName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Text(
            text = stringResource(R.string.app_credit),
            color = BraveSubText,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = versionName,
            color = BraveSubText.copy(0.6f),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

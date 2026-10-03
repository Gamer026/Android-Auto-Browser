/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.ui.compose.screens.tabs

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.GridOff
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kododake.aabrowser.R
import com.kododake.aabrowser.ui.compose.components.bouncyClickable

private val BraveCardBg = Color(0xFF1B1D21)
private val BraveCardBorder = Color(0xFF3A3D42)
private val BraveSearchBg = Color(0xFF2B2D31)
private val BraveBlueBorder = Color(0xFF4285F4)
private val BraveGroupBg = Color(0xFF2C5BCC)
private val BravePreviewBg = Color(0xFF25272B)

@Composable
fun TabSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    requestFocus: Boolean,
    onFocusApplied: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    androidx.compose.runtime.LaunchedEffect(requestFocus) {
        if (requestFocus) {
            focusRequester.requestFocus()
            onFocusApplied()
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BraveSearchBg)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.White.copy(0.5f), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            cursorBrush = SolidColor(Color.White),
            modifier = Modifier.weight(1f).focusRequester(focusRequester),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(stringResource(R.string.tab_search_hint), color = Color.White.copy(0.45f), style = MaterialTheme.typography.bodyMedium)
                }
                inner()
            }
        )
    }
}

@Composable
fun TabGridSingleCard(
    tab: TabItemUi,
    favicon: Bitmap?,
    thumbnail: Bitmap?,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    onGroupWithActive: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val borderColor = if (tab.isActive) BraveBlueBorder else BraveCardBorder.copy(0.5f)
    val borderWidth = if (tab.isActive) 2.dp else 1.dp

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BraveCardBg)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .bouncyClickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabFavicon(favicon, tab.isPrivate, Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(tab.title, Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.White, style = MaterialTheme.typography.labelMedium)
            Box {
                IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Rounded.MoreVert, contentDescription = null, tint = Color.White.copy(0.6f), modifier = Modifier.size(16.dp))
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.tab_group_with_active)) }, onClick = { menuExpanded = false; onGroupWithActive() })
                }
            }
            IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Rounded.Close, contentDescription = null, tint = Color.White.copy(0.6f), modifier = Modifier.size(16.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .padding(bottom = 6.dp)
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(8.dp))
                .background(BravePreviewBg),
            contentAlignment = Alignment.Center
        ) {
            if (thumbnail != null) {
                Image(
                    bitmap = thumbnail.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else if (favicon != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(bitmap = favicon.asImageBitmap(), contentDescription = null, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(6.dp)), contentScale = ContentScale.Fit)
                    Spacer(Modifier.height(6.dp))
                    Text(tab.url.removePrefix("https://").removePrefix("http://").removePrefix("www.").take(30), color = Color.White.copy(0.4f), style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 8.dp))
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Language, contentDescription = null, tint = Color.White.copy(0.3f), modifier = Modifier.size(36.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(tab.url.removePrefix("https://").removePrefix("http://").removePrefix("www.").take(30), color = Color.White.copy(0.4f), style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    }
}

@Composable
fun TabGridGroupCard(
    entry: TabGridEntry.Group,
    faviconProvider: (String) -> Bitmap?,
    thumbnailProvider: (Long) -> Bitmap?,
    onSelectTab: (Long) -> Unit,
    onCloseGroup: () -> Unit,
    onRenameGroup: (String) -> Unit,
    onUngroupTabs: () -> Unit,
    onDeleteGroup: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    if (showRenameDialog) {
        GroupRenameDialog(
            currentTitle = entry.title,
            onDismiss = { showRenameDialog = false },
            onRename = { newTitle -> showRenameDialog = false; onRenameGroup(newTitle) }
        )
    }

    if (expanded) {
        ExpandedGroupView(entry, faviconProvider, thumbnailProvider, onSelectTab, onCollapse = { expanded = false })
        return
    }

    val previewTabs = entry.tabs.take(4)
    val extraCount = (entry.tabs.size - 4).coerceAtLeast(0)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BraveGroupBg)
            .border(1.dp, BraveGroupBg.copy(0.6f), RoundedCornerShape(12.dp))
            .bouncyClickable { expanded = true }
    ) {
        Row(
            Modifier.fillMaxWidth().padding(start = 10.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF6AA4F8)))
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.tab_group_count, entry.tabs.size),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Box {
                IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Rounded.MoreVert, contentDescription = null, tint = Color.White.copy(0.7f), modifier = Modifier.size(16.dp))
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.group_menu_close)) },
                        leadingIcon = { Icon(Icons.Outlined.Close, contentDescription = null) },
                        onClick = { menuExpanded = false; onCloseGroup() }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.group_menu_rename)) },
                        leadingIcon = { Icon(Icons.Outlined.DriveFileRenameOutline, contentDescription = null) },
                        onClick = { menuExpanded = false; showRenameDialog = true }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.group_menu_ungroup)) },
                        leadingIcon = { Icon(Icons.Outlined.GridOff, contentDescription = null) },
                        onClick = { menuExpanded = false; onUngroupTabs() }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.group_menu_delete)) },
                        leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                        onClick = { menuExpanded = false; onDeleteGroup() }
                    )
                }
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 6.dp).padding(bottom = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            previewTabs.take(2).forEach { tab ->
                GroupThumbnailBox(tab, faviconProvider, thumbnailProvider, onSelectTab, Modifier.weight(1f))
            }
            if (previewTabs.size < 2) Spacer(Modifier.weight(1f))
        }

        if (previewTabs.size > 2 || extraCount > 0) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 6.dp).padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                previewTabs.drop(2).forEach { tab ->
                    GroupThumbnailBox(tab, faviconProvider, thumbnailProvider, onSelectTab, Modifier.weight(1f))
                }
                if (extraCount > 0) {
                    Box(
                        modifier = Modifier.weight(1f).aspectRatio(0.75f).clip(RoundedCornerShape(6.dp)).background(Color(0xFF3B6FD6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+$extraCount", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
                val filledSlots = previewTabs.drop(2).size + if (extraCount > 0) 1 else 0
                if (filledSlots < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GroupThumbnailBox(
    tab: TabItemUi,
    faviconProvider: (String) -> Bitmap?,
    thumbnailProvider: (Long) -> Bitmap?,
    onSelectTab: (Long) -> Unit,
    modifier: Modifier
) {
    val thumb = thumbnailProvider(tab.id)
    Box(
        modifier = modifier
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1B1D21))
            .bouncyClickable { onSelectTab(tab.id) },
        contentAlignment = Alignment.Center
    ) {
        if (thumb != null) {
            Image(bitmap = thumb.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            val fav = faviconProvider(tab.url)
            if (fav != null) {
                Image(bitmap = fav.asImageBitmap(), contentDescription = null, modifier = Modifier.size(28.dp).clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Fit)
            } else {
                Icon(Icons.Rounded.Language, contentDescription = null, tint = Color.White.copy(0.3f), modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun ExpandedGroupView(
    entry: TabGridEntry.Group,
    faviconProvider: (String) -> Bitmap?,
    thumbnailProvider: (Long) -> Bitmap?,
    onSelectTab: (Long) -> Unit,
    onCollapse: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BraveGroupBg)
            .border(2.dp, BraveBlueBorder, RoundedCornerShape(12.dp))
    ) {
        Row(
            Modifier.fillMaxWidth().padding(start = 12.dp, end = 8.dp, top = 10.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("← ", color = Color.White, modifier = Modifier.bouncyClickable(onClick = onCollapse))
            Box(Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF6AA4F8)))
            Spacer(Modifier.width(8.dp))
            Text(
                entry.title,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                stringResource(R.string.tab_group_count, entry.tabs.size),
                color = Color.White.copy(0.7f),
                style = MaterialTheme.typography.labelSmall
            )
        }

        Column(Modifier.padding(horizontal = 6.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            entry.tabs.forEach { tab ->
                val thumb = thumbnailProvider(tab.id)
                val fav = faviconProvider(tab.url)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1B1D21))
                        .bouncyClickable { onSelectTab(tab.id) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (thumb != null) {
                        Image(
                            bitmap = thumb.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp, 36.dp).clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        TabFavicon(fav, tab.isPrivate, Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(tab.title, color = Color.White, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(tab.url.removePrefix("https://").removePrefix("http://").take(40), color = Color.White.copy(0.5f), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
    }
}

@Composable
private fun GroupRenameDialog(currentTitle: String, onDismiss: () -> Unit, onRename: (String) -> Unit) {
    var text by remember { mutableStateOf(currentTitle) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.group_menu_rename)) },
        text = {
            TextField(value = text, onValueChange = { text = it }, singleLine = true)
        },
        confirmButton = {
            TextButton(onClick = { if (text.isNotBlank()) onRename(text.trim()) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.menu_close)) }
        }
    )
}

@Composable
internal fun TabFavicon(favicon: Bitmap?, isPrivate: Boolean, modifier: Modifier = Modifier) {
    if (isPrivate) {
        Icon(Icons.Rounded.DarkMode, contentDescription = null, tint = Color(0xFF9FA8DA), modifier = modifier)
    } else if (favicon != null) {
        Image(bitmap = favicon.asImageBitmap(), contentDescription = null, modifier = modifier.clip(RoundedCornerShape(3.dp)), contentScale = ContentScale.Fit)
    } else {
        Icon(Icons.Rounded.Language, contentDescription = null, tint = Color.White.copy(0.5f), modifier = modifier)
    }
}

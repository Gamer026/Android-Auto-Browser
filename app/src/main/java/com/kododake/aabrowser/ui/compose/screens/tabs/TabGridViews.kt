/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.ui.compose.screens.tabs

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.GridOff
import androidx.compose.material.icons.outlined.SelectAll
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kododake.aabrowser.R
import kotlin.math.roundToInt
import com.kododake.aabrowser.ui.compose.components.bouncyClickable

private val BraveCardBg = Color(0xFF1B1D21)
private val BraveCardBorder = Color(0xFF3A3D42)
private val BraveSearchBg = Color(0xFF2B2D31)
private val BraveBlueBorder = Color(0xFF4285F4)
private val BraveGroupBg = Color(0xFF2952C8)
private val BraveGroupAccent = Color(0xFF6AA4F8)
private val BravePreviewBg = Color(0xFF25272B)
private val BraveGroupPanelBg = Color(0xFF1E2024)
private val BraveDropHighlight = Color(0xFF5B9BF5)

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
            .clip(RoundedCornerShape(22.dp))
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TabGridSingleCard(
    tab: TabItemUi,
    otherTabs: List<TabItemUi>,
    favicon: Bitmap?,
    thumbnail: Bitmap?,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    onGroupWithActive: () -> Unit,
    onGroupWithTab: (Long) -> Unit,
    modifier: Modifier = Modifier,
    dragState: TabGridDragState? = null,
    enableDrag: Boolean = true,
    showGroupMenu: Boolean = true
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isDropTarget = dragState?.isHighlighted(TabDropTarget.Tab(tab.id)) == true
    val borderColor = when {
        isDropTarget -> BraveDropHighlight
        tab.isActive -> BraveBlueBorder
        else -> BraveCardBorder.copy(0.5f)
    }
    val borderWidth = when {
        isDropTarget || tab.isActive -> 2.dp
        else -> 1.dp
    }
    val dragging = dragState?.isDragging(tab.id) == true
    val dragFade by animateFloatAsState(
        targetValue = if (dragging) 0.45f else 1f,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "tabCardDragFade"
    )

    Column(
        modifier = modifier
            .alpha(dragFade)
            .then(
                if (dragState != null && enableDrag) {
                    Modifier
                        .tabDropTarget(dragState, TabDropTarget.Tab(tab.id))
                        .tabCardDraggable(dragState, tab.id)
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(12.dp))
            .background(BraveCardBg)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .pointerInput(tab.id, dragState?.draggedTabId) {
                detectTapGestures(onTap = { onSelect() })
            }
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
            if (showGroupMenu) {
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = null, tint = Color.White.copy(0.6f), modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        if (otherTabs.isNotEmpty()) {
                            otherTabs.forEach { other ->
                                DropdownMenuItem(
                                    text = {
                                        Text(stringResource(R.string.tab_group_with_named, other.title))
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onGroupWithTab(other.id)
                                    }
                                )
                            }
                        } else {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.tab_group_with_active)) },
                                onClick = {
                                    menuExpanded = false
                                    onGroupWithActive()
                                }
                            )
                        }
                    }
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
            if (thumbnail != null && !thumbnail.isRecycled) {
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
    onOpenGroup: () -> Unit,
    onCloseGroup: () -> Unit,
    onRenameGroup: (String) -> Unit,
    onUngroupTabs: () -> Unit,
    onDeleteGroup: () -> Unit,
    modifier: Modifier = Modifier,
    dragState: TabGridDragState? = null
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    if (showRenameDialog) {
        GroupRenameDialog(
            currentTitle = entry.title,
            onDismiss = { showRenameDialog = false },
            onRename = { newTitle -> showRenameDialog = false; onRenameGroup(newTitle) }
        )
    }

    val hasOverflow = entry.tabs.size > 4
    val previewTabs = if (hasOverflow) entry.tabs.take(3) else entry.tabs.take(4)
    val extraCount = if (hasOverflow) entry.tabs.size - 3 else 0
    val isDropTarget = dragState?.isHighlighted(TabDropTarget.Group(entry.groupId)) == true

    Column(
        modifier = modifier
            .then(
                if (dragState != null) {
                    Modifier.tabDropTarget(dragState, TabDropTarget.Group(entry.groupId))
                } else {
                    Modifier
                }
            )
            .shadow(if (isDropTarget) 8.dp else 0.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(BraveGroupBg)
            .border(
                width = if (isDropTarget) 2.dp else 1.dp,
                color = if (isDropTarget) BraveDropHighlight else BraveGroupBg.copy(0.85f),
                shape = RoundedCornerShape(14.dp)
            )
            .bouncyClickable { onOpenGroup() }
    ) {
        Row(
            Modifier.fillMaxWidth().padding(start = 10.dp, end = 4.dp, top = 8.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(14.dp).clip(CircleShape).background(BraveGroupAccent))
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
                    Icon(Icons.Rounded.MoreVert, contentDescription = null, tint = Color.White.copy(0.85f), modifier = Modifier.size(18.dp))
                }
                GroupOverflowMenu(
                    expanded = menuExpanded,
                    onDismiss = { menuExpanded = false },
                    onCloseGroup = { menuExpanded = false; onCloseGroup() },
                    onRename = { menuExpanded = false; showRenameDialog = true },
                    onUngroup = { menuExpanded = false; onUngroupTabs() },
                    onDelete = { menuExpanded = false; onDeleteGroup() },
                    showBraveExtras = false
                )
            }
        }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 6.dp).padding(bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                GroupPreviewCell(previewTabs.getOrNull(0), faviconProvider, thumbnailProvider, Modifier.weight(1f))
                GroupPreviewCell(previewTabs.getOrNull(1), faviconProvider, thumbnailProvider, Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                GroupPreviewCell(previewTabs.getOrNull(2), faviconProvider, thumbnailProvider, Modifier.weight(1f))
                if (extraCount > 0) {
                    GroupOverflowCell(extraCount, Modifier.weight(1f))
                } else {
                    GroupPreviewCell(previewTabs.getOrNull(3), faviconProvider, thumbnailProvider, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun GroupPreviewCell(
    tab: TabItemUi?,
    faviconProvider: (String) -> Bitmap?,
    thumbnailProvider: (Long) -> Bitmap?,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1B1D21)),
        contentAlignment = Alignment.Center
    ) {
        if (tab == null) return@Box
        val thumb = thumbnailProvider(tab.id)
        if (thumb != null && !thumb.isRecycled) {
            Image(bitmap = thumb.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            val fav = faviconProvider(tab.url)
            if (fav != null) {
                Image(bitmap = fav.asImageBitmap(), contentDescription = null, modifier = Modifier.size(28.dp).clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Fit)
            } else {
                Icon(Icons.Rounded.Language, contentDescription = null, tint = Color.White.copy(0.3f), modifier = Modifier.size(24.dp))
            }
        }
        val fav = faviconProvider(tab.url)
        Box(
            Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
                .size(18.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF0D0F12).copy(0.85f)),
            contentAlignment = Alignment.Center
        ) {
            TabFavicon(fav, tab.isPrivate, Modifier.size(12.dp))
        }
    }
}

@Composable
private fun GroupOverflowCell(extraCount: Int, modifier: Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF3B6FD6)),
        contentAlignment = Alignment.Center
    ) {
        Text("+$extraCount", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun GroupOverflowMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onCloseGroup: () -> Unit,
    onRename: () -> Unit,
    onUngroup: () -> Unit,
    onDelete: () -> Unit,
    showBraveExtras: Boolean
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        if (showBraveExtras) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.group_menu_select_tabs)) },
                leadingIcon = { Icon(Icons.Outlined.SelectAll, contentDescription = null) },
                onClick = onDismiss
            )
        }
        DropdownMenuItem(
            text = { Text(stringResource(R.string.group_menu_rename)) },
            leadingIcon = { Icon(Icons.Outlined.DriveFileRenameOutline, contentDescription = null) },
            onClick = onRename
        )
        if (showBraveExtras) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.group_menu_edit_colour)) },
                leadingIcon = { Icon(Icons.Outlined.Colorize, contentDescription = null) },
                onClick = onDismiss
            )
        }
        DropdownMenuItem(
            text = { Text(stringResource(R.string.group_menu_close)) },
            leadingIcon = { Icon(Icons.Outlined.Close, contentDescription = null) },
            onClick = onCloseGroup
        )
        if (!showBraveExtras) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.group_menu_ungroup)) },
                leadingIcon = { Icon(Icons.Outlined.GridOff, contentDescription = null) },
                onClick = onUngroup
            )
        }
        DropdownMenuItem(
            text = { Text(stringResource(R.string.group_menu_delete)) },
            leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
            onClick = onDelete
        )
    }
}

@Composable
fun TabGroupDetailScreen(
    entry: TabGridEntry.Group,
    allTabs: List<TabItemUi>,
    actions: TabActions,
    faviconProvider: (String) -> Bitmap?,
    dragState: TabGridDragState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    if (showRenameDialog) {
        GroupRenameDialog(
            currentTitle = entry.title,
            onDismiss = { showRenameDialog = false },
            onRename = { newTitle ->
                showRenameDialog = false
                actions.onRenameGroup(entry.groupId, newTitle)
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .tabDropTarget(dragState, TabDropTarget.UngroupZone)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BraveGroupPanelBg)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.tab_manager_back), tint = Color.White)
                }
                Box(Modifier.size(14.dp).clip(CircleShape).background(BraveGroupAccent))
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.tab_group_count, entry.tabs.size),
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = actions.onNewTab) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.tab_manager_add), tint = Color.White)
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = null, tint = Color.White)
                    }
                    GroupOverflowMenu(
                        expanded = menuExpanded,
                        onDismiss = { menuExpanded = false },
                        onCloseGroup = {
                            menuExpanded = false
                            actions.onCloseGroup(entry.groupId)
                            onBack()
                        },
                        onRename = { menuExpanded = false; showRenameDialog = true },
                        onUngroup = {
                            menuExpanded = false
                            actions.onUngroupTabs(entry.groupId)
                            onBack()
                        },
                        onDelete = {
                            menuExpanded = false
                            actions.onDeleteGroup(entry.groupId)
                            onBack()
                        },
                        showBraveExtras = true
                    )
                }
            }

            Text(
                stringResource(R.string.tab_drag_ungroup_hint),
                color = Color.White.copy(0.45f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                items(
                    count = entry.tabs.size,
                    key = { index -> entry.tabs[index].id }
                ) { index ->
                    val tab = entry.tabs[index]
                    TabGridSingleCard(
                        tab = tab,
                        otherTabs = allTabs.filter { it.id != tab.id },
                        favicon = faviconProvider(tab.url),
                        thumbnail = actions.thumbnailProvider(tab.id),
                        onSelect = { actions.onSelectTab(tab.id) },
                        onClose = { actions.onCloseTab(tab.id) },
                        onGroupWithActive = { actions.onGroupWithActive(tab.id) },
                        onGroupWithTab = { partnerId -> actions.onGroupWithTab(tab.id, partnerId) },
                        dragState = dragState,
                        showGroupMenu = false
                    )
                }
            }
        }
    }
}

@Composable
fun TabDragFloatingPreview(
    tabs: List<TabItemUi>,
    dragState: TabGridDragState,
    faviconProvider: (String) -> Bitmap?,
    thumbnailProvider: (Long) -> Bitmap?
) {
    val draggedId = dragState.draggedTabId ?: return
    val tab = tabs.firstOrNull { it.id == draggedId } ?: return
    val position = dragState.dragPositionInRoot

    Box(Modifier.fillMaxSize()) {
        TabGridSingleCard(
            tab = tab,
            otherTabs = emptyList(),
            favicon = faviconProvider(tab.url),
            thumbnail = thumbnailProvider(tab.id),
            onSelect = {},
            onClose = {},
            onGroupWithActive = {},
            onGroupWithTab = {},
            enableDrag = false,
            showGroupMenu = false,
            modifier = Modifier
                .width(156.dp)
                .offset {
                    IntOffset(
                        (position.x - 78f).roundToInt(),
                        (position.y - 100f).roundToInt()
                    )
                }
                .shadow(12.dp, RoundedCornerShape(12.dp))
        )
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

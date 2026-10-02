/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.ui.compose.screens.startpage

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Tab
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kododake.aabrowser.R
import com.kododake.aabrowser.ui.compose.components.BraveSearchBar
import com.kododake.aabrowser.ui.compose.components.bouncyClickable

private val BraveSearchBarColor = Color(0xFF21262D)
private val BraveBottomBarColor = Color(0xFF0D0F12)
private val BraveShortcutPanelColor = Color(0xCC1A1D24)
private val BraveOrange = Color(0xFFFB542B)

@Composable
fun BraveHomeBackground(
    customBackgroundBitmap: Bitmap?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (customBackgroundBitmap != null && !customBackgroundBitmap.isRecycled) {
            Image(
                bitmap = customBackgroundBitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(R.drawable.start_page_wallpaper_default),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.05f),
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.72f)
                        )
                    )
                )
        )
    }
}

@Composable
fun BraveShortcutsPanel(
    slots: List<StartPageSlotUi>,
    dragState: StartPageQuickLinksDragState,
    onSlotClick: (Int, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BraveShortcutPanelColor,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            itemsIndexed(slots, key = { _, slot -> slot.id }) { index, slot ->
                key(slot.id) {
                    BraveShortcutItem(
                        slot = slot,
                        index = index,
                        dragState = dragState,
                        onSlotClick = onSlotClick
                    )
                }
            }
        }
    }
}

@Composable
private fun BraveShortcutItem(
    slot: StartPageSlotUi,
    index: Int,
    dragState: StartPageQuickLinksDragState,
    onSlotClick: (Int, String?) -> Unit
) {
    val isDragging = dragState.draggedIndex == index
    val label = when {
        !slot.url.isNullOrBlank() && slot.title.isNotBlank() -> slot.title
        !slot.url.isNullOrBlank() && slot.label.isNotBlank() -> slot.label
        !slot.url.isNullOrBlank() -> slot.url.removePrefix("https://").take(12)
        else -> stringResource(R.string.start_page_slot_number, index + 1)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .zIndex(if (isDragging) 10f else 0f)
            .onGloballyPositioned { dragState.updateSlotCenter(index, it) }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A2F38))
                .bouncyClickable(shape = CircleShape) {
                    if (dragState.draggedIndex == null && !dragState.isSettling) {
                        onSlotClick(index, slot.url)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            SiteIconBadge(
                url = slot.url,
                modifier = Modifier.size(52.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.88f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BraveBottomNavigationBar(
    openTabCount: Int,
    onHome: () -> Unit,
    onBookmarks: () -> Unit,
    onSearch: () -> Unit,
    onTabs: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BraveBottomBarColor.copy(alpha = 0.96f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BraveNavItem(
                icon = Icons.Rounded.Home,
                label = stringResource(R.string.start_page_nav_home),
                onClick = onHome
            )
            BraveNavItem(
                icon = Icons.Rounded.Bookmarks,
                label = stringResource(R.string.start_page_nav_bookmarks),
                onClick = onBookmarks
            )
            BraveNavItem(
                icon = Icons.Rounded.Search,
                label = stringResource(R.string.start_page_nav_search),
                onClick = onSearch
            )
            BraveNavTabItem(
                tabCount = openTabCount.coerceAtLeast(1),
                onClick = onTabs
            )
            BraveNavItem(
                icon = Icons.AutoMirrored.Rounded.MenuBook,
                label = stringResource(R.string.start_page_nav_menu),
                onClick = onMenu
            )
        }
    }
}

@Composable
private fun BraveNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .bouncyClickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.92f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun BraveNavTabItem(
    tabCount: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .bouncyClickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Rounded.Tab,
                contentDescription = stringResource(R.string.start_page_nav_tabs),
                tint = Color.White.copy(alpha = 0.92f),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = tabCount.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun BraveStartPageTopBar(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        BraveSearchBar(
            onNavigate = onNavigate,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_car_browser_mark),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(22.dp)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Security,
                    contentDescription = null,
                    tint = BraveOrange,
                    modifier = Modifier.size(22.dp)
                )
            },
            containerColor = BraveSearchBarColor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

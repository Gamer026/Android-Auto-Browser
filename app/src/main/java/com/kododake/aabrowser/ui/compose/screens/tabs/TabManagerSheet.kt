/*

 * Car Browser ù GPLv3 derivative. See LICENSE.

 */



package com.kododake.aabrowser.ui.compose.screens.tabs



import android.graphics.Bitmap

import androidx.compose.foundation.background

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

import androidx.compose.foundation.lazy.grid.GridCells

import androidx.compose.foundation.lazy.grid.LazyVerticalGrid

import androidx.compose.foundation.lazy.grid.items

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Add

import androidx.compose.material.icons.filled.GridView

import androidx.compose.material3.FloatingActionButton

import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalConfiguration

import androidx.compose.ui.res.dimensionResource

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import com.kododake.aabrowser.R

import com.kododake.aabrowser.ui.compose.components.ExpressiveBottomSheetContainer



private val BraveDarkBg = Color(0xFF202124)



@Composable

fun TabManagerSheet(

    isVisible: Boolean,

    tabs: List<TabItemUi>,

    actions: TabActions,

    faviconProvider: (String) -> Bitmap? = { null },

    animateEnter: Boolean = true,

    keepScrimOnClose: Boolean = false,

    onProgress: (Float) -> Unit = {},

    modifier: Modifier = Modifier

) {

    ExpressiveBottomSheetContainer(

        isVisible = isVisible,

        onDismissRequest = actions.onDismiss,

        onDismissFinished = actions.onDismissFinished,

        animateEnter = animateEnter,

        keepScrimOnClose = keepScrimOnClose,

        onProgress = onProgress,

        modifier = modifier

    ) {

        val screenHeight = LocalConfiguration.current.screenHeightDp.dp

        val chromeBar = dimensionResource(R.dimen.browser_chrome_bottom_bar_height)

        val filteredEntries = filterGridEntries(actions.gridEntries, actions.tabSearchQuery)

        var openGroupId by remember { mutableStateOf<String?>(null) }



        val openGroupEntry = openGroupId?.let { id ->

            actions.gridEntries.filterIsInstance<TabGridEntry.Group>().firstOrNull { it.groupId == id }

        }



        val dragState = rememberTabGridDragState { draggedTabId, target ->

            handleTabDrop(

                draggedTabId = draggedTabId,

                target = target,

                openGroupId = openGroupId,

                actions = actions

            )

        }



        Box(

            modifier = Modifier

                .fillMaxWidth()

                .height(screenHeight - 40.dp)

                .background(BraveDarkBg)

        ) {

            AnimatedContent(
                targetState = openGroupEntry,
                transitionSpec = {
                    if (targetState != null) {
                        (slideInHorizontally(animationSpec = tween(340)) { it / 4 } + fadeIn(tween(280)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(280)) { -it / 4 } + fadeOut(tween(220)))
                    } else {
                        (slideInHorizontally(animationSpec = tween(340)) { -it / 4 } + fadeIn(tween(280)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(280)) { it / 4 } + fadeOut(tween(220)))
                    }
                },
                label = "tabGroupDetail"
            ) { groupEntry ->
                if (groupEntry != null) {
                    TabGroupDetailScreen(
                        entry = groupEntry,
                        allTabs = tabs,
                        actions = actions,
                        faviconProvider = faviconProvider,
                        dragState = dragState,
                        onBack = { openGroupId = null },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                Column(

                    modifier = Modifier

                        .fillMaxSize()

                        .padding(bottom = chromeBar + 60.dp)

                ) {

                    BraveTabTopBar(tabCount = tabs.size)



                    TabSearchBar(

                        query = actions.tabSearchQuery,

                        onQueryChange = actions.onTabSearchQueryChange,

                        requestFocus = actions.requestTabSearchFocus,

                        onFocusApplied = actions.onTabSearchFocusConsumed,

                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

                    )



                    if (filteredEntries.isEmpty()) {

                        TabEmptyView()

                    } else {

                        LazyVerticalGrid(

                            columns = GridCells.Fixed(2),

                            horizontalArrangement = Arrangement.spacedBy(8.dp),

                            verticalArrangement = Arrangement.spacedBy(8.dp),

                            modifier = Modifier

                                .fillMaxWidth()

                                .weight(1f)

                                .padding(horizontal = 16.dp)

                        ) {

                            items(filteredEntries, key = { entry ->

                                when (entry) {

                                    is TabGridEntry.Single -> "tab_${entry.tab.id}"

                                    is TabGridEntry.Group -> "group_${entry.groupId}"

                                }

                            }) { entry ->

                                when (entry) {

                                    is TabGridEntry.Single -> TabGridSingleCard(

                                        tab = entry.tab,

                                        otherTabs = tabs.filter { it.id != entry.tab.id },

                                        favicon = faviconProvider(entry.tab.url),

                                        thumbnail = actions.thumbnailProvider(entry.tab.id),

                                        onSelect = { actions.onSelectTab(entry.tab.id) },

                                        onClose = { actions.onCloseTab(entry.tab.id) },

                                        onGroupWithActive = { actions.onGroupWithActive(entry.tab.id) },

                                        onGroupWithTab = { partnerId ->

                                            actions.onGroupWithTab(entry.tab.id, partnerId)

                                        },

                                        dragState = dragState

                                    )

                                    is TabGridEntry.Group -> TabGridGroupCard(

                                        entry = entry,

                                        faviconProvider = faviconProvider,

                                        thumbnailProvider = actions.thumbnailProvider,

                                        onOpenGroup = { openGroupId = entry.groupId },

                                        onCloseGroup = { actions.onCloseGroup(entry.groupId) },

                                        onRenameGroup = { newTitle -> actions.onRenameGroup(entry.groupId, newTitle) },

                                        onUngroupTabs = { actions.onUngroupTabs(entry.groupId) },

                                        onDeleteGroup = { actions.onDeleteGroup(entry.groupId) },

                                        onCycleGroupColor = { actions.onCycleGroupColor(entry.groupId) },

                                        dragState = dragState

                                    )

                                }

                            }

                        }

                    }

                }
                }
            }

            FloatingActionButton(

                onClick = actions.onNewTab,

                containerColor = Color(0xFF4285F4),

                contentColor = Color.White,

                shape = CircleShape,

                modifier = Modifier

                    .align(Alignment.BottomCenter)

                    .padding(bottom = chromeBar + 12.dp)

                    .size(52.dp)

            ) {

                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(28.dp))

            }



            TabDragFloatingPreview(

                tabs = tabs,

                dragState = dragState,

                faviconProvider = faviconProvider,

                thumbnailProvider = actions.thumbnailProvider

            )

        }

    }

}



private fun handleTabDrop(

    draggedTabId: Long,

    target: TabDropTarget?,

    openGroupId: String?,

    actions: TabActions

) {

    if (openGroupId != null) {

        when (target) {

            is TabDropTarget.Tab -> {

                if (target.id != draggedTabId) {

                    actions.onGroupWithTab(draggedTabId, target.id)

                }

            }

            is TabDropTarget.Group -> actions.onAddTabToGroup(draggedTabId, target.groupId)

            TabDropTarget.UngroupZone -> actions.onRemoveTabFromGroup(draggedTabId)
            null -> Unit

        }

        return

    }



    when (target) {

        is TabDropTarget.Tab -> {

            if (target.id != draggedTabId) {

                actions.onGroupWithTab(draggedTabId, target.id)

            }

        }

        is TabDropTarget.Group -> actions.onAddTabToGroup(draggedTabId, target.groupId)

        TabDropTarget.UngroupZone, null -> Unit

    }

}



@Composable

private fun BraveTabTopBar(tabCount: Int) {

    Row(

        modifier = Modifier

            .fillMaxWidth()

            .padding(horizontal = 16.dp, vertical = 12.dp),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.Center

    ) {

        Box(

            modifier = Modifier

                .clip(RoundedCornerShape(6.dp))

                .background(Color(0xFF3A3D42))

                .padding(horizontal = 10.dp, vertical = 4.dp),

            contentAlignment = Alignment.Center

        ) {

            Text(tabCount.toString(), color = Color.White, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), fontSize = 14.sp)

        }

        Spacer(Modifier.width(8.dp))

        Box(

            modifier = Modifier

                .clip(RoundedCornerShape(6.dp))

                .background(Color(0xFF3A3D42))

                .padding(horizontal = 8.dp, vertical = 4.dp),

            contentAlignment = Alignment.Center

        ) {

            Icon(Icons.Filled.GridView, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))

        }

    }

}



private fun filterGridEntries(entries: List<TabGridEntry>, query: String): List<TabGridEntry> {

    if (query.isBlank()) return entries

    val q = query.trim().lowercase()

    return entries.mapNotNull { entry ->

        when (entry) {

            is TabGridEntry.Single -> {

                if (entry.tab.title.lowercase().contains(q) || entry.tab.url.lowercase().contains(q)) entry else null

            }

            is TabGridEntry.Group -> {

                val matching = entry.tabs.filter { it.title.lowercase().contains(q) || it.url.lowercase().contains(q) }

                if (matching.isEmpty()) null else entry.copy(tabs = matching)

            }

        }

    }

}



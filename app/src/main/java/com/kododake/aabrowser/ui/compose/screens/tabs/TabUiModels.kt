/*
 * Car Browser ù GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.ui.compose.screens.tabs

import android.graphics.Bitmap

data class TabItemUi(
    val id: Long,
    val title: String,
    val url: String,
    val isActive: Boolean,
    val isPrivate: Boolean = false,
    val groupId: String? = null
)

sealed class TabGridEntry {
    data class Single(val tab: TabItemUi) : TabGridEntry()
    data class Group(
        val groupId: String,
        val title: String,
        val tabs: List<TabItemUi>,
        val colorIndex: Int = 0
    ) : TabGridEntry()
}

data class TabActions(
    val onSelectTab: (Long) -> Unit = {},
    val onCloseTab: (Long) -> Unit = {},
    val onNewTab: () -> Unit = {},
    val onNewTabInGroup: (String) -> Unit = {},
    val onReorderTabs: (Int, Int) -> Unit = { _, _ -> },
    val onCommitTabReorder: () -> Unit = {},
    val onClose: () -> Unit = {},
    val onDismiss: () -> Unit = {},
    val onDismissFinished: () -> Unit = {},
    val onGroupWithActive: (Long) -> Unit = {},
    val onGroupWithTab: (Long, Long) -> Unit = { _, _ -> },
    val tabSearchQuery: String = "",
    val onTabSearchQueryChange: (String) -> Unit = {},
    val requestTabSearchFocus: Boolean = false,
    val onTabSearchFocusConsumed: () -> Unit = {},
    val gridEntries: List<TabGridEntry> = emptyList(),
    val onCloseGroup: (String) -> Unit = {},
    val onRenameGroup: (String, String) -> Unit = { _, _ -> },
    val onUngroupTabs: (String) -> Unit = {},
    val onDeleteGroup: (String) -> Unit = {},
    val onAddTabToGroup: (Long, String) -> Unit = { _, _ -> },
    val onRemoveTabFromGroup: (Long) -> Unit = {},
    val onCycleGroupColor: (String) -> Unit = {},
    val thumbnailProvider: (Long) -> Bitmap? = { null }
)

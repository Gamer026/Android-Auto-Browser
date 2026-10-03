/*
 * Car Browser  GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.tabs

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.kododake.aabrowser.bookmarks.BookmarkIconUtils
import com.kododake.aabrowser.databinding.ActivityMainBinding
import com.kododake.aabrowser.ui.compose.screens.tabs.TabActions
import com.kododake.aabrowser.ui.compose.screens.tabs.TabManagerSheet

object TabComposeHelper {
    fun setupComposeTabs(
        manager: TabManager,
        activity: AppCompatActivity,
        binding: ActivityMainBinding,
        callbacks: TabCallbacks
    ) {
        binding.tabComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val isVisible by manager.isVisibleState
                val tabs by manager.tabsState
                val keepScrim by manager.keepScrimState
                val gridEntries by manager.gridEntriesState
                val tabSearchQuery by manager.tabSearchQueryState
                val requestTabSearchFocus by manager.requestTabSearchFocusState

                TabManagerSheet(
                    isVisible = isVisible,
                    tabs = tabs,
                    animateEnter = true,
                    keepScrimOnClose = keepScrim,
                    onProgress = callbacks::onSheetProgress,
                    actions = TabActions(
                        onSelectTab = { tabId -> manager.dismissTabManagerToPage { manager.switchToTab(tabId) } },
                        onCloseTab = { tabId ->
                            manager.closeTab(tabId) { callbacks.onSpeechTabClosed(tabId) }
                        },
                        onNewTab = { manager.dismissTabManagerToPage { manager.createNewTab(true) } },
                        onReorderTabs = { from, to -> manager.reorderTabs(from, to) },
                        onCommitTabReorder = { manager.commitTabReorder() },
                        onClose = {
                            if (manager.isOpenedFromMenuState.value) {
                                manager.returnToMenu()
                            } else {
                                manager.hideTabManager()
                            }
                        },
                        onDismiss = { manager.hideTabManager() },
                        onDismissFinished = { manager.onTabDismissFinished() },
                        onGroupWithActive = { tabId -> manager.groupTabWithActive(tabId) },
                        onGroupWithTab = { tabId, partnerId -> manager.groupTwoTabs(tabId, partnerId) },
                        tabSearchQuery = tabSearchQuery,
                        onTabSearchQueryChange = { manager.tabSearchQueryState.value = it },
                        requestTabSearchFocus = requestTabSearchFocus,
                        onTabSearchFocusConsumed = { manager.clearTabSearchFocusRequest() },
                        gridEntries = gridEntries,
                        onCloseGroup = { groupId -> manager.closeGroupTabs(groupId) },
                        onRenameGroup = { groupId, newTitle -> manager.renameGroup(groupId, newTitle) },
                        onUngroupTabs = { groupId -> manager.ungroupAllTabs(groupId) },
                        onDeleteGroup = { groupId -> manager.deleteGroup(groupId) },
                        onAddTabToGroup = { tabId, groupId -> manager.addTabToGroup(tabId, groupId) },
                        onRemoveTabFromGroup = { tabId -> manager.removeTabFromGroup(tabId) },
                        thumbnailProvider = { tabId -> manager.getTabThumbnail(tabId) }
                    ),
                    faviconProvider = { url ->
                        BookmarkIconUtils.resolveCachedSiteIcon(activity, url) { manager.refreshTabs() }
                    }
                )
            }
        }
    }
}

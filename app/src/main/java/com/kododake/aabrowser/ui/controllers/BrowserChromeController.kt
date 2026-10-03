/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.ui.controllers

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.kododake.aabrowser.R
import com.kododake.aabrowser.databinding.ActivityMainBinding
import com.kododake.aabrowser.tabs.TabManager
import com.kododake.aabrowser.ui.compose.screens.startpage.BraveBottomNavigationBar
import com.kododake.aabrowser.ui.compose.theme.AABrowserTheme

class BrowserChromeController(
    private val binding: ActivityMainBinding,
    private val tabManager: TabManager
) {
    private val barVisibleState = mutableStateOf(true)
    var isBarVisible: Boolean
        get() = barVisibleState.value
        private set(value) {
            barVisibleState.value = value
        }

    private val bottomInsetPx: Int
        get() = binding.root.resources.getDimensionPixelSize(R.dimen.browser_chrome_bottom_bar_height)

    fun setup(
        onHome: () -> Unit,
        onBookmarks: () -> Unit,
        onSearch: () -> Unit,
        onTabs: () -> Unit,
        onMenu: () -> Unit
    ) {
        binding.browserBottomBarComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AABrowserTheme(darkTheme = true) {
                    val tabs by tabManager.tabsState
                    if (barVisibleState.value) {
                        BraveBottomNavigationBar(
                            openTabCount = tabs.size.coerceAtLeast(1),
                            onHome = onHome,
                            onBookmarks = onBookmarks,
                            onSearch = onSearch,
                            onTabs = onTabs,
                            onMenu = onMenu
                        )
                    }
                }
            }
        }
        setBottomBarVisible(true)
    }

    fun setBottomBarVisible(visible: Boolean) {
        isBarVisible = visible
        binding.browserBottomBarComposeView.visibility = if (visible) View.VISIBLE else View.GONE
        applyContentBottomInset(if (visible) bottomInsetPx else 0)
    }

    fun applyContentBottomInset(bottomPx: Int) {
        val params = binding.contentRoot.layoutParams as CoordinatorLayout.LayoutParams
        if (params.bottomMargin != bottomPx) {
            params.bottomMargin = bottomPx
            binding.contentRoot.layoutParams = params
        }
    }

    fun contentBottomInsetPx(): Int = if (isBarVisible) bottomInsetPx else 0
}

/*
 * Car Browser ù GPLv3 derivative. See LICENSE.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://gnu.org>.
 */

package com.kododake.aabrowser.ui.controllers

import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.kododake.aabrowser.ui.compose.screens.menu.BrowserMenuFab
import com.kododake.aabrowser.data.prefs.DownloadHistoryPreferences
import com.kododake.aabrowser.data.prefs.NavigationHistoryPreferences
import com.kododake.aabrowser.ui.compose.screens.menu.BraveBrowserMenuSheet
import com.kododake.aabrowser.ui.compose.screens.menu.MenuSubscreen
import com.kododake.aabrowser.ui.compose.screens.menu.MenuActions
import com.kododake.aabrowser.ui.compose.screens.menu.MenuStateHolder

/**
 * Helper to manage Compose browser menu lifecycle and reactive state bridge.
 */
class MenuSetupHelper {
    val stateHolder = MenuStateHolder()
    private var hostContext: android.content.Context? = null

    fun setup(composeView: ComposeView, actions: MenuActions) {
        hostContext = composeView.context
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                BraveBrowserMenuSheet(
                    stateHolder = stateHolder,
                    actions = actions
                )
            }
        }
    }

    fun setupFab(composeView: ComposeView, onClick: () -> Unit) {
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                BrowserMenuFab(
                    stateHolder = stateHolder,
                    onClick = onClick
                )
            }
        }
    }

    fun showFab() { stateHolder.isFabVisible = true }
    fun hideFab() { stateHolder.isFabVisible = false }
    fun setFabMode(addressBarMode: Boolean) { stateHolder.fabIsAddressBarMode = addressBarMode }

    fun updateNavigation(
        canBack: Boolean,
        canForward: Boolean,
        canRel: Boolean,
        desktop: Boolean,
        canQr: Boolean = false
    ) {
        stateHolder.updateNavigationState(canBack, canForward, canRel, desktop, canQr)
    }

    fun updatePage(url: String, title: String? = null) {
        stateHolder.updatePageInfo(url, title)
    }

    fun updateVersion(version: String) {
        stateHolder.versionName = version
    }

    fun showMenu() {
        refreshMenuLists()
        stateHolder.menuSubscreen = MenuSubscreen.MAIN
        stateHolder.isMenuVisible = true
    }

    fun hideMenu() {
        stateHolder.isMenuVisible = false
        stateHolder.menuSubscreen = MenuSubscreen.MAIN
    }

    fun refreshMenuLists() {
        val ctx = hostContext ?: return
        stateHolder.historyEntries = NavigationHistoryPreferences.getEntries(ctx)
        stateHolder.downloadEntries = DownloadHistoryPreferences.getUrls(ctx)
    }
}

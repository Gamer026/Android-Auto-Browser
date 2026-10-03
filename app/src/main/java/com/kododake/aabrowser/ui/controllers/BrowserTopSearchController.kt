/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.ui.controllers

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.kododake.aabrowser.databinding.ActivityMainBinding
import com.kododake.aabrowser.ui.compose.components.BraveSearchBar
import com.kododake.aabrowser.ui.compose.theme.AABrowserTheme

class BrowserTopSearchController(
    private val binding: ActivityMainBinding,
    private val onNavigate: (String) -> Unit
) {
    private var isVisible by mutableStateOf(false)
    private var requestFocus by mutableStateOf(false)
    private var query by mutableStateOf("")

    init {
        binding.browserTopSearchComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AABrowserTheme(darkTheme = true) {
                    if (isVisible) {
                        BraveSearchBar(
                            query = query,
                            onQueryChange = { query = it },
                            onNavigate = { raw ->
                                hide()
                                onNavigate(raw)
                            },
                            requestFocus = requestFocus,
                            onFocusConsumed = { requestFocus = false }
                        )
                    }
                }
            }
        }
    }

    fun show(prefill: String = "", focus: Boolean = true) {
        query = prefill
        requestFocus = focus
        isVisible = true
        binding.browserTopSearchComposeView.visibility = View.VISIBLE
    }

    fun hide() {
        isVisible = false
        requestFocus = false
        binding.browserTopSearchComposeView.visibility = View.GONE
    }

    val isShowing: Boolean
        get() = isVisible
}

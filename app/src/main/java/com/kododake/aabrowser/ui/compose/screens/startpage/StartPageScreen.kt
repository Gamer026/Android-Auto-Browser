package com.kododake.aabrowser.ui.compose.screens.startpage

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kododake.aabrowser.R
import com.kododake.aabrowser.ui.compose.components.bouncyClickable
import com.kododake.aabrowser.ui.compose.theme.AABrowserTheme

data class StartPageScreenCallbacks(
    val onNavigate: (String) -> Unit = {},
    val onSlotClick: (Int, String?) -> Unit = { _, _ -> },
    val onMoveSlot: (Int, Int) -> Unit = { _, _ -> },
    val onClearSlot: (Int) -> Unit = {},
    val onUpdateSlot: (Int, String) -> Unit = { _, _ -> },
    val onOpenSlotUrl: (String) -> Unit = {},
    val onResumeClick: () -> Unit = {},
    val onOpenBookmarks: () -> Unit = {},
    val onOpenTabs: () -> Unit = {},
    val onOpenMenu: () -> Unit = {},
    val openTabCount: () -> Int = { 1 },
)

private data class ActiveSlotDialog(val index: Int, val url: String)

@Composable
fun StartPageScreen(
    context: Context,
    slots: List<StartPageSlotUi>,
    hasResumePage: Boolean,
    customBackgroundBitmap: Bitmap? = null,
    isNavigating: Boolean = false,
    requestTopSearchFocus: Boolean = false,
    onTopSearchFocusConsumed: () -> Unit = {},
    callbacks: StartPageScreenCallbacks = StartPageScreenCallbacks(),
    modifier: Modifier = Modifier
) {
    var activeSlotDialog by remember { mutableStateOf<ActiveSlotDialog?>(null) }
    val dragState = rememberStartPageQuickLinksDragState(slots, callbacks.onMoveSlot)

    AABrowserTheme(darkTheme = true) {
        Box(modifier = modifier.fillMaxSize()) {
            BraveHomeBackground(customBackgroundBitmap = customBackgroundBitmap)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BraveStartPageTopBar(
                    onNavigate = callbacks.onNavigate,
                    requestFocus = requestTopSearchFocus,
                    onFocusConsumed = onTopSearchFocusConsumed
                )

                Spacer(Modifier.height(12.dp))

                BraveShortcutsPanel(
                    slots = slots,
                    dragState = dragState,
                    onSlotClick = { index, url ->
                        if (url.isNullOrBlank()) {
                            callbacks.onSlotClick(index, url)
                        } else {
                            activeSlotDialog = ActiveSlotDialog(index, url)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                )

                if (hasResumePage) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.start_page_resume_last_page),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFFB542B),
                        modifier = Modifier
                            .bouncyClickable { callbacks.onResumeClick() }
                            .padding(8.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = stringResource(R.string.start_page_photo_credit),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 12.dp)
                )
            }

            activeSlotDialog?.let { dialog ->
                StartPageSlotOptionsDialog(
                    slotIndex = dialog.index,
                    currentUrl = dialog.url,
                    onOpen = { callbacks.onOpenSlotUrl(dialog.url) },
                    onSaveUrl = { newUrl -> callbacks.onUpdateSlot(dialog.index, newUrl) },
                    onRemove = { callbacks.onClearSlot(dialog.index) },
                    onDismiss = { activeSlotDialog = null }
                )
            }
        }
    }
}

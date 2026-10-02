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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kododake.aabrowser.ui.compose.components.DynamicWallpaperBackground
import com.kododake.aabrowser.ui.compose.theme.AABrowserTheme

data class StartPageScreenCallbacks(
    val onNavigate: (String) -> Unit = {},
    val onSlotClick: (Int, String?) -> Unit = { _, _ -> },
    val onMoveSlot: (Int, Int) -> Unit = { _, _ -> },
    val onClearSlot: (Int) -> Unit = {},
    val onUpdateSlot: (Int, String) -> Unit = { _, _ -> },
    val onOpenSlotUrl: (String) -> Unit = {},
    val onResumeClick: () -> Unit = {},
)

private data class ActiveSlotDialog(val index: Int, val url: String)

@Composable
fun StartPageScreen(
    context: Context,
    slots: List<StartPageSlotUi>,
    hasResumePage: Boolean,
    customBackgroundBitmap: Bitmap? = null,
    isNavigating: Boolean = false,
    callbacks: StartPageScreenCallbacks = StartPageScreenCallbacks(),
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var activeSlotDialog by remember { mutableStateOf<ActiveSlotDialog?>(null) }

    AABrowserTheme {
        Box(modifier = modifier.fillMaxSize()) {
            DynamicWallpaperBackground(customBackgroundBitmap = customBackgroundBitmap)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StartPageHeader(
                    onNavigate = callbacks.onNavigate
                )

                Spacer(Modifier.height(16.dp))

                StartPageQuickLinks(
                    slots = slots,
                    onSlotClick = { index, url ->
                        if (url.isNullOrBlank()) {
                            callbacks.onSlotClick(index, url)
                        } else {
                            activeSlotDialog = ActiveSlotDialog(index, url)
                        }
                    },
                    onMoveSlot = callbacks.onMoveSlot,
                    onClearSlot = callbacks.onClearSlot
                )

                Spacer(Modifier.height(16.dp))

                StartPageActionRow(
                    hasResumePage = hasResumePage,
                    onResumeClick = callbacks.onResumeClick
                )

                Spacer(Modifier.height(48.dp))
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

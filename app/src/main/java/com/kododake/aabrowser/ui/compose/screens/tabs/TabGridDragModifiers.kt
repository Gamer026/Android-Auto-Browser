/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.ui.compose.screens.tabs

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot

fun Modifier.tabDropTarget(
    state: TabGridDragState,
    target: TabDropTarget,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed this
    TabDragTargetEffect(state, target)
    onGloballyPositioned { coordinates ->
        val pos = coordinates.positionInRoot()
        val size = coordinates.size
        state.updateTarget(
            target,
            Rect(pos.x, pos.y, pos.x + size.width, pos.y + size.height)
        )
    }
}

fun Modifier.tabCardDraggable(
    state: TabGridDragState,
    tabId: Long,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed this
    var positionInRoot by remember { mutableStateOf(Offset.Zero) }
    onGloballyPositioned { positionInRoot = it.positionInRoot() }
        .pointerInput(tabId) {
            detectDragGesturesAfterLongPress(
                onDragStart = { localOffset ->
                    state.onDragStart(tabId, positionInRoot + localOffset)
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    state.onDragDelta(dragAmount)
                },
                onDragEnd = { state.onDragEnd() },
                onDragCancel = { state.onDragCancel() }
            )
        }
}

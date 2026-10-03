/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.ui.compose.screens.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

sealed class TabDropTarget {
    data class Tab(val id: Long) : TabDropTarget()
    data class Group(val groupId: String) : TabDropTarget()
    data object UngroupZone : TabDropTarget()
}

@Stable
class TabGridDragState(
    private val onDrop: (draggedTabId: Long, target: TabDropTarget?) -> Unit
) {
    var draggedTabId by mutableStateOf<Long?>(null)
        private set
    var dragOffset by mutableStateOf(Offset.Zero)
        private set
    var highlightedTarget by mutableStateOf<TabDropTarget?>(null)
        private set

    private var dragAnchorInRoot = Offset.Zero
    private val targets = mutableStateMapOf<TabDropTarget, Rect>()

    fun updateTarget(target: TabDropTarget, boundsInRoot: Rect) {
        if (boundsInRoot.width > 0f && boundsInRoot.height > 0f) {
            targets[target] = boundsInRoot
        }
    }

    fun removeTarget(target: TabDropTarget) {
        targets.remove(target)
    }

    fun onDragStart(tabId: Long, pointerInRoot: Offset) {
        draggedTabId = tabId
        dragAnchorInRoot = pointerInRoot
        dragOffset = Offset.Zero
        highlightedTarget = null
    }

    fun onDragDelta(delta: Offset) {
        if (draggedTabId == null) return
        dragOffset += delta
        highlightedTarget = findTargetAt(dragAnchorInRoot + dragOffset)
    }

    fun onDragEnd() {
        val tabId = draggedTabId ?: return
        val target = highlightedTarget
        draggedTabId = null
        dragOffset = Offset.Zero
        highlightedTarget = null
        onDrop(tabId, target)
    }

    fun onDragCancel() {
        draggedTabId = null
        dragOffset = Offset.Zero
        highlightedTarget = null
    }

    fun isDragging(tabId: Long): Boolean = draggedTabId == tabId

    fun isHighlighted(target: TabDropTarget): Boolean = highlightedTarget == target

    val dragPositionInRoot: Offset
        get() = dragAnchorInRoot + dragOffset

    private fun findTargetAt(pointer: Offset): TabDropTarget? {
        val hits = targets.entries.filter { (_, rect) -> rect.contains(pointer) }
        if (hits.isEmpty()) return null
        val dragged = draggedTabId
        return hits
            .map { it.key }
            .sortedBy { priority(it) }
            .firstOrNull { target ->
                when (target) {
                    is TabDropTarget.Tab -> target.id != dragged
                    is TabDropTarget.Group -> true
                    TabDropTarget.UngroupZone -> true
                }
            }
    }

    private fun priority(target: TabDropTarget): Int = when (target) {
        is TabDropTarget.Tab -> 0
        is TabDropTarget.Group -> 1
        TabDropTarget.UngroupZone -> 2
    }
}

@Composable
fun rememberTabGridDragState(onDrop: (Long, TabDropTarget?) -> Unit): TabGridDragState {
    return remember { TabGridDragState(onDrop) }
}

@Composable
fun TabDragTargetEffect(state: TabGridDragState, target: TabDropTarget) {
    DisposableEffect(target) {
        onDispose { state.removeTarget(target) }
    }
}

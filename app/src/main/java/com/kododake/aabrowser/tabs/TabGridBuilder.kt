/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.tabs

import android.content.Context
import com.kododake.aabrowser.data.prefs.TabGroupPreferences
import com.kododake.aabrowser.ui.compose.screens.tabs.TabGridEntry
import com.kododake.aabrowser.ui.compose.screens.tabs.TabItemUi

object TabGridBuilder {
    fun build(context: Context, tabs: List<TabItemUi>): List<TabGridEntry> {
        if (tabs.isEmpty()) return emptyList()
        val seenGroups = mutableSetOf<String>()
        val entries = mutableListOf<TabGridEntry>()
        for (tab in tabs) {
            val groupId = tab.groupId
            if (groupId.isNullOrBlank()) {
                entries.add(TabGridEntry.Single(tab))
            } else if (groupId !in seenGroups) {
                seenGroups.add(groupId)
                val groupTabs = tabs.filter { it.groupId == groupId }
                val title = TabGroupPreferences.getGroupTitle(context, groupId)
                val colorIndex = TabGroupPreferences.getGroupColorIndex(context, groupId) ?: 0
                entries.add(TabGridEntry.Group(groupId, title, groupTabs, colorIndex))
            }
        }
        return entries
    }
}

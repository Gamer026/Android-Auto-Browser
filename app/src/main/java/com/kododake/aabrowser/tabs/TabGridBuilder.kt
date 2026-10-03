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
        val grouped = tabs.filter { !it.groupId.isNullOrBlank() }.groupBy { it.groupId!! }
        val ungrouped = tabs.filter { it.groupId.isNullOrBlank() }
        val entries = mutableListOf<TabGridEntry>()
        grouped.forEach { (groupId, groupTabs) ->
            val title = TabGroupPreferences.getGroupTitle(context, groupId)
            entries.add(TabGridEntry.Group(groupId, title, groupTabs))
        }
        ungrouped.forEach { entries.add(TabGridEntry.Single(it)) }
        return entries
    }
}

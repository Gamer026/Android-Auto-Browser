/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.data.prefs

import android.content.Context
import org.json.JSONObject

object TabGroupPreferences {
    private const val PREFS = "tab_group_prefs"
    private const val KEY_TAB_TO_GROUP = "tab_to_group"
    private const val KEY_GROUP_TITLES = "group_titles"

    fun getGroupId(context: Context, tabId: Long): String? {
        val map = loadTabToGroup(context)
        return map[tabId.toString()]
    }

    fun getGroupTitle(context: Context, groupId: String): String {
        val titles = loadGroupTitles(context)
        return titles[groupId] ?: groupId
    }

    fun setGroupTitle(context: Context, groupId: String, title: String) {
        val titles = loadGroupTitles(context).toMutableMap()
        titles[groupId] = title
        saveGroupTitles(context, titles)
    }

    fun assignTabToGroup(context: Context, tabId: Long, groupId: String?) {
        val map = loadTabToGroup(context).toMutableMap()
        if (groupId == null) {
            map.remove(tabId.toString())
        } else {
            map[tabId.toString()] = groupId
        }
        saveTabToGroup(context, map)
    }

    fun createGroupForTabs(context: Context, tabIds: List<Long>, title: String): String {
        val groupId = "group_${System.currentTimeMillis()}"
        tabIds.forEach { assignTabToGroup(context, it, groupId) }
        setGroupTitle(context, groupId, title)
        return groupId
    }

    fun tabsInGroup(context: Context, groupId: String, allTabIds: List<Long>): List<Long> {
        val map = loadTabToGroup(context)
        return allTabIds.filter { map[it.toString()] == groupId }
    }

    fun removeTabFromGroups(context: Context, tabId: Long) {
        assignTabToGroup(context, tabId, null)
    }

    private fun loadTabToGroup(context: Context): Map<String, String> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_TAB_TO_GROUP, null) ?: return emptyMap()
        return runCatching {
            val json = JSONObject(raw)
            buildMap {
                json.keys().forEach { key ->
                    put(key, json.getString(key))
                }
            }
        }.getOrDefault(emptyMap())
    }

    private fun saveTabToGroup(context: Context, map: Map<String, String>) {
        val json = JSONObject()
        map.forEach { (k, v) -> json.put(k, v) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TAB_TO_GROUP, json.toString())
            .apply()
    }

    private fun loadGroupTitles(context: Context): Map<String, String> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_GROUP_TITLES, null) ?: return emptyMap()
        return runCatching {
            val json = JSONObject(raw)
            buildMap {
                json.keys().forEach { key ->
                    put(key, json.getString(key))
                }
            }
        }.getOrDefault(emptyMap())
    }

    private fun saveGroupTitles(context: Context, map: Map<String, String>) {
        val json = JSONObject()
        map.forEach { (k, v) -> json.put(k, v) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_GROUP_TITLES, json.toString())
            .apply()
    }
}

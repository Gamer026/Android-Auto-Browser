/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.data.prefs

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object NavigationHistoryPreferences {
    private const val PREFS = "navigation_history_prefs"
    private const val KEY_ENTRIES = "entries"
    private const val MAX_ENTRIES = 200

    data class HistoryEntry(
        val url: String,
        val title: String,
        val visitedAtMs: Long
    )

    fun addVisit(context: Context, url: String, title: String) {
        if (url.isBlank() || url.startsWith("file://")) return
        val entries = getEntries(context).toMutableList()
        entries.removeAll { it.url == url }
        entries.add(0, HistoryEntry(url, title.ifBlank { url }, System.currentTimeMillis()))
        while (entries.size > MAX_ENTRIES) {
            entries.removeAt(entries.lastIndex)
        }
        save(context, entries)
    }

    fun getEntries(context: Context): List<HistoryEntry> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_ENTRIES, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        HistoryEntry(
                            url = obj.getString("url"),
                            title = obj.optString("title", obj.getString("url")),
                            visitedAtMs = obj.optLong("visitedAtMs", 0L)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(KEY_ENTRIES).apply()
    }

    private fun save(context: Context, entries: List<HistoryEntry>) {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(
                JSONObject()
                    .put("url", entry.url)
                    .put("title", entry.title)
                    .put("visitedAtMs", entry.visitedAtMs)
            )
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ENTRIES, array.toString())
            .apply()
    }
}

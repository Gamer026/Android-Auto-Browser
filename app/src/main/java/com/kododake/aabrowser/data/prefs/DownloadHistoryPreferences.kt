/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.data.prefs

import android.content.Context
import org.json.JSONArray

object DownloadHistoryPreferences {
    private const val PREFS = "download_history_prefs"
    private const val KEY_URLS = "urls"
    private const val MAX_ENTRIES = 50

    fun addDownload(context: Context, url: String) {
        if (url.isBlank()) return
        val entries = getUrls(context).toMutableList()
        entries.remove(url)
        entries.add(0, url)
        while (entries.size > MAX_ENTRIES) {
            entries.removeAt(entries.lastIndex)
        }
        save(context, entries)
    }

    fun getUrls(context: Context): List<String> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_URLS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    add(array.getString(i))
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun save(context: Context, urls: List<String>) {
        val array = JSONArray()
        urls.forEach { array.put(it) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_URLS, array.toString())
            .apply()
    }
}

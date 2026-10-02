/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 *
 * Private build: icons come only from WebView favicons (cacheIcon) â€” no remote favicon HTTP.
 */

package com.kododake.aabrowser.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

object SiteIconCache {
    private const val ICON_DIR = "site-icons-v2"

    private val diskExecutor = Executors.newSingleThreadExecutor()

    private val memoryCache: LruCache<String, Bitmap> by lazy {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 16
        object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }

    fun getCachedIcon(context: Context, url: String?): Bitmap? {
        val hostKey = hostKey(url) ?: return null
        val memBitmap = memoryCache.get(hostKey)
        if (memBitmap != null) return memBitmap

        val file = iconFile(context, url) ?: return null
        if (!file.exists()) return null
        val diskBitmap = runCatching { BitmapFactory.decodeFile(file.absolutePath) }.getOrNull()
        if (diskBitmap != null) {
            memoryCache.put(hostKey, diskBitmap)
        }
        return diskBitmap
    }

    fun cacheIcon(context: Context, pageUrl: String?, bitmap: Bitmap?, overwriteExisting: Boolean = false) {
        if (bitmap == null) return
        val hostKey = hostKey(pageUrl) ?: return
        if (!overwriteExisting && memoryCache.get(hostKey) != null) return
        val file = iconFile(context, pageUrl) ?: return
        if (!overwriteExisting && file.exists()) return
        memoryCache.put(hostKey, bitmap)
        diskExecutor.execute {
            runCatching {
                file.parentFile?.mkdirs()
                FileOutputStream(file).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }
            }
        }
    }

    fun prefetchIconIfNeeded(
        context: Context,
        pageUrl: String?,
        onComplete: (Bitmap?) -> Unit = {}
    ) {
        onComplete(getCachedIcon(context, pageUrl))
    }

    private fun iconFile(context: Context, url: String?): File? {
        val hostKey = hostKey(url) ?: return null
        return File(File(context.cacheDir, ICON_DIR), "$hostKey.png")
    }

    fun hostKey(url: String?): String? {
        val host = extractHost(url) ?: return null
        return host.replace(Regex("[^a-z0-9._-]"), "_").takeIf { it.isNotBlank() }
    }

    fun extractHost(url: String?): String? {
        val raw = url?.trim() ?: return null
        if (raw.isBlank()) return null
        val normalized = if (raw.contains("://")) raw else "https://$raw"
        val host = runCatching { Uri.parse(normalized).host?.lowercase() }.getOrNull() ?: return null
        val cleaned = host.removePrefix("www.")
        return cleaned.takeIf { it.isNotBlank() }
    }
}

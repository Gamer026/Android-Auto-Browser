/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 *
 * Private build: no network update checks.
 */

package com.kododake.aabrowser.ui.compose.screens.version

import android.app.Activity
import com.kododake.aabrowser.BuildConfig

object VersionFetcher {

    fun fetchLatestVersion(
        activity: Activity,
        onSuccess: (latestUrl: String, tagName: String) -> Unit,
        onError: () -> Unit
    ) {
        activity.runOnUiThread {
            val tag = "v${BuildConfig.VERSION_NAME}"
            onSuccess(BuildConfig.VERSION_NAME, tag)
        }
    }
}

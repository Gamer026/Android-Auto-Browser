/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 *
 * Palette inspired by Chromium tab-group colors (UX reference only).
 */

package com.kododake.aabrowser.tabs

import androidx.compose.ui.graphics.Color

object TabGroupColors {
    val palette: List<Color> = listOf(
        Color(0xFF2952C8), // blue (default)
        Color(0xFF1E8E3E), // green
        Color(0xFF1967D2), // lighter blue
        Color(0xFF9334E6), // purple
        Color(0xFFD93025), // red
        Color(0xFFF9AB00), // yellow
        Color(0xFF12B5CB), // cyan
        Color(0xFF5F6368), // grey
    )

    fun colorForIndex(index: Int): Color = palette[index.mod(palette.size)]

    fun indexOfColor(color: Color): Int {
        val idx = palette.indexOfFirst { it == color }
        return if (idx >= 0) idx else 0
    }
}

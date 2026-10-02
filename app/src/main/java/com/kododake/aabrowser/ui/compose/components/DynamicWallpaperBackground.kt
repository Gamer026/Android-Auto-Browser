/*
 * Car Browser � GPLv3 derivative. See LICENSE.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://gnu.org>.
 */

package com.kododake.aabrowser.ui.compose.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale

/**
 * Universal dynamic background displaying custom wallpaper image if configured,
 * or a vibrant dynamic theme gradient based on Material 3 Expressive device colors.
 */
@Composable
fun DynamicWallpaperBackground(
    customBackgroundBitmap: Bitmap? = null,
    dimOverlayAlpha: Float = 0.15f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (customBackgroundBitmap != null && !customBackgroundBitmap.isRecycled) {
            Image(
                bitmap = customBackgroundBitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = dimOverlayAlpha))
            )
        } else {
            val isDark = isSystemInDarkTheme()
            val primaryContainer = MaterialTheme.colorScheme.primaryContainer
            val secondaryContainer = MaterialTheme.colorScheme.secondaryContainer
            val tertiaryContainer = MaterialTheme.colorScheme.tertiaryContainer

            val primary = MaterialTheme.colorScheme.primary
            val gradientColors = if (isDark) {
                listOf(
                    lerp(MaterialTheme.colorScheme.surface, secondaryContainer, 0.55f),
                    lerp(MaterialTheme.colorScheme.surface, tertiaryContainer, 0.50f),
                    lerp(MaterialTheme.colorScheme.surface, primaryContainer, 0.45f),
                    lerp(MaterialTheme.colorScheme.surface, primary, 0.25f)
                )
            } else {
                listOf(
                    secondaryContainer.copy(alpha = 0.95f),
                    tertiaryContainer.copy(alpha = 0.90f),
                    primaryContainer.copy(alpha = 0.92f),
                    primary.copy(alpha = 0.18f)
                )
            }

            val gradientBrush = remember(gradientColors) {
                Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush)
            )

            val ambientBrush = remember(primaryContainer, isDark) {
                Brush.radialGradient(
                    colors = listOf(
                        primaryContainer.copy(alpha = if (isDark) 0.35f else 0.45f),
                        Color.Transparent
                    ),
                    radius = 950f
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ambientBrush)
            )
        }
    }
}

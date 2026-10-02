/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.ui.compose.screens.startpage

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kododake.aabrowser.R

@Composable
fun StartPageHeroBanner(
    showHero: Boolean,
    modifier: Modifier = Modifier
) {
    if (!showHero) return

    val cornerShape = RoundedCornerShape(20.dp)
    val elevation = if (isSystemInDarkTheme()) 2.dp else 4.dp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(cornerShape),
        shape = cornerShape,
        shadowElevation = elevation,
        tonalElevation = elevation
    ) {
        Image(
            painter = painterResource(R.drawable.start_page_hero_illustration),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

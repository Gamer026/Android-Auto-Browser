package com.kododake.aabrowser.ui.compose.screens.startpage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kododake.aabrowser.R
import com.kododake.aabrowser.ui.compose.components.bouncyClickable
import com.kododake.aabrowser.ui.compose.theme.ExpressiveTypography

@Composable
fun StartPageActionRow(
    hasResumePage: Boolean,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!hasResumePage) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StartPageActionButton(
            icon = Icons.Rounded.Refresh,
            text = stringResource(R.string.start_page_resume_last_page),
            onClick = onResumeClick
        )
    }
}

@Composable
private fun StartPageActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val containerColor = if (isDark) {
        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.90f)
    }
    val pillShape = RoundedCornerShape(24.dp)

    Surface(
        shape = pillShape,
        color = containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f)
        ),
        shadowElevation = 0.dp,
        modifier = modifier
            .height(46.dp)
            .bouncyClickable(
                shape = pillShape,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                style = ExpressiveTypography.labelLargeEmphasized
            )
        }
    }
}

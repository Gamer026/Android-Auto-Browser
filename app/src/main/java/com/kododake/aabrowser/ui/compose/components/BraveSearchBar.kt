/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.ui.compose.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kododake.aabrowser.R

@Composable
fun BraveSearchBar(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFF21262D),
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
) {
    var queryText by remember { mutableStateOf("") }
    val pillShape = RoundedCornerShape(28.dp)

    Surface(
        shape = pillShape,
        color = containerColor,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(14.dp))
            leadingIcon()
            Spacer(Modifier.width(10.dp))
            BasicTextField(
                value = queryText,
                onValueChange = { queryText = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (queryText.isNotBlank()) onNavigate(queryText)
                    }
                ),
                textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                ),
                cursorBrush = SolidColor(Color(0xFFFB542B)),
                decorationBox = { innerTextField ->
                    if (queryText.isEmpty()) {
                        Text(
                            text = stringResource(R.string.start_page_search_hint),
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.55f)
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            trailingIcon()
            Spacer(Modifier.width(14.dp))
        }
    }
}

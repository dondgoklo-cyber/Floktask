package com.taskmanager.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Avatar/attachment image with explicit size + downsampling (issue 45:
 * images loaded at full resolution). The [sizePx] hint tells Coil to
 * decode at the display size, not the source resolution.
 */
@Composable
fun OptimizedImage(
    model: Any?,
    contentDescription: String?,
    sizePx: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val request = ImageRequest.Builder(context)
        .data(model)
        .size(sizePx)
        .crossfade(true)
        .build()
    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}

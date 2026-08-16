package com.taskmanager.image

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds a battery/bandwidth-friendly Coil [ImageLoader] (issue 45:
 * attachments/avatars loaded at full resolution).
 *
 * - Memory cache: 25% of app heap
 * - Disk cache: 50MB with respect for cache dir limits
 * - Hardware bitmaps + crossfade; downsampling handled by Coil on decode
 */
@Singleton
class ImageLoaderFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun create(): ImageLoader = ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context).maxSizePercent(0.25).build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizeBytes(DISK_CACHE_BYTES)
                .build()
        }
        .crossfade(true)
        .respectCacheHeaders(true)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    companion object {
        private const val DISK_CACHE_BYTES = 50L * 1024 * 1024 // 50 MB
    }
}

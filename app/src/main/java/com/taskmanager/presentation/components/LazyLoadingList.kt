package com.taskmanager.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A lazily-loading list (issue 41: LazyColumn loaded all items → scroll lag
 * at 500+ tasks). Triggers [onLoadMore] when the user scrolls near the bottom
 * via a derived prefetch threshold, and shows a loading footer.
 */
@Composable
fun <T> LazyLoadingList(
    items: List<T>,
    key: (T) -> Any,
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    content: @Composable (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState: LazyListState = rememberLazyListState()

    // Trigger load-more when within PREFETCH_THRESHOLD items of the end.
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - PREFETCH_THRESHOLD
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoading) onLoadMore()
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        items(items, key = key) { item -> content(item) }
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        }
    }

}

private const val PREFETCH_THRESHOLD = 5

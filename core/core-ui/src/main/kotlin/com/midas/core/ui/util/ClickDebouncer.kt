package com.midas.core.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * Debounce interval for clicks in milliseconds.
 */
private const val DEBOUNCE_INTERVAL = 500L

/**
 * A Modifier extension that adds debounced click behavior.
 * Prevents multiple rapid clicks within the debounce interval.
 *
 * @param debounceInterval Time in milliseconds to wait between clicks
 * @param onClick Lambda to execute on debounced click
 */
fun Modifier.debouncedClickable(
    debounceInterval: Long = DEBOUNCE_INTERVAL,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    clickable {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= debounceInterval) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

/**
 * Remember a debounced version of a callback.
 * Useful for preventing multiple rapid invocations.
 *
 * @param debounceInterval Time in milliseconds to wait between calls
 * @param callback The original callback to debounce
 * @return A debounced version of the callback
 */
@Composable
fun rememberDebouncedCallback(
    debounceInterval: Long = DEBOUNCE_INTERVAL,
    callback: () -> Unit
): () -> Unit {
    var lastExecutionTime by remember { mutableLongStateOf(0L) }

    return remember(callback) {
        {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastExecutionTime >= debounceInterval) {
                lastExecutionTime = currentTime
                callback()
            }
        }
    }
}

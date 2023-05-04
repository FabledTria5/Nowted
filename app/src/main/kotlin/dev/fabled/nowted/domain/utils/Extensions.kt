package dev.fabled.nowted.domain.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Returns a list containing the results of applying the given [transform] function
 * to each element in the original collection.
 *
 * Each transformation performs asynchronous
 */
suspend fun <T, R> List<T>.mapAsync(transform: suspend (T) -> R): List<R> = coroutineScope {
    map { async { transform(it) } }.awaitAll()
}

/**
 * Returns non-null error message from [Throwable]
 */
val Throwable.errorMessage
    get() = message ?: stackTraceToString()
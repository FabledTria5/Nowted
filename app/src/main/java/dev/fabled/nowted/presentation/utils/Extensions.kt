package dev.fabled.nowted.presentation.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun <T, R> List<T>.mapAsync(transformation: suspend (T) -> R): List<R> = coroutineScope {
    map { async { transformation(it) } }.awaitAll()
}
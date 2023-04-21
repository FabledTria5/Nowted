package dev.fabled.nowted.domain.model

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val error: String, val data: T? = null) : Resource<T>()
    object Completed: Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

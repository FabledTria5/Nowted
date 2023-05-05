package dev.fabled.nowted.domain.model

/**
 * Returned as result of operation
 */
sealed class Resource<out T> {

    /**
     * Returns if operation is successful and has some data
     */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * Returns if operation fails and has error message
     */
    data class Error<T>(val error: String, val data: T? = null) : Resource<T>()

    /**
     * Returns to indicate operation finish successful
     */
    object Completed: Resource<Nothing>()

    /**
     * Returns if operation failed without error message
     */
    object Failure: Resource<Nothing>()
}

package com.amirmuhsin.listinghelper.core_views.result

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Represents different types of errors that can occur in the application
 */
sealed class ResultError {
    abstract val message: String
    abstract val cause: Throwable?

    /**
     * Network-related errors (connection, timeout, etc.)
     */
    data class NetworkError(
        override val message: String,
        override val cause: Throwable? = null,
        val isTimeout: Boolean = false
    ) : ResultError()

    /**
     * HTTP errors (4xx, 5xx responses)
     */
    data class HttpError(
        val code: Int,
        override val message: String,
        override val cause: Throwable? = null
    ) : ResultError()

    /**
     * Errors during data parsing
     */
    data class ParseError(
        override val message: String,
        override val cause: Throwable? = null
    ) : ResultError()

    /**
     * Business logic errors
     */
    data class BusinessError(
        override val message: String,
        override val cause: Throwable? = null
    ) : ResultError()

    /**
     * Unknown/unexpected errors
     */
    data class UnknownError(
        override val message: String,
        override val cause: Throwable? = null
    ) : ResultError()

    /**
     * Converts this error to an exception
     */
    fun toException(): Exception = when (this) {
        is NetworkError -> IOException(message, cause)
        is HttpError -> cause as? HttpException ?: IOException("HTTP $code: $message")
        is ParseError -> IllegalStateException(message, cause)
        is BusinessError -> IllegalStateException(message, cause)
        is UnknownError -> RuntimeException(message, cause)
    }

    companion object {
        /**
         * Creates a ResultError from an exception
         */
        fun from(exception: Throwable): ResultError = when (exception) {
            is SocketTimeoutException -> NetworkError(
                message = "Request timed out. Please check your connection and try again.",
                cause = exception,
                isTimeout = true
            )
            is UnknownHostException -> NetworkError(
                message = "Unable to connect to server. Please check your internet connection.",
                cause = exception
            )
            is IOException -> NetworkError(
                message = exception.message ?: "Network error occurred",
                cause = exception
            )
            is HttpException -> HttpError(
                code = exception.code(),
                message = when (exception.code()) {
                    400 -> "Bad request. Please check your input."
                    401 -> "Unauthorized. Please log in again."
                    403 -> "Access forbidden."
                    404 -> "Resource not found."
                    408 -> "Request timeout. Please try again."
                    429 -> "Too many requests. Please try again later."
                    in 500..599 -> "Server error. Please try again later."
                    else -> "HTTP error ${exception.code()}"
                },
                cause = exception
            )
            is NoSuchElementException -> BusinessError(
                message = exception.message ?: "Item not found",
                cause = exception
            )
            is IllegalArgumentException, is IllegalStateException -> BusinessError(
                message = exception.message ?: "Invalid operation",
                cause = exception
            )
            else -> UnknownError(
                message = exception.message ?: "An unexpected error occurred",
                cause = exception
            )
        }

        /**
         * Creates a network timeout error
         */
        fun timeout(message: String = "Request timed out"): ResultError = NetworkError(
            message = message,
            isTimeout = true
        )

        /**
         * Creates a network connectivity error
         */
        fun networkUnavailable(message: String = "No internet connection"): ResultError =
            NetworkError(message)

        /**
         * Creates a business logic error
         */
        fun business(message: String, cause: Throwable? = null): ResultError =
            BusinessError(message, cause)
    }
}

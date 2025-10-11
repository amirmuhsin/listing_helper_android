package com.amirmuhsin.listinghelper.core_views.result

/**
 * A simple Result monad for error handling without throwing exceptions.
 * Represents either a Success with data or a Failure with an error.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val error: ResultError) : Result<Nothing>()

    /**
     * Returns true if this is a Success
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Returns true if this is a Failure
     */
    val isFailure: Boolean
        get() = this is Failure

    /**
     * Returns the data if Success, or null if Failure
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }

    /**
     * Returns the data if Success, or the default value if Failure
     */
    inline fun getOrElse(default: () -> @UnsafeVariance T): T = when (this) {
        is Success -> data
        is Failure -> default()
    }

    /**
     * Returns the data if Success, or throws the error if Failure
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Failure -> throw error.toException()
    }

    /**
     * Maps the success value using the given transform function
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> this
    }

    /**
     * Maps the success value using a transform that returns a Result
     */
    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> transform(data)
        is Failure -> this
    }

    /**
     * Executes the given action if this is a Success
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Executes the given action if this is a Failure
     */
    inline fun onFailure(action: (ResultError) -> Unit): Result<T> {
        if (this is Failure) action(error)
        return this
    }

    /**
     * Recovers from a Failure by providing a default value
     */
    inline fun recover(recovery: (ResultError) -> @UnsafeVariance T): Result<T> = when (this) {
        is Success -> this
        is Failure -> Success(recovery(error))
    }

    companion object {
        /**
         * Creates a Success result
         */
        fun <T> success(data: T): Result<T> = Success(data)

        /**
         * Creates a Failure result
         */
        fun failure(error: ResultError): Result<Nothing> = Failure(error)

        /**
         * Creates a Failure result from an exception
         */
        fun failure(exception: Throwable): Result<Nothing> =
            Failure(ResultError.from(exception))

        /**
         * Wraps a potentially throwing operation in a Result
         */
        inline fun <T> runCatching(block: () -> T): Result<T> = try {
            Success(block())
        } catch (e: Exception) {
            Failure(ResultError.from(e))
        }
    }
}

/**
 * Extension function to convert Kotlin's built-in Result to our custom Result
 */
fun <T> kotlin.Result<T>.toResult(): Result<T> = fold(
    onSuccess = { Result.success(it) },
    onFailure = { Result.failure(it) }
)

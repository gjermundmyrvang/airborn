package no.uio.ifi.in2000.team18.airborn.ui.common

sealed interface LoadingState<out T> {
    fun <U> map(function: (T) -> U): LoadingState<U> = when (this) {
        is Success -> Success(function(this.value))
        is Error -> Error(this.message)
        is Loading -> Loading
    }

    data class Success<T>(val value: T) : LoadingState<T>
    data class Error(val message: String) : LoadingState<Nothing>
    data object Loading : LoadingState<Nothing>
}

fun <T> T.toSuccess() = LoadingState.Success(this)
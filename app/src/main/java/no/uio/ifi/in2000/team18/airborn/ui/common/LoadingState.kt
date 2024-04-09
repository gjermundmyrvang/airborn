package no.uio.ifi.in2000.team18.airborn.ui.common

sealed interface LoadingState<out T> {
    data class Success<T>(val value: T) : LoadingState<T>
    data class Error(val message: String) : LoadingState<Nothing>
    data object Loading : LoadingState<Nothing>
}

fun <T> T.toSuccess() = LoadingState.Success(this)
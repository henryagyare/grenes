package ui.screens

internal sealed class State<T> {
    class Loading<T> : State<T>()
    class Empty<T> : State<T>()
    data class Success<T>(val value: T) : State<T>()
    data class Error<T>(val message: String? = null) : State<T>()
}
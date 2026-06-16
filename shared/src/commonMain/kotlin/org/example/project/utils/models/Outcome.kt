package org.example.project.utils.models

sealed interface Outcome<out ValueType, out ErrorType> {
    data class Success<out ValueType>(
        val value: ValueType,
    ) : Outcome<ValueType, Nothing>

    data class Error<out ErrorType>(
        val code: ErrorType,
        val message: String? = null,
    ) : Outcome<Nothing, ErrorType>
}

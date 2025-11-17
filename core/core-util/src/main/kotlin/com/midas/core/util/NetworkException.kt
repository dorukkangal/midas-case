package com.midas.core.util

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class NetworkException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    data class BadRequest(
        override val message: String = "Bad Request"
    ) : NetworkException(message)

    data class Unauthorized(
        override val message: String = "Unauthorized"
    ) : NetworkException(message)

    data class Forbidden(
        override val message: String = "Forbidden"
    ) : NetworkException(message)

    data class NotFound(
        override val message: String = "Not Found"
    ) : NetworkException(message)

    data class TooManyRequests(
        override val message: String = "Too Many Requests"
    ) : NetworkException(message)

    data class ServerError(
        override val message: String = "Internal Server Error"
    ) : NetworkException(message)

    data class NoInternet(
        override val message: String = "No Internet Connection"
    ) : NetworkException(message)

    data class Timeout(
        override val message: String = "Request Timeout"
    ) : NetworkException(message)

    data class Unknown(
        override val message: String = "Unknown Error",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)
}

fun Throwable.toNetworkException(): NetworkException {
    return when (this) {
        is ClientRequestException -> {
            when (response.status) {
                HttpStatusCode.BadRequest -> NetworkException.BadRequest()
                HttpStatusCode.Unauthorized -> NetworkException.Unauthorized()
                HttpStatusCode.Forbidden -> NetworkException.Forbidden()
                HttpStatusCode.NotFound -> NetworkException.NotFound()
                HttpStatusCode.TooManyRequests -> NetworkException.TooManyRequests()
                else -> NetworkException.Unknown("Client Error: ${response.status}", this)
            }
        }

        is ServerResponseException -> {
            NetworkException.ServerError("Server Error: ${response.status}")
        }

        is RedirectResponseException -> {
            NetworkException.Unknown("Redirect Error: ${response.status}", this)
        }

        is UnknownHostException -> {
            NetworkException.NoInternet()
        }

        is SocketTimeoutException -> {
            NetworkException.Timeout()
        }

        else -> {
            NetworkException.Unknown(message ?: "Unknown error occurred", this)
        }
    }
}

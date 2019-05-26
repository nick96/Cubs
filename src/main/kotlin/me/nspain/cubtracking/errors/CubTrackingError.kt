package me.nspain.cubtracking.errors

import io.ktor.http.HttpStatusCode


data class CubTrackingError(
        val request: String,
        val message: String,
        val code: HttpStatusCode,
        val cause: Throwable? = null
)
package me.nspain.cubtracking.errors

import io.ktor.http.HttpStatusCode


data class CubTrackingError(
        val request: String,
        val code: HttpStatusCode,
        val message: Any?
)
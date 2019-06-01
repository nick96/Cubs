package me.nspain.cubtracking.security

import io.ktor.auth.Principal

data class User(
        val id: Long,
        val name: String
): Principal
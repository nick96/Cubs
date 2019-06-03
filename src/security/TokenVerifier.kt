package me.nspain.cubtracking.security

interface TokenVerifier {
    fun verify(token: String): User?
}
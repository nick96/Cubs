package me.nspain.cubtracking.security

import com.auth0.jwt.JWTVerifier

interface JWTVerifier {
    val verifier: JWTVerifier
    fun sign(name: String): String
}
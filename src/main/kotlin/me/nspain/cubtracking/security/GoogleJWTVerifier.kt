package me.nspain.cubtracking.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

open class GoogleJWTVerifier(val secret: String): JWTVerifier {
    private val algorithm = Algorithm.HMAC512(secret)
    override val verifier = JWT.require(algorithm).build()

    override fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
}
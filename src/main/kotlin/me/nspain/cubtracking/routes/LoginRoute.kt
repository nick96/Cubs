package me.nspain.cubtracking.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.call
import io.ktor.auth.UnauthorizedResponse
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import me.nspain.cubtracking.security.TokenVerifier
import java.util.*

data class AuthRequest(val token: String)
data class AuthResponse(val token: String)

fun Route.authentication(tokenVerifier: TokenVerifier, issuer: String, algorithm: Algorithm) {
    route("/authentication") {
        post {
            val body = call.receive<AuthRequest>()
            val resp = tokenVerifier.verify(body.token)
            if (resp != null) {
                val token = JWT.create()
                        .withSubject("Authentication")
                        .withIssuer(issuer)
                        .withClaim("id", resp.id)
                        .withExpiresAt(Date(System.currentTimeMillis() + 36_000_000))
                        .sign(algorithm)
                call.respond(AuthRequest(token))
            } else {
                call.respond(UnauthorizedResponse())
            }

        }
    }
}

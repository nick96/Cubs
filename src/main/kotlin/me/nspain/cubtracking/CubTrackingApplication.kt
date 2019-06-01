package me.nspain.cubtracking

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import me.nspain.cubtracking.errors.CubTrackingError
import me.nspain.cubtracking.repository.Repository
import me.nspain.cubtracking.repository.SolrRepository
import me.nspain.cubtracking.routes.*
import me.nspain.cubtracking.security.GoogleTokenVerifier
import me.nspain.cubtracking.security.TokenVerifier
import org.slf4j.event.Level
import java.text.DateFormat

data class SolrConfig(val connectionString: String)
data class JWTConfig(val secret: String, val issuer: String)

@InternalAPI
@KtorExperimentalAPI
fun main() {
    embeddedServer(Netty, port = 8080) {
        cubTracking()
    }.start()
}

@InternalAPI
@KtorExperimentalAPI
fun Application.cubTracking() {
    val cfg = ConfigFactory.load()
    val solrCfg = cfg.extract<SolrConfig>("solr")
    val jwtCfg = cfg.extract<JWTConfig>("jwt")

    val repo = SolrRepository(solrCfg.connectionString)
    val algo = Algorithm.HMAC256(jwtCfg.secret)
    val tokenVerifier = GoogleTokenVerifier()

    cubTracking(repo, algo, tokenVerifier, jwtCfg.issuer)
}

@InternalAPI
@KtorExperimentalAPI
fun Application.cubTracking(
        repository: Repository,
        algorithm: Algorithm,
        tokenVerifier: TokenVerifier,
        issuer: String) {

    install(DefaultHeaders)
    install(Compression)
    install(ConditionalHeaders)
    install(AutoHeadResponse)

    install(CORS) {
        anyHost()
        allowCredentials = true
        HttpMethod.DefaultMethods.forEach { method(it) }
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            environment.log.error(cause.toString())
            val err = CubTrackingError(
                    call.request.local.uri,
                    HttpStatusCode.InternalServerError,
                    "There was an issue on the server side"
                    )
            call.respond(err)
        }
    }

    install(CallLogging) {
        level = Level.valueOf("DEBUG")
    }

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    install(Authentication) {
        jwt("jwt") {
            verifier(JWT.require(algorithm).withIssuer(issuer).build())
            validate {
                JWTPrincipal(it.payload)
            }
        }
    }


    install(Routing) {
        authentication(tokenVerifier, issuer, algorithm)
        authenticate("jwt") {
            cub(repository.cubRepository)
            greyWolf(repository)
            boomerang(repository)
            achievementBadge(repository)
        }
    }

}
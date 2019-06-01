package me.nspain.cubtracking

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import me.nspain.cubtracking.errors.CubTrackingError
import me.nspain.cubtracking.repository.Repository
import me.nspain.cubtracking.repository.SolrRepository
import me.nspain.cubtracking.routes.achievementBadge
import me.nspain.cubtracking.routes.boomerang
import me.nspain.cubtracking.routes.cub
import me.nspain.cubtracking.routes.greyWolf
import me.nspain.cubtracking.security.GoogleJWTVerifier
import me.nspain.cubtracking.security.JWTVerifier
import org.slf4j.event.Level
import java.text.DateFormat

data class SolrConfig(val connectionString: String)
data class JWTConfig(val secret: String)

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

    cubTracking(SolrRepository(
            solrCfg.connectionString),
            GoogleJWTVerifier(jwtCfg.secret))
}

@InternalAPI
@KtorExperimentalAPI
fun Application.cubTracking(
        repository: Repository,
        jwtVerifier: JWTVerifier) {
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
                    cause.toString(),
                    HttpStatusCode.InternalServerError,
                    cause
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
            verifier(jwtVerifier.verifier)
        }
    }


    install(Routing) {
        authenticate("jwt") {
            cub(repository)
            greyWolf(repository)
            boomerang(repository)
            achievementBadge(repository)
        }
    }

}

private fun ApplicationCall.redirectUrl(path: String): String {
    val defaultPort = if (request.origin.scheme == "http") 80 else 443
    val hostPort = request.host() + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
    val protocol = request.origin.scheme
    return "$protocol://$hostPort$path"
}
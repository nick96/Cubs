package me.nspain.cubtracking

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import me.nspain.cubtracking.errors.CubTrackingError
import org.slf4j.event.Level
import java.text.DateFormat

fun Application.main() {
    install(DefaultHeaders)
    install(Compression)
    install(ConditionalHeaders)
    install(AutoHeadResponse)

    install(CORS) {
        anyHost()
        allowCredentials = true
        listOf(HttpMethod.Get, HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch, HttpMethod.Delete).forEach {
            method(it)
        }
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
        level = Level.DEBUG
    }

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    routing {
        get("/") {
            call.respondText("Example")
        }
    }
}
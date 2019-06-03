package me.nspain.cubtracking.routes

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.core.Validator
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import me.nspain.cubtracking.errors.CubTrackingError
import me.nspain.cubtracking.repository.DocumentRepository
import me.nspain.cubtracking.schemas.Cub

@KtorExperimentalLocationsAPI
@Location("/cubs")
class Cubs

@KtorExperimentalLocationsAPI
@Location("/cubs/{id}")
data class CubID(val id: Long)

@KtorExperimentalLocationsAPI
fun Route.cub(repository: DocumentRepository<Cub>) {
    // GET /cubs
    getCubs(repository)
    // GET /cubs/{id}
    getCubById(repository)
    // POST /cubs
    createCub(repository)
    // PUT /cubs/{id}
    replaceCub(repository)
    // DELETE /cubs/{id}
    deleteCubById(repository)
}

@KtorExperimentalLocationsAPI
fun Route.getCubs(repository: DocumentRepository<Cub>) {
    get<Cubs> {
        val cubs = repository.filter {
            var ok = true
            if (call.parameters["name"] != null) {
                ok = it.name == call.parameters["name"]
            }
            ok
        }

        if (cubs.isEmpty()) call.respond(HttpStatusCode.NotFound) else call.respond(cubs)
    }
}

@KtorExperimentalLocationsAPI
fun Route.getCubById(repository: DocumentRepository<Cub>) {
    get<CubID> {
        val cub = repository[it.id]
        if (cub == null) call.respond(HttpStatusCode.NotFound) else call.respond(cub)
    }
}

@KtorExperimentalLocationsAPI
fun Route.createCub(repository: DocumentRepository<Cub>) {
    post<Cubs> {
        val body = call.receive<Cub>()
        val validator: Validator<Cub> = ValidatorBuilder.of<Cub>()
                .konstraint(Cub::id) {
                    isNull()
                }
                .konstraint(Cub::name) {
                    notNull()
                }
                .build()
        val violations = validator.validate(body)
        if (!violations.isValid) {
            call.respond(HttpStatusCode.BadRequest, CubTrackingError(
                    call.request.local.uri,
                    HttpStatusCode.BadRequest,
                    mapOf("errors" to violations.map { it.message() })
            ))
        } else {
            val cub = repository.append(body)
            call.respond(HttpStatusCode.Created, cub)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.replaceCub(repository: DocumentRepository<Cub>) {
    // Replace registered cub with ID.
    put<CubID> {
        val body = call.receive<Cub>()
        val validator = ValidatorBuilder<Cub>()
                .konstraint(Cub::id) {
                    isNull()
                }
                .build()
        val violations = validator.validate(body)
        if (!violations.isValid) {
            call.respond(HttpStatusCode.BadRequest, CubTrackingError(
                    call.request.local.uri,
                    HttpStatusCode.BadRequest,
                    mapOf("errors" to violations.map { it.message() })
            ))
        } else {
            val newCub = repository.replace(it.id, body)
            if (newCub == null) call.respond(HttpStatusCode.NotFound, CubTrackingError(
                    call.request.local.uri,
                    HttpStatusCode.NotFound,
                    "No Cub document with ID ${it.id} was not found."
            )) else call.respond(newCub)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.deleteCubById(repository: DocumentRepository<Cub>) {
    // Delete a registered cub with ID.
    delete<CubID> {
        val deletedCub = repository.delete(it.id)
        if (deletedCub == null) call.respond(HttpStatusCode.NotFound) else call.respond(deletedCub)
    }
}
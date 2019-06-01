package me.nspain.cubtracking.routes

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.core.Validator
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.routing.*
import me.nspain.cubtracking.errors.CubTrackingError
import me.nspain.cubtracking.repository.DocumentRepository
import me.nspain.cubtracking.schemas.Cub

fun Route.cub(repository: DocumentRepository<Cub>) {
    route("/cubs") {
        // GET /cubs
        // Get all registered cubs
        get("/") {
            val cubs = repository.filter {
                var ok = true
                if (call.parameters["name"] != null) {
                    ok = it.name == call.parameters["name"]
                }
                ok
            }

            if (cubs.isEmpty()) call.respond(HttpStatusCode.NotFound) else call.respond(cubs)
        }

        // GET /cubs/{id}
        // Get a registered but with ID.
        get("/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val cub = repository[id]
            if (cub == null) call.respond(HttpStatusCode.NotFound) else call.respond(cub)
        }

        // POST /cubs
        // Create a new cub
        post("/") {
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

        // PUT /cubs/{id}
        // Replace registered cub with ID.
        put("/{id}") {
            val id = call.parameters["id"]!!.toLong()
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
                val newCub = repository.replace(id, body)
                if (newCub == null) call.respond(HttpStatusCode.NotFound, CubTrackingError(
                        call.request.local.uri,
                        HttpStatusCode.NotFound,
                        "No Cub document with ID $id was not found."
                )) else call.respond(newCub)
            }
        }

        // DELETE /cubs/{id}
        // Delete a registered cub with ID.
        delete("/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val deletedCub = repository.delete(id)
            if (deletedCub == null) call.respond(HttpStatusCode.NotFound) else call.respond(deletedCub)
        }
    }
}
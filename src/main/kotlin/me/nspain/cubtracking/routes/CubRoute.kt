package me.nspain.cubtracking.routes

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import me.nspain.cubtracking.repository.DocumentRepository
import me.nspain.cubtracking.schemas.Cub

fun Route.cub(repository: DocumentRepository<Cub, Cub.Update>) {
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
            val cub = repository.append(body)
            call.respond(
                    status = HttpStatusCode.Created,
                    message = cub
            )
        }

        // PUT /cubs/{id}
        // Replace registered cub with ID.
        put("/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val body = call.receive<Cub>()
            val newCub = repository.replace(id, body)
            if (newCub == null) call.respond(HttpStatusCode.NotFound) else call.respond(newCub)
        }

        // PATCH /cubs/{id}
        // Update a registered cub with ID.
        patch("/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val body = call.receive<Cub.Update>()
            val updatedCub = repository.update(id) {
                name = body.name
                achievementBadges = body.achievementBadges
                specialInterestBadges = body.specialInterestBadges
                bronzeBoomerang = body.bronzeBoomerang
                silverBoomerang = body.silverBoomerang
                goldBoomerang = body.goldBoomerang
            }
            if (updatedCub == null) call.respond(HttpStatusCode.NotFound) else call.respond(updatedCub)
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
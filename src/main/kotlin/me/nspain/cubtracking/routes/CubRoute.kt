package me.nspain.cubtracking.routes

import io.ktor.routing.*
import me.nspain.cubtracking.repository.Repository

fun Route.cub(repository: Repository) {
    route("/cubs") {
        // GET /cubs
        // Get all registered cubs
        get("/") {
            TODO()
        }

        // GET /cubs/{id}
        // Get a registered but with ID.
        get("/{id}") {
            TODO()
        }

        // POST /cubs
        // Create a new cub
        post("/") {
            TODO()
        }

        // PUT /cubs/{id}
        // Replace registered cub with ID.
        put("/{id}") {
            TODO()
        }

        // PATCH /cubs/{id}
        // Update a registered cub with ID.
        patch("/{id}") {
            TODO()
        }

        // DELETE /cubs/{id}
        // Delete a registered cub with ID.
        delete("/{id}") {
            TODO()
        }
    }
}
package me.nspain.cubtracking.routes

import io.ktor.routing.*
import me.nspain.cubtracking.repository.Repository


fun Route.boomerang(repository: Repository) {
    route("/boomerang") {
        // GET /badges/boomerang
        // Get all boomerang badges
        get("/") {
            TODO()
        }

        // GET /badges/boomerang/{id}
        // Get boomerang badge with ID.
        get("/{id}") {
            TODO()
        }

        // POST /badges/boomerang
        // Create a new boomerang badge
        post("/") {
            TODO()
        }

        // PUT /badges/boomerang/{id}
        // Replace the boomerang badge with ID.
        put("/{id}") {
            TODO()
        }

        // PATCH /badges/boomerang/{id}
        // Update the boomerang badge with ID.
        patch("/{id}") {
            TODO()
        }

        // DELETE /badges/boomerang/{id}
        // Delete the boomerang badge with ID.
        delete("/{id}") {
            TODO()
        }
    }
}
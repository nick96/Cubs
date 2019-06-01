package me.nspain.cubtracking.routes

import io.ktor.routing.*
import me.nspain.cubtracking.repository.Repository


fun Route.greyWolf(repository: Repository) {
    route("/greywolf") {
        // GET /badges/greywolf
        // Get all greywolf badges
        get("/") {
            TODO()
        }

        // GET /badges/greywolf/{id}
        // Get greywolf badge with ID.
        get("/{id}") {
            TODO()
        }

        // POST /badges/greywolf
        // Create a new greywolf badge
        post("/") {
            TODO()
        }

        // PUT /badges/greywolf/{id}
        // Replace the greywolf badge with ID.
        put("/{id}") {
            TODO()
        }

        // PATCH /badges/greywolf/{id}
        // Update the greywolf badge with ID.
        patch("/{id}") {
            TODO()
        }

        // DELETE /badges/greywolf/{id}
        // Delete the greywolf badge with ID.
        delete("/{id}") {
            TODO()
        }
    }
}
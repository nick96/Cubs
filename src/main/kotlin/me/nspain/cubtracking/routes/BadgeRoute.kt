package me.nspain.cubtracking.routes

import io.ktor.routing.*
import me.nspain.cubtracking.repository.Repository

fun Route.achievementBadge(repository: Repository) {
    route("/badges") {
        // GET /badges
        // Get all badges, broken up into their collections
        get("/") {
            TODO()
        }

        route("/achievement") {
            // GET /badges/achievement
            // Get all achievement badges
            get("/") {
                TODO()
            }

            // GET /badges/achievement/{id}
            // Get achievement badge with ID.
            get("/{id}") {
                TODO()
            }

            // POST /badges/achievement
            // Create a new achievement badge
            post("/") {
                TODO()
            }

            // PUT /badges/achievement/{id}
            // Replace the achievement badge with ID.
            put("/{id}") {
                TODO()
            }

            // PATCH /badges/achievement/{id}
            // Update the achievement badge with ID.
            patch("/{id}") {
                TODO()
            }

            // DELETE /badges/achievement/{id}
            // Delete the achievement badge with ID.
            delete("/{id}") {
                TODO()
            }
        }

        route("/specialinterest") {
            // GET /badges/specialinterest
            // Get all special interest badges
            get("/") {
                TODO()
            }

            // GET /badges/specialinterest/{id}
            // Get special interest badge with ID.
            get("/{id}") {
                TODO()
            }

            // POST /badges/specialinterest
            // Create a new special interest badge
            post("/") {
                TODO()
            }

            // PUT /badges/specialinterest/{id}
            // Replace the special interest badge with ID.
            put("/{id}") {
                TODO()
            }

            // PATCH /badges/specialinterest/{id}
            // Update the special interest badge with ID.
            patch("/{id}") {
                TODO()
            }

            // DELETE /badges/specialinterest/{id}
            // Delete the special interest badge with ID.
            delete("/{id}") {
                TODO()
            }
        }
    }
}
package me.nspain.cubtracking

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.hostWithPort
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.testing.*
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

@InternalAPI
@KtorExperimentalAPI
object CubRouteSpec : Spek({
    describe("Cubs") {
        describe("Authentication") {
            val engine = TestApplicationEngine(createTestEnvironment())
            engine.start(false)
            engine.application.cubTracking()

            with(engine) {
                val testCases = mapOf(
                        "GET" to listOf("/cubs", "/cubs/1"),
                        "PUT" to listOf("/cubs/1"),
                        "PATCH" to listOf("/cubs/1"),
                        "DELETE" to listOf("/cubs/1")

                )
                testCases.forEach { (method, paths) ->
                    paths.forEach { path ->
                        it("should return 401 if no 'Authorization' header is provided") {
                            handleRequest(HttpMethod(method), path).apply {
                                assertEquals(401, response.status()?.value)
                            }
                        }

                        it("should return 401 if the bearer token is not valid") {
                            assertEquals(401, handleRequest(HttpMethod(method), path) {
                                addHeader("Authorization", "dummy")
                            }.response.status()?.value)
                        }
                    }
                }
            }
        }

        describe("GET /cubs") {
            it("should return all the available cub documents and a 200 status code") {

            }

            it("should filter cubs by name") {

            }

            it("should filter cubs by the status for a an achievement") {

            }
        }

        describe("GET /cubs/{id}") {
            it("should return cub with the ID if they exist and a 200 status code") {

            }

            it("should return a 404 status code if the ID does not exist") {

            }
        }

        describe("POST /cubs") {
            it("should create a new cub, return a 201 status code and return the created cub document") {

            }

            it("should return a 400 status code if the cub document is not full and valid") {

            }
        }

        describe("PUT /cubs/{id}") {
            it("should replace the cub document with the ID and return a 204 status code") {

            }

            it("should return a 404 status code if the ID does not exist") {

            }

            it("should return a 400 status code if the cub document is not full and valid") {

            }
        }

        describe("PATCH /cubs/{id}") {
            it("should update the cub document with the ID and return a 204 status code if the ID exists") {

            }

            it("should return a 404 status code if the ID does not exist") {

            }

            it("should return a 400 status code if the cub document contains unknown fields") {

            }
        }

        describe("DELETE /cubs/{id}") {
            it("should return delete the cub with ID, return a 200 status code and return the deleted document if the ID exists") {

            }

            it("should return a 404 status code if the ID does not exist") {

            }
        }
    }
})
package me.nspain.cubtracking

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.GsonBuilder
import io.ktor.http.*
import io.ktor.request.contentType
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import me.nspain.cubtracking.schemas.Cub
import me.nspain.cubtracking.utils.DummyAlwaysTrueTokenVerifier
import me.nspain.cubtracking.utils.addJwtHeader
import me.nspain.cubtracking.utils.withServer
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNullOrBlank
import org.eclipse.jetty.http.HttpHeader
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

@InternalAPI
@KtorExperimentalAPI
object CubRouteAuthSpec : Spek({
    describe("Authentication") {
        val testCases = mapOf(
                "GET" to listOf("/cubs", "/cubs/1"),
                "PUT" to listOf("/cubs/1"),
                "PATCH" to listOf("/cubs/1"),
                "DELETE" to listOf("/cubs/1")

        )

        withServer {

            testCases.forEach { (method, paths) ->
                paths.forEach { path ->
                    it("should return 401 if no 'Authorization' header is provided") {
                        handleRequest(HttpMethod(method), path).apply {
                            assertTrue(requestHandled)
                            assertEquals(HttpStatusCode.Unauthorized, response.status())
                        }
                    }

                    it("should return 401 if the bearer token is not valid") {
                        handleRequest(HttpMethod(method), path) {
                            addHeader("Authorization", "dummy")
                        }.apply {
                            assertTrue(requestHandled)
                            assertEquals(HttpStatusCode.Unauthorized, response.status())
                        }
                    }
                }
            }
        }

        withServer(tokenVerifier = DummyAlwaysTrueTokenVerifier()) {

        }
    }
})

@InternalAPI
object CubRouteGetSpec : Spek({
    val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
    val algo = Algorithm.HMAC256("secret")
    val data = arrayOf(1, 2, 3, 4, 5).map {
        Cub(it.toLong(), "cub${it}")
    }
    val issuer = "test"
    withServer(repositoryData = data, algorithm = algo, tokenVerifier = DummyAlwaysTrueTokenVerifier(), issuer = issuer) {
        val token = JWT.create().withIssuer(issuer).sign(algo)!!
        describe("GET /cubs") {
            it("should return all the available cub documents and a 200 status code") {
                val expectedResponse = gson.toJson(data)
                handleRequest(HttpMethod.Get, "/cubs") {
                    addJwtHeader(token)
                }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.OK
                    response.content.shouldNotBeNullOrBlank()
                    response.content!! shouldBeEqualTo  expectedResponse
                }
            }

            it("should filter cubs by name") {
                val name = "cub2"
                val expectedResponse = gson.toJson(data.filter { it.name == name })
                val uri = "/cubs?name=$name"
                handleRequest(HttpMethod.Get, uri) {
                    addJwtHeader(token)
                }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.OK
                    response.content.shouldNotBeNullOrBlank()
                    response.content!! shouldBeEqualTo  expectedResponse
                }
            }

            it("should filter cubs by the status for a an achievement") {

            }
        }

        describe("GET /cubs/{id}") {
            it("should return cub with the ID if they exist and a 200 status code") {
                val expectedResponse = gson.toJson(data.first { it.id == 1L })
                handleRequest(HttpMethod.Get, "/cubs/1") { addJwtHeader(token) }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.OK
                    response.content.shouldNotBeNullOrBlank()
                    response.content!! shouldBeEqualTo  expectedResponse
                }
            }

            it("should return a 404 status code if the ID does not exist") {
                handleRequest(HttpMethod.Get, "/cubs/${data.maxBy { it.id }!!.id + 1}") {
                    addJwtHeader(token)
                }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.NotFound
                }

            }
        }
    }
})

@InternalAPI
object CubRoutePostSpec : Spek({
    val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
    val algo = Algorithm.HMAC256("secret")
    val data = arrayOf(1, 2, 3, 4, 5).map {
        Cub(it.toLong(), "cub${it}")
    }
    val issuer = "test"
    withServer(repositoryData = data, algorithm = algo, tokenVerifier = DummyAlwaysTrueTokenVerifier(), issuer = issuer) {
        val token = JWT.create().withIssuer(issuer).sign(algo)!!
        val cub = Cub(0, "new_cub")
        val expectedResponse = gson.toJson(Cub(((data.size + 1).toLong()), "new_cub"))
        describe("POST /cubs") {
            it("should create a new cub, return a 201 status code and return the created cub document") {
                handleRequest(HttpMethod.Post, "/cubs") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addJwtHeader(token)
                    setBody(gson.toJson(cub))
                }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.Created
                    response.content!! shouldBeEqualTo expectedResponse
                }
            }

            it("should return a 400 status code if the cub document is not full and valid") {

            }
        }
    }
})

@InternalAPI
object CubRoutePutSpec : Spek({

    withServer {
        describe("PUT /cubs/{id}") {
            it("should replace the cub document with the ID and return a 204 status code") {

            }

            it("should return a 404 status code if the ID does not exist") {

            }

            it("should return a 400 status code if the cub document is not full and valid") {

            }
        }
    }
})

@InternalAPI
object CubRoutePatchSpec : Spek({

    withServer {
        describe("PATCH /cubs/{id}") {
            it("should update the cub document with the ID and return a 204 status code if the ID exists") {

            }

            it("should return a 404 status code if the ID does not exist") {

            }

            it("should return a 400 status code if the cub document contains unknown fields") {

            }
        }
    }
})

object CubRouteDeleteSpec : Spek({

    describe("DELETE /cubs/{id}") {
        it("should return delete the cub with ID, return a 200 status code and return the deleted document if the ID exists") {

        }

        it("should return a 404 status code if the ID does not exist") {

        }
    }
})
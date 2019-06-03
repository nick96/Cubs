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
import me.nspain.cubtracking.errors.CubTrackingError
import me.nspain.cubtracking.schemas.Cub
import me.nspain.cubtracking.utils.DummyAlwaysTrueTokenVerifier
import me.nspain.cubtracking.utils.addJwtHeader
import me.nspain.cubtracking.utils.withServer
import org.amshove.kluent.*
import org.eclipse.jetty.http.HttpHeader
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@InternalAPI
@KtorExperimentalAPI
object CubRouteAuthSpec : Spek({
    describe("Authentication") {
        val testCases = mapOf(
                "GET" to listOf("/cubs", "/cubs/1"),
                "PUT" to listOf("/cubs/1"),
                "DELETE" to listOf("/cubs/1")

        )

        withServer {

            testCases.forEach { (method, paths) ->
                paths.forEach { path ->
                    it("should return 401 if no 'Authorization' header is provided") {
                        handleRequest(HttpMethod(method), path).apply {
                            requestHandled.shouldBeTrue()
                            assertTrue(requestHandled)
                            response.status() shouldBe HttpStatusCode.Unauthorized
                        }
                    }

                    it("should return 401 if the bearer token is not valid") {
                        handleRequest(HttpMethod(method), path) {
                            addHeader("Authorization", "dummy")
                        }.apply {
                            requestHandled.shouldBeTrue()
                            response.status() shouldBe HttpStatusCode.Unauthorized
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
    val issuer = ""
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
                    response.content!! shouldBeEqualTo expectedResponse
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
                    response.content!! shouldBeEqualTo expectedResponse
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
                    response.content!! shouldBeEqualTo expectedResponse
                }
            }

            it("should return a 404 status code if the ID does not exist") {
                handleRequest(HttpMethod.Get, "/cubs/${data.maxBy { it.id!! }!!.id!! + 1}") {
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
    val issuer = ""
    withServer(repositoryData = data, algorithm = algo, tokenVerifier = DummyAlwaysTrueTokenVerifier(), issuer = issuer) {
        val token = JWT.create().withIssuer(issuer).sign(algo)!!
        describe("POST /cubs") {
            it("should create a new cub, return a 201 status code and return the created cub document") {
                val cub = Cub(name ="new_cub")
                val expectedResponse = gson.toJson(Cub(((data.size + 1).toLong()), "new_cub"))
                handleRequest(HttpMethod.Post, "/cubs") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addJwtHeader(token)
                    setBody(gson.toJson(cub))
                }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.Created
                    response.content.shouldNotBeNullOrBlank()
                    response.content!! shouldBeEqualTo expectedResponse
                }
            }

            it("should return a 400 status code if the cub document is not valid") {
                val noNameCub = Cub(achievementBadges = listOf())
                val expectedResponse = gson.toJson(CubTrackingError(
                        "/cubs",
                        HttpStatusCode.BadRequest,
                        mapOf("errors" to listOf("\"name\" must not be null"))
                ))
                handleRequest(HttpMethod.Post, "/cubs") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addJwtHeader(token)
                    setBody(gson.toJson(noNameCub))
                }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.BadRequest
                    response.content.shouldNotBeNullOrBlank()
                    response.content!! shouldBeEqualTo expectedResponse
                }
            }
        }
    }
})

@InternalAPI
object CubRoutePutSpec : Spek({
    val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
    val algo = Algorithm.HMAC256("secret")
    val data = arrayOf(1, 2, 3, 4, 5).map {
        Cub(it.toLong(), "cub${it}")
    }
    val issuer = ""
    withServer(repositoryData = data, algorithm = algo, tokenVerifier = DummyAlwaysTrueTokenVerifier(), issuer = issuer) {
        val token = JWT.create().withIssuer(issuer).sign(algo)!!
        describe("PUT /cubs/{id}") {
            it("should replace the cub document with the ID and return a 200 status code") {
                val expectedResponse = gson.toJson(Cub(1, "new_name"))
                handleRequest(HttpMethod.Put, "/cubs/1") {
                    addJwtHeader(token)
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(gson.toJson(Cub(name = "new_name")))
                }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.OK
                    response.content.shouldNotBeNullOrBlank()
                    response.content!! shouldBeEqualTo expectedResponse
                }
            }

            it("should return a 404 status code if the ID does not exist") {
                val expectedResponse = gson.toJson(CubTrackingError(
                        "/cubs/${data.size + 1}",
                        HttpStatusCode.NotFound,
                        "No Cub document with ID ${data.size + 1} was not found."
                ))
                handleRequest(HttpMethod.Put, "/cubs/${data.size + 1}") {
                    addJwtHeader(token)
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(gson.toJson(Cub(name = "new_name")))
                }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.NotFound
                    response.content.shouldNotBeNullOrBlank()
                    response.content!! shouldBeEqualTo expectedResponse
                }
            }

            it("should return a 400 status code if the cub document is not valid") {
                val expectedResponse = gson.toJson(CubTrackingError(
                        "/cubs/1",
                        HttpStatusCode.BadRequest,
                        mapOf("errors" to listOf("\"id\" must be null"))
                ))
                handleRequest(HttpMethod.Put, "/cubs/1") {
                    addJwtHeader(token)
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(gson.toJson(Cub(id = 10)))
                }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.BadRequest
                    response.content.shouldNotBeNullOrBlank()
                    response.content!! shouldBeEqualTo expectedResponse
                }
            }
        }
    }
})

@InternalAPI
object CubRouteDeleteSpec : Spek({
    val algo = Algorithm.HMAC256("secret")
    val data = arrayOf(1, 2, 3, 4, 5).map {
        Cub(it.toLong(), "cub${it}")
    }
    val issuer = ""
    withServer(repositoryData = data, algorithm = algo, tokenVerifier = DummyAlwaysTrueTokenVerifier(), issuer = issuer) {
        val token = JWT.create().withIssuer(issuer).sign(algo)!!
        describe("DELETE /cubs/{id}") {
            it("should delete the cub with ID, return a 200 status code and return the deleted document if the ID exists") {
                handleRequest(HttpMethod.Delete, "/cubs/1") {
                    addJwtHeader(token)
                }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.OK
                }
            }

            it("should return a 404 status code if the ID does not exist") {
                handleRequest(HttpMethod.Delete, "/cubs/${data.size + 1}") {
                    addJwtHeader(token)
                }.apply {
                    requestHandled.shouldBeTrue()
                    response.status() shouldBe HttpStatusCode.NotFound
                }
            }
        }
    }
})
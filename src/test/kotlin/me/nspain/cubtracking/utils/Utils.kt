package me.nspain.cubtracking.utils

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.createTestEnvironment
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import me.nspain.cubtracking.cubTracking
import me.nspain.cubtracking.repository.DocumentRepository
import me.nspain.cubtracking.repository.Repository
import me.nspain.cubtracking.schemas.Cub
import me.nspain.cubtracking.security.TokenVerifier
import me.nspain.cubtracking.security.User

@UseExperimental(KtorExperimentalAPI::class)
@InternalAPI
fun withServer(repositoryData: List<Cub> = listOf(),
               algorithm: Algorithm = Algorithm.HMAC256("secret"),
               tokenVerifier: TokenVerifier = DummyAlwaysFalseTokenVerifier(),
               issuer: String = "test",
               block: TestApplicationEngine.() -> Unit) {
    val engine = TestApplicationEngine(createTestEnvironment())
    val repo = DummyRepository(repositoryData)

    engine.start(false)
    engine.application.cubTracking(repo, algorithm, tokenVerifier, issuer)
    with(engine, block)
}

fun TestApplicationRequest.addJwtHeader(token: String) = addHeader(HttpHeaders.Authorization, "Bearer $token")

class DummyAlwaysFalseTokenVerifier : TokenVerifier {
    override fun verify(token: String): User? {
        return null
    }

}

class DummyAlwaysTrueTokenVerifier : TokenVerifier {
    override fun verify(token: String): User? {
        return User(1, "dummy")
    }
}

class DummyRepository(cubData: List<Cub>) : Repository {
    override val cubRepository = DummyCubRepository(cubData)
    override val connectionString = "test.repository"
}

class DummyCubRepository(private var data: List<Cub>) : DocumentRepository<Cub> {
    /** Get all available Cub documents. */
    override fun get(): List<Cub> {
        return data
    }

    /** Get the Cub document at [id]. */
    override fun get(id: Long): Cub? {
        return data.firstOrNull { it.id == id }
    }

    /** Get all Cub documents that fulfill [predicate]. */
    override fun filter(predicate: (Cub) -> Boolean): List<Cub> {
        return data.filter(predicate)
    }

    /** Update the Cub document at [id] with [update]. */
    override fun update(id: Long, update: Cub.() -> Unit): Cub? {
        val cub = data.firstOrNull { it.id == id } ?: return null

        val cubUpdate = Cub().apply(update)
        cub.name = cubUpdate.name ?: cub.name
        cub.achievementBadges = cubUpdate.achievementBadges ?: cub.achievementBadges
        cub.specialInterestBadges = cubUpdate.specialInterestBadges ?: cub.specialInterestBadges
        cub.bronzeBoomerang = cubUpdate.bronzeBoomerang ?: cub.bronzeBoomerang
        cub.silverBoomerang = cubUpdate.silverBoomerang ?: cub.silverBoomerang
        cub.goldBoomerang = cubUpdate.goldBoomerang ?: cub.goldBoomerang

        return cub
    }

    /** Replace the Cub document at [id] with [replacement]. */
    override fun replace(id: Long, replacement: Cub): Cub? {
        val cub = data.firstOrNull { it.id == id } ?: return null
        cub.name = replacement.name
        cub.achievementBadges = replacement.achievementBadges
        cub.specialInterestBadges = replacement.specialInterestBadges
        cub.bronzeBoomerang = replacement.bronzeBoomerang
        cub.silverBoomerang = replacement.silverBoomerang
        cub.goldBoomerang = replacement.goldBoomerang
        data = data.map { return if (it.id == id) cub else it }
        return cub
    }

    /** Delete Cub document with id [id]. */
    override fun delete(id: Long): Cub? {
        val cub = data.firstOrNull { it.id == id } ?: return null
        data = data.filter { it.id != id }
        return cub
    }

    /** Delete all the Cub documents that fulfill [predicate]. */
    override fun delete(predicate: (Cub) -> Boolean): List<Cub>? {
        val cubs = data.filter(predicate)
        data = data.filter { !predicate(it) }
        return cubs
    }

    /** Append [elem] to the collection of Cub documents. */
    override fun append(elem: Cub): Cub {
        val nextId = data.size + 1
        val cub = Cub(
                nextId.toLong(),
                elem.name,
                elem.achievementBadges,
                elem.specialInterestBadges,
                elem.bronzeBoomerang,
                elem.silverBoomerang,
                elem.goldBoomerang
        )
        data += cub
        return cub
    }

    override fun plus(elem: Cub): DummyCubRepository {
        data += elem
        return this
    }

}




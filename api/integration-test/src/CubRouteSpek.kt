package me.nspain.cubtracking

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object CubAuthSpek: Spek({
    describe("Authentication") {
        it ("accepts a valid google JWT and returns its own JWT") {

        }

        it("rejects an invalid google JWT and responds with 401") {

        }
    }
})

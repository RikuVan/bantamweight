package fi.monad

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveBody
import org.http4k.kotest.shouldHaveStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SkinnyStackTemplateTest {

    @Test
    fun `Ping test`() {
        assertEquals(app(Request(GET, "/ping")), Response(OK).body("pong"))
    }
    @Test
    fun `Check Kotest matcher for http4k work as expected`() {
        val request = Request(GET, "/testing/kotest?a=b").body("http4k is cool").header("my header", "a value")
    
        val response = app(request)
    
        // response assertions
        response shouldHaveStatus OK
        response shouldHaveBody "Echo 'http4k is cool'"
    }

}

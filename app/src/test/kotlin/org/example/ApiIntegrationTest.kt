package org.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.call.body
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApiIntegrationTest {
    @Test
    fun validateEndpoint() = testApplication {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
        }
        val response = client.post("/validate") {
            contentType(ContentType.Application.Json)
            setBody(PasswordRequest("AbTp9!fok"))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<PasswordResponse>()
        assertTrue(body.valid)
    }
}

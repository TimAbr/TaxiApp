package org.example.project

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        environment {
            config = MapApplicationConfig(
                Config.DB_DRIVER to "org.h2.Driver",
                Config.DB_URL to "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                Config.JWT_SECRET to "test-secret",
                Config.JWT_REALM to "test-realm",
                Config.GOOGLE_CLIENT_ID to "test-google-id"
            )
        }
        application {
            module()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Ktor: ${Greeting().greet()}", response.bodyAsText())
    }
}
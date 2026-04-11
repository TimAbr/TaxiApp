package org.example.project

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import org.example.project.utils.config.ConfigKeys
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        environment {
            config = MapApplicationConfig(
                ConfigKeys.DB_DRIVER to "org.h2.Driver",
                ConfigKeys.DB_URL to "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                ConfigKeys.JWT_SECRET to "test-secret",
                ConfigKeys.JWT_REALM to "test-realm",
                ConfigKeys.GOOGLE_CLIENT_ID to "test-google-id",
                ConfigKeys.JWT_ACCESS_TOKEN_EXPIRATION_HOURS to "1",
                ConfigKeys.JWT_REFRESH_TOKEN_EXPIRATION_DAYS to "30",
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

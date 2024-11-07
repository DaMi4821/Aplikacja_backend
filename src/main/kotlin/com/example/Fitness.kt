@file:JvmName("Fitness")

import com.example.repository.UserRepository
import com.example.repository.initDatabase
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080) {
        initDatabase()

        install(CORS) {
            allowHost("localhost:3000") // Zezwala na połączenia z frontendu działającego na porcie 3000
            allowMethod(HttpMethod.Options) // Zezwala na zapytania wstępne OPTIONS
            allowMethod(HttpMethod.Post) // Zezwala na metodę POST
            allowHeader(HttpHeaders.ContentType) // Zezwala na nagłówek Content-Type
        }

        install(ContentNegotiation) {
            gson { setPrettyPrinting() }
        }

        install(CallLogging) { level = Level.INFO }

        install(StatusPages) {
            status(HttpStatusCode.BadRequest) { call, status ->
                call.respondText("Błędne dane", status = status)
            }
            status(HttpStatusCode.InternalServerError) { call, status ->
                call.respondText("Błąd serwera", status = status)
            }
        }

        routing {
            // Endpoint rejestracyjny
            post("/api/register") {
                try {
                    val user = call.receive<UserRequest>()
                    if (UserRepository.isEmailOrUsernameTaken(user.email, user.username)) {
                        call.respond(HttpStatusCode.BadRequest, "Podany email lub nazwa użytkownika są już zajęte.")
                    } else {
                        UserRepository.addUser(user.email, user.username, user.password)
                        call.respond(HttpStatusCode.Created, "Użytkownik został pomyślnie utworzony! Możesz się teraz zalogować.")
                    }
                } catch (e: Exception) {
                    call.application.log.error("Błąd rejestracji użytkownika: ${e.message}")
                    call.respond(HttpStatusCode.InternalServerError, "Błąd podczas przetwarzania rejestracji.")
                }
            }

            // Endpoint testowy
            get("/") {
                call.respondText("Welcome to the Ktor application!")
            }
        }
    }.start(wait = true)
}

// Klasa danych do odbierania danych rejestracyjnych
data class UserRequest(val email: String, val username: String, val password: String)

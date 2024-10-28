@file:JvmName("Fitness")

import com.example.repository.*
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.serialization.gson.*
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.response.*
import org.slf4j.event.Level

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import com.typesafe.config.ConfigFactory
import io.ktor.http.*

fun main() {

    embeddedServer(Netty, 8080) {
        initDatabase()
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        routing() {
            get("/cars") {
                log.info("Próbuję pobrać listę samochodów z bazy danych...")
                val car = getAllCars()
                log.info("Pobrano samochody: $car")
                call.respond(car)
            }

            post("/cars") {
                val carRequest = call.receive<Map<String, String>>()
                val name = carRequest["name"]
                if (name != null) {
                    log.info("Dodawanie nowego samochodu: $name")
                    val newCar = addCar(name)
                    call.respond(newCar)
                } else {
                    call.respondText("Brak nazwy samochodu", status = HttpStatusCode.BadRequest)
                }
            }

            delete("/cars/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    log.info("Usuwanie samochodu o ID: $id")
                    val success = deleteCar(id)
                    if (success) {
                        call.respondText("Samochód usunięty", status = HttpStatusCode.OK)
                    } else {
                        call.respondText("Samochód o podanym ID nie istnieje", status = HttpStatusCode.NotFound)
                    }
                } else {
                    call.respondText("Nieprawidłowe ID", status = HttpStatusCode.BadRequest)
                }
            }

            route("/") {
                get {
                    call.respondText("Welcome to the Ktor application!")
                }
                get("/1") {
                    call.respondText("Abcde")
                }
            }

        }
    }.start(wait = true)
}








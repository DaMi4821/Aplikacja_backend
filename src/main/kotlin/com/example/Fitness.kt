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








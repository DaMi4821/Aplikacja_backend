package com.example.repository

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory


// Reprezentacja tabeli `car`
object Cars : IntIdTable("car") {  // Tutaj zmieniamy nazwę tabeli na "car"
    val name = varchar("name", 50)
}


// Reprezentacja wiersza z tabeli `car`
data class Car(val id: Int, val name: String)

// Funkcja do mapowania wiersza z bazy danych do obiektu `Car`
fun ResultRow.toCar() = Car(
    id = this[Cars.id].value,
    name = this[Cars.name]
)

// Funkcja do pobierania wszystkich samochodów z tabeli `car`

// Zainicjuj logger
val log = LoggerFactory.getLogger("CarRepository")

fun getAllCars(): List<Car> {
    return try {
        transaction {
            Cars.selectAll().map { it.toCar() }
        }
    } catch (e: Exception) {
        log.error("Błąd podczas pobierania samochodów: ${e.message}")
        emptyList()
    }
}




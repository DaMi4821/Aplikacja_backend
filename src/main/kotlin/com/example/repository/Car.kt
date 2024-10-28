package com.example.repository

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
        log.info("Rozpoczynam pobieranie samochodów...")
        val cars = transaction {
            Cars.selectAll().map { it.toCar() }
        }
        log.info("Pomyślnie pobrano samochody: $cars")
        cars
    } catch (e: Exception) {
        log.error("Błąd podczas pobierania samochodów: ${e.message}")
        emptyList()
    }
}

fun addCar(name: String): Car {
    // Dodaj nowy rekord do tabeli Cars i uzyskaj wygenerowane ID
    val id = transaction {
        Cars.insertAndGetId {
            it[Cars.name] = name
        }
    }
    // Zwróć obiekt Car z automatycznie wygenerowanym ID
    return Car(id.value, name)
}

fun deleteCar(id: Int): Boolean {
    // Usuń rekord z tabeli Cars, gdzie ID równa się przekazanemu id
    val rowsDeleted = transaction {
        Cars.deleteWhere { Cars.id eq id }
    }
    // Zwróć true, jeśli usunięto przynajmniej jeden rekord, false w przeciwnym razie
    return rowsDeleted > 0
}



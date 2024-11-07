package com.example.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Table


// Definicja tabeli "users" przy użyciu Exposed
object UserTable : Table("users") {
    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)
}

// Obiekt UserRepository zawierający metody do obsługi bazy danych
object UserRepository {

    // Funkcja sprawdzająca, czy istnieje użytkownik o danym emailu lub nazwie użytkownika
    fun isEmailOrUsernameTaken(email: String, username: String): Boolean {
        return transaction {
            UserTable.select { (UserTable.email eq email) or (UserTable.username eq username) }
                .count() > 0 // Zwraca true, jeśli istnieje użytkownik o podanym emailu lub username
        }
    }

    // Funkcja dodająca nowego użytkownika do bazy danych
    fun addUser(email: String, username: String, password: String) {
        transaction {
            UserTable.insert {
                it[UserTable.email] = email
                it[UserTable.username] = username
                it[UserTable.password] = password
            }
        }
    }
}

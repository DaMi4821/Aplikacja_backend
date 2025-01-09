package com.example.repository

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import com.example.repository.UploadedFileKoszyk
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.initDatabase() {
    try {
        val config = ConfigFactory.load().getConfig("ktor.datasource")

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.getString("jdbcUrl")
            driverClassName = config.getString("driverClassName")
            username = config.getString("username")
            password = config.getString("password")
            maximumPoolSize = config.getInt("maximumPoolSize")
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(Koszyk) // Add your table here
        }

        log.info("Połączono z bazą danych PostgreSQL")
    } catch (e: Exception) {
        log.error("Błąd podczas łączenia z bazą danych: ${e.message}")
    }
}
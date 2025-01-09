package com.example.repository

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction

object DaneKlienta : LongIdTable("billing_data") {
    val order_id = long("order_id").autoIncrement() // Order ID
    val order_date = datetime("order_date").defaultExpression(CurrentTimestamp()) // Order Date
    val name = varchar("name", 255) // Name
    val email = varchar("email", 255) // Email
    val address = varchar("address", 255) // Address
    val city = varchar("city", 255) // City
    val zip = varchar("zip", 200) // Zip Code
    val country = varchar("country", 100) // Country
    val orderDetails = text("order_details").nullable()
}
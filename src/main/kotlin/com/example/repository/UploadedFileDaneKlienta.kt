package com.example.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UploadedFileDaneKlienta (id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UploadedFileDaneKlienta>(DaneKlienta)
    var order_id by DaneKlienta.order_id
    var order_date by DaneKlienta.order_date
    var name by DaneKlienta.name
    var email by DaneKlienta.email
    var address by DaneKlienta.address
    var city by DaneKlienta.city
    var zip by DaneKlienta.zip
    var country by DaneKlienta.country
    var orderDetails by DaneKlienta.orderDetails
}
package com.example.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UploadedFileFood(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UploadedFileFood>(UploadedFilesFoodTable)

    var fileName by UploadedFilesFoodTable.fileName
    var filePath by UploadedFilesFoodTable.filePath
    var filePrice by UploadedFilesFoodTable.filePrice
    var categoryId by UploadedFilesFoodTable.categoryId
}


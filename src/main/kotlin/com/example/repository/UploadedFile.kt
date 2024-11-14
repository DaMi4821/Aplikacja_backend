package com.example.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UploadedFile(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UploadedFile>(UploadedFilesTable)

    var fileName by UploadedFilesTable.fileName
    var filePath by UploadedFilesTable.filePath
    var filePrice by UploadedFilesTable.filePrice
}


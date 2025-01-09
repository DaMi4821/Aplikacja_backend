package com.example.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

public class UploadedFileKoszyk(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UploadedFileKoszyk>(Koszyk)

    var fileName by Koszyk.fileName
    var filePath by Koszyk.filePath
    var filePrice by Koszyk.filePrice
    var fileValue by Koszyk.fileValue
}

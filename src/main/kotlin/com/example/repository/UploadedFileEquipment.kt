package com.example.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UploadedFileEquipment(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UploadedFileEquipment>(UploadedFilesEquipmentTable)

    var fileName by UploadedFilesEquipmentTable.fileName
    var filePath by UploadedFilesEquipmentTable.filePath
    var filePrice by UploadedFilesEquipmentTable.filePrice
    var categoryId by UploadedFilesEquipmentTable.categoryId
}




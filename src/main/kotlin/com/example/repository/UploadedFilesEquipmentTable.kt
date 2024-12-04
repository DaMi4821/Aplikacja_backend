package com.example.repository

import com.example.repository.UploadedFilesEquipmentTable.integer
import com.example.repository.UploadedFilesEquipmentTable.varchar
import org.jetbrains.exposed.dao.id.LongIdTable

object UploadedFilesEquipmentTable   : LongIdTable("uploaded_files_equipment"){
    val fileName = varchar("file_name", 255)
    val filePath = varchar("file_path", 255)
    val filePrice = varchar("file_price", 255)
    val categoryId = integer("category_id")
}
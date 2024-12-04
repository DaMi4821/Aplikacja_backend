package com.example.repository

import com.example.repository.UploadedFilesFoodTable.integer
import com.example.repository.UploadedFilesFoodTable.varchar
import org.jetbrains.exposed.dao.id.LongIdTable

object UploadedFilesFoodTable  : LongIdTable("uploaded_files_food"){
    val fileName = varchar("file_name", 255)
    val filePath = varchar("file_path", 255)
    val filePrice = varchar("file_price", 255)
    val categoryId = integer("category_id")
}
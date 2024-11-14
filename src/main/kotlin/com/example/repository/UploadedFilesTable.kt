package com.example.repository

import org.jetbrains.exposed.dao.id.LongIdTable


object UploadedFilesTable : LongIdTable("uploaded_files") {
    val fileName = varchar("file_name", 255)
    val filePath = varchar("file_path", 255)
}




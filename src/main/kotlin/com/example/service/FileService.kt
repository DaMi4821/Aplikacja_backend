package com.example.service

import org.jetbrains.exposed.sql.transactions.transaction
import com.example.repository.UploadedFile

object FileService {
    fun saveFile(fileName: String, filePath: String) {
        try {
            transaction {
                UploadedFile.new {
                    this.fileName = fileName
                    this.filePath = filePath
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Błąd podczas zapisu do bazy danych: ${e.message}")
        }
    }
}

package com.example.routing

import com.example.repository.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import com.example.service.FileService
import java.io.File
import org.jetbrains.exposed.sql.transactions.transaction


fun Route.addFileRoute() {
    post("/api/add-file") {
        try {
            val requestData = call.receive<Map<String, String>>()
            call.application.log.info("Odebrane dane: $requestData")

            val fileName = requestData["fileName"] ?: run {
                call.application.log.error("Brak nazwy pliku")
                return@post call.respond(HttpStatusCode.BadRequest, "Brak nazwy pliku")
            }
            val filePath = requestData["filePath"] ?: run {
                call.application.log.error("Brak ścieżki pliku")
                return@post call.respond(HttpStatusCode.BadRequest, "Brak ścieżki pliku")
            }

            call.application.log.info("Dodawanie pliku: fileName=$fileName, filePath=$filePath")

            // Próbuj zapisać dane do bazy danych
            FileService.saveFile(fileName, filePath)

            call.respond(HttpStatusCode.Created, "Plik został dodany do bazy danych.")
        } catch (e: Exception) {
            call.application.log.error("Błąd podczas przetwarzania danych: ${e.message}")
            call.respond(HttpStatusCode.BadRequest, "Błędne dane: ${e.message}")
        }
    }

}



fun Route.getFileRoute() {
    get("/api/files/{fileName}") {
        val fileName = call.parameters["fileName"]
        if (fileName == null) {
            call.respond(HttpStatusCode.BadRequest, "Brak nazwy pliku")
            return@get
        }

        // Pobierz ścieżkę pliku z bazy danych na podstawie nazwy pliku
        val filePath = transaction {
            UploadedFile.find { UploadedFilesTable.fileName eq fileName }
                .firstOrNull()?.filePath
        }

        if (filePath == null) {
            call.respond(HttpStatusCode.NotFound, "Plik nie został znaleziony w bazie danych")
            return@get
        }

        val file = File(filePath)

        if (file.exists()) {
            call.respondFile(file)
        } else {
            call.respond(HttpStatusCode.NotFound, "Plik nie istnieje na serwerze")
        }
    }
}

fun Route.viewFileRoute() {
    get("/api/view-file/{id}") {
        val fileId = call.parameters["id"]?.toLongOrNull()
        if (fileId == null) {
            call.application.log.error("Nieprawidłowe ID pliku: ${call.parameters["id"]}")
            call.respond(HttpStatusCode.BadRequest, "Nieprawidłowe ID pliku")
            return@get
        }

        call.application.log.info("Otrzymano ID pliku: $fileId")

        // Pobierz ścieżkę pliku z bazy danych na podstawie ID
        val filePath = transaction {
            UploadedFile.findById(fileId)?.filePath
        }

        if (filePath == null) {
            call.application.log.error("Plik o ID $fileId nie został znaleziony w bazie danych")
            call.respond(HttpStatusCode.NotFound, "Plik nie został znaleziony w bazie danych")
            return@get
        }

        call.application.log.info("Ścieżka pliku: $filePath")

        val file = File(filePath)

        if (file.exists()) {
            call.application.log.info("Plik o ID $fileId znaleziony na serwerze")
            call.respondFile(file)
        } else {
            call.application.log.error("Plik o ścieżce $filePath nie istnieje na serwerze")
            call.respond(HttpStatusCode.NotFound, "Plik nie istnieje na serwerze")
        }
    }


}

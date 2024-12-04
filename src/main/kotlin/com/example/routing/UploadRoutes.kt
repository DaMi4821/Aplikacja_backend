package com.example.routing

import com.example.repository.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
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
            val filePrice = requestData["filePrice"] ?: run {
                call.application.log.error("Brak ceny pliku")
                return@post call.respond(HttpStatusCode.BadRequest, "Brak ceny pliku")
            }
            val categoryId = requestData["categoryId"]?.toIntOrNull() ?: run {
                call.application.log.error("Brak ID kategorii")
                return@post call.respond(HttpStatusCode.BadRequest, "Brak ID kategorii")
            }

            call.application.log.info("Dodawanie pliku: fileName=$fileName, filePath=$filePath, filePrice=$filePrice, categoryId=$categoryId")

            // Zapis do bazy danych
            FileService.saveFile(fileName, filePath, filePrice, categoryId)

            call.respond(HttpStatusCode.Created, "Plik został dodany do bazy danych.")
        } catch (e: Exception) {
            call.application.log.error("Błąd podczas przetwarzania danych: ${e.message}")
            call.respond(HttpStatusCode.BadRequest, "Błędne dane: ${e.message}")
        }
    }
}

fun Route.listFilesRoute() {
    get("/api/files/{categoryId?}") {
        val categoryId = call.parameters["categoryId"]?.toIntOrNull()

        call.application.log.info("Otrzymano żądanie do /api/files z kategorią: $categoryId")

        val files = transaction {
            if (categoryId != null) {
                UploadedFile.find { UploadedFilesTable.categoryId eq categoryId }
                    .map { file ->
                        mapOf(
                            "id" to file.id.value,
                            "name" to file.fileName,
                            "path" to file.filePath,
                            "price" to file.filePrice // Pole filePrice
                        )
                    }
            } else {
                UploadedFile.all().map { file ->
                    mapOf(
                        "id" to file.id.value,
                        "name" to file.fileName,
                        "path" to file.filePath,
                        "price" to file.filePrice // Pole filePrice
                    )
                }
            }
        }

        call.application.log.info("Zwracanie plików: ${files.size} elementów")
        call.respond(files)
    }
}

fun Route.listFilesRouteZywnosc() {
    get("/api/files/food/{categoryId?}") {
        val categoryId = call.parameters["categoryId"]?.toIntOrNull()

        call.application.log.info("Otrzymano żądanie do /api/files z kategorią: $categoryId")

        val files = transaction {
            if (categoryId != null) {
                UploadedFileFood.find { UploadedFilesFoodTable.categoryId eq categoryId }
                    .map { file ->
                        mapOf(
                            "id" to file.id.value,
                            "name" to file.fileName,
                            "path" to file.filePath,
                            "price" to file.filePrice // Pole filePrice
                        )
                    }
            } else {
                UploadedFileFood.all().map { file ->
                    mapOf(
                        "id" to file.id.value,
                        "name" to file.fileName,
                        "path" to file.filePath,
                        "price" to file.filePrice // Pole filePrice
                    )
                }
            }
        }

        call.application.log.info("Zwracanie plików: ${files.size} elementów")
        call.respond(files)
    }
}


fun Route.viewFileRoute() {
    get("/api/view-file/supplements/{id}") {
        val fileId = call.parameters["id"]?.toLongOrNull()
        if (fileId == null) {
            call.application.log.error("Nieprawidłowe ID pliku: ${call.parameters["id"]}")
            call.respond(HttpStatusCode.BadRequest, "Nieprawidłowe ID pliku")
            return@get
        }

        call.application.log.info("Otrzymano ID pliku: $fileId")

        val filePath = transaction {
            UploadedFile.findById(fileId)?.filePath
        }

        if (filePath == null) {
            call.application.log.error("Plik o ID $fileId nie został znaleziony w bazie danych")
            call.respond(HttpStatusCode.NotFound, "Plik nie został znaleziony w bazie danych")
            return@get
        }

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

fun Route.viewFileRouteEquipment() {
    get("/api/view-file/equipment/{id}") {
        val fileId = call.parameters["id"]?.toLongOrNull()
        if (fileId == null) {
            call.application.log.error("Nieprawidłowe ID pliku: ${call.parameters["id"]}")
            call.respond(HttpStatusCode.BadRequest, "Nieprawidłowe ID pliku")
            return@get
        }

        call.application.log.info("Otrzymano ID pliku: $fileId")

        val filePath = transaction {
            UploadedFileEquipment.findById(fileId)?.filePath
        }

        if (filePath == null) {
            call.application.log.error("Plik o ID $fileId nie został znaleziony w bazie danych")
            call.respond(HttpStatusCode.NotFound, "Plik nie został znaleziony w bazie danych")
            return@get
        }

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

fun Route.listFilesRouteEquipment() {
    get("/api/files/equipment/{categoryId?}") {
        val categoryId = call.parameters["categoryId"]?.toIntOrNull()

        call.application.log.info("Otrzymano żądanie do /api/files z kategorią: $categoryId")

        val files = transaction {
            if (categoryId != null) {
                UploadedFileEquipment.find { UploadedFilesEquipmentTable.categoryId eq categoryId }
                    .map { file ->
                        mapOf(
                            "id" to file.id.value,
                            "name" to file.fileName,
                            "path" to file.filePath,
                            "price" to file.filePrice // Pole filePrice
                        )
                    }
            } else {
                UploadedFileEquipment.all().map { file ->
                    mapOf(
                        "id" to file.id.value,
                        "name" to file.fileName,
                        "path" to file.filePath,
                        "price" to file.filePrice // Pole filePrice
                    )
                }
            }
        }

        call.application.log.info("Zwracanie plików: ${files.size} elementów")
        call.respond(files)
    }
}

fun Route.viewFileRouteZywnosc() {
    get("/api/view-file/food/{id}") {
        val fileId = call.parameters["id"]?.toLongOrNull()
        if (fileId == null) {
            call.application.log.error("Nieprawidłowe ID pliku: ${call.parameters["id"]}")
            call.respond(HttpStatusCode.BadRequest, "Nieprawidłowe ID pliku")
            return@get
        }

        call.application.log.info("Otrzymano ID pliku: $fileId")

        val filePath = transaction {
            UploadedFileFood.findById(fileId)?.filePath
        }

        if (filePath == null) {
            call.application.log.error("Plik o ID $fileId nie został znaleziony w bazie danych")
            call.respond(HttpStatusCode.NotFound, "Plik nie został znaleziony w bazie danych")
            return@get
        }

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






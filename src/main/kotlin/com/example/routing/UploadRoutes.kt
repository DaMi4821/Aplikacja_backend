package com.example.routing

import com.example.repository.*
import com.example.service.FileService

import io.jsonwebtoken.Jwts
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import io.jsonwebtoken.SignatureAlgorithm


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


fun Route.postKoszyk() {
    post("/api/addToCart") {
        try {
            // Parse incoming JSON body
            val requestData = call.receive<Map<String, Any>>()
            val productName = requestData["name"] as? String ?: throw IllegalArgumentException("Nieprawidłowa nazwa produktu")
            val productPrice = requestData["price"] as? String ?: throw IllegalArgumentException("Nieprawidłowa cena produktu")
            val productPath = requestData["path"] as? String ?: throw IllegalArgumentException("Nieprawidłowa ścieżka produktu")

            transaction {
                // Sprawdź, czy produkt już istnieje w tabeli
                val existingProduct = Koszyk.select {
                    Koszyk.fileName eq productName and (Koszyk.filePath eq productPath)
                }.singleOrNull()

                if (existingProduct != null) {
                    // Jeśli produkt istnieje, zwiększ jego `file_value`
                    Koszyk.update({ Koszyk.fileName eq productName and (Koszyk.filePath eq productPath) }) {
                        it[fileValue] = existingProduct[Koszyk.fileValue] + 1
                    }
                } else {
                    // Jeśli produkt nie istnieje, dodaj nowy rekord
                    Koszyk.insert {
                        it[this.filePath] = productPath
                        it[this.fileName] = productName
                        it[this.filePrice] = productPrice
                    }
                }
            }

            call.respond(HttpStatusCode.Created, "Produkt został dodany do koszyka.")
        } catch (e: Exception) {
            call.application.log.error("Błąd podczas dodawania do koszyka: ${e.message}")
            call.respond(HttpStatusCode.BadRequest, "Błąd podczas dodawania do koszyka: ${e.message}")
        }
    }
}


fun Route.viewFileRouteKoszyk() {
    get("/api/view-file/koszyk") {
        call.application.log.info("Otrzymano żądanie pobrania danych koszyka")

        try {
            // Pobranie wszystkich danych z tabeli Koszyk
            val koszykItems = transaction {
                UploadedFileKoszyk.all()
                    .orderBy(Koszyk.id to SortOrder.ASC) // Sortowanie według kolumny 'id' rosnąco
                    .map { file ->
                        mapOf(
                            "id" to file.id.value,
                            "name" to file.fileName,
                            "path" to file.filePath,
                            "price" to file.filePrice,
                            "quantity" to file.fileValue
                        )
                    }
            }


            if (koszykItems.isEmpty()) {
                call.application.log.error("Koszyk jest pusty")
                call.respond(HttpStatusCode.NotFound, "Koszyk jest pusty")
                return@get
            }

            // Odpowiedz listą produktów z koszyka
            call.application.log.info("Znaleziono produkty w koszyku: ${koszykItems.size}")
            call.respond(koszykItems)
        } catch (e: Exception) {
            call.application.log.error("Wystąpił błąd podczas przetwarzania koszyka", e)
            call.respond(HttpStatusCode.InternalServerError, "Wystąpił błąd na serwerze")
        }
    }
}

fun Route.deleteItemFromCartRoute() {
    options("/api/remove-item/{id}") {
        call.respond(HttpStatusCode.OK) // Zezwól na zapytania preflight
    }

    delete("/api/remove-item/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Nieprawidłowe ID produktu")
            return@delete
        }

        transaction {
            UploadedFileKoszyk.findById(id)?.delete()
        }
        call.respond(HttpStatusCode.OK, "Produkt usunięty z koszyka")
    }
}

fun Route.updateItemQuantityKoszyk() {
    post("/api/update-item-quantity") {
        try {
            // Odczytanie danych z żądania
            val requestData = call.receive<Map<String, Int>>()
            val itemId = requestData["id"] ?: throw IllegalArgumentException("Brak ID przedmiotu")
            val change = requestData["change"] ?: throw IllegalArgumentException("Brak zmiany ilości")

            // Aktualizacja ilości w bazie danych
            transaction {
                val item = UploadedFileKoszyk.findById(itemId.toLong())
                if (item != null) {
                    item.fileValue = Math.max(1, item.fileValue + change) // Nie pozwalamy na ilość mniejszą niż 1
                } else {
                    throw IllegalArgumentException("Przedmiot o ID $itemId nie istnieje")
                }
            }

            call.respond(HttpStatusCode.OK, "Ilość przedmiotu zaktualizowana")
        } catch (e: Exception) {
            call.application.log.error("Błąd podczas aktualizacji ilości przedmiotu: ${e.message}")
            call.respond(HttpStatusCode.BadRequest, "Błąd podczas aktualizacji ilości przedmiotu: ${e.message}")
        }
    }
}


fun Route.saveBillingDataRoute() {
    post("/api/save-billing-data") {
        try {
            val billingData = call.receive<Map<String, String>>() // Odbierz dane JSON
            transaction {
                DaneKlienta.insert {
                    it[name] = billingData["name"] ?: throw IllegalArgumentException("Brak pola name")
                    it[email] = billingData["email"] ?: throw IllegalArgumentException("Brak pola email")
                    it[address] = billingData["address"] ?: throw IllegalArgumentException("Brak pola address")
                    it[city] = billingData["city"] ?: throw IllegalArgumentException("Brak pola city")
                    it[zip] = billingData["zip"] ?: throw IllegalArgumentException("Brak pola zip")
                    it[country] = billingData["country"] ?: throw IllegalArgumentException("Brak pola country")
                }
            }
            call.respond(mapOf("status" to "success"))
        } catch (e: Exception) {
            call.respond(mapOf("status" to "error", "message" to e.message))
        }
    }
}

fun Route.aggregateAndTruncateRoute() {
    post("/api/aggregate-and-truncate") {
        try {
            transaction {
                // Execute the aggregation and insert the data into billing_data
                exec("""
                    UPDATE billing_data
                    SET order_details = (
                        SELECT json_agg(koszyk) -- Aggregate data from Koszyk as JSON
                        FROM (
                            SELECT file_name, file_path, file_price, file_value 
                            FROM Koszyk
                        ) AS koszyk
                    )
                    WHERE order_id = (
                        SELECT order_id
                        FROM billing_data
                        WHERE order_date IS NOT NULL -- Find the row where data already exists
                        LIMIT 1
                    );

                """.trimIndent())

                // Truncate the Koszyk table after aggregation
                exec("TRUNCATE TABLE Koszyk;")
            }

            call.respond(mapOf("status" to "success"))
        } catch (e: Exception) {
            call.application.log.error("Error during aggregation and truncation", e)
            call.respond(mapOf("status" to "error", "message" to e.message))
        }
    }
}



fun sendEmail(toEmail: String, subject: String, body: String) {
    val username = "dami482001@wp.pl" // Adres e-mail na WP
    val password = "matematyka6" // Hasło do konta e-mail na WP

    val props = Properties()
    props["mail.smtp.auth"] = "true"
    props["mail.smtp.starttls.enable"] = "true"
    props["mail.smtp.host"] = "smtp.wp.pl" // Serwer SMTP WP
    props["mail.smtp.port"] = "587" // Port dla TLS

    val session = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(username, password)
        }
    })

    try {
        val message = MimeMessage(session)
        message.setFrom(InternetAddress(username))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
        message.subject = subject
        message.setText(body)

        Transport.send(message)
        println("E-mail został wysłany pomyślnie.")
    } catch (e: MessagingException) {
        e.printStackTrace()
        throw RuntimeException("Błąd podczas wysyłania e-maila: ${e.message}")
    }
}

fun Route.sendBillingDataEmailRoute() {
    post("/api/send-billing-data-email") {
        try {
            // Pobierz adres e-mail z treści żądania
            val requestData = call.receive<Map<String, String>>()
            val toEmail = requestData["toEmail"] ?: throw IllegalArgumentException("Nie podano adresu e-mail")

            // Pobierz wszystkie dane z tabeli billing_data
            val billingDataList = transaction {
                exec("SELECT * FROM billing_data") { resultSet ->
                    val result = mutableListOf<String>()
                    while (resultSet.next()) {
                        val orderId = resultSet.getInt("order_id")
                        val name = resultSet.getString("name")
                        val email = resultSet.getString("email")
                        val address = resultSet.getString("address")
                        val city = resultSet.getString("city")
                        val zip = resultSet.getString("zip")
                        val country = resultSet.getString("country")
                        val orderDate = resultSet.getTimestamp("order_date")
                        val orderDetails = resultSet.getString("order_details")

                        result.add(
                            """
                Order ID: $orderId
                Imię i nazwisko: $name
                E-mail: $email
                Adres: $address, $city, $zip, $country
                Data zamówienia: $orderDate
                Szczegóły zamówienia: $orderDetails
                ----------------------------
                """.trimIndent()
                        )
                    }
                    result
                } ?: emptyList()
            }


            // Połącz wszystkie dane w jeden ciąg znaków
            val emailBody = if (billingDataList.isNotEmpty()) {
                billingDataList.joinToString("\n\n")
            } else {
                "Brak danych w tabeli billing_data."
            }

            // Wyślij e-mail
            sendEmail(toEmail, "Wszystkie Szczegóły Zamówień", emailBody)


            transaction {
                exec("TRUNCATE TABLE billing_data")
            }
            // Odpowiedz na żądanie
            call.respond(mapOf("status" to "success", "message" to "E-mail wysłany pomyślnie"))
        } catch (e: Exception) {
            call.application.log.error("Błąd podczas wysyłania e-maila", e)
            call.respond(mapOf("status" to "error", "message" to e.message))
        }
    }
}

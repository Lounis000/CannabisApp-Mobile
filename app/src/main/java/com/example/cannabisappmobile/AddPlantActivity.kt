package com.example.cannabisappmobile

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStream
import java.util.*
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.CellType
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.WriterException
import java.io.FileOutputStream
import java.io.IOException

class AddPlantActivity : AppCompatActivity() {

    private lateinit var etIdentification: EditText
    private lateinit var spinnerHealthStatus: Spinner
    private lateinit var etOrigin: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerStage: Spinner
    private lateinit var spinnerStorage: Spinner
    private lateinit var etWithdrawalDate: EditText
    private lateinit var etNote: EditText
    private lateinit var btnAddPlant: Button
    private lateinit var btnImportData: Button
    private lateinit var btnImportExcel: Button

    private val gson = Gson()
    private val plantFileName = "plants.json"
    private val plants = mutableListOf<Plant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plant)

        etIdentification = findViewById(R.id.etIdentification)
        spinnerHealthStatus = findViewById(R.id.spinnerHealthStatus)
        etOrigin = findViewById(R.id.etOrigin)
        etDescription = findViewById(R.id.etDescription)
        spinnerStage = findViewById(R.id.spinnerStage)
        spinnerStorage = findViewById(R.id.spinnerStorage)
        etWithdrawalDate = findViewById(R.id.etWithdrawalDate)
        etNote = findViewById(R.id.etNote)
        btnAddPlant = findViewById(R.id.btnAddPlant)
        btnImportData = findViewById(R.id.btnImportData)
        btnImportExcel = findViewById(R.id.btnImportExcel)

        setupSpinners()
        setupDatePickers()
        loadPlants()

        btnAddPlant.setOnClickListener {
            addPlant()
        }

        btnImportData.setOnClickListener {
            importData()
        }

        btnImportExcel.setOnClickListener {
            importExcelData()
        }
    }

    private fun setupSpinners() {
        val healthItems = listOf(
            ColorItem(Color.RED, "Rouge"),
            ColorItem(Color.parseColor("#FFA500"), "Orange"),
            ColorItem(Color.YELLOW, "Jaune"),
            ColorItem(Color.GREEN, "Vert")
        )

        val healthStatusAdapter = ColorSpinnerAdapter(this, healthItems)
        spinnerHealthStatus.adapter = healthStatusAdapter

        val stageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Initiation", "Microdissection", "Magenta", "Double magenta", "Hydroponie"))
        stageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStage.adapter = stageAdapter

        val storageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Storage 1", "Storage 2"))
        storageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStorage.adapter = storageAdapter
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        val withdrawalDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val dateFormat = "yyyy-MM-dd"
            val sdf = java.text.SimpleDateFormat(dateFormat, Locale.US)
            etWithdrawalDate.setText(sdf.format(calendar.time))
        }

        etWithdrawalDate.setOnClickListener {
            DatePickerDialog(
                this, withdrawalDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun loadPlants() {
        val file = File(filesDir, plantFileName)
        if (file.exists()) {
            val fileReader = FileReader(file)
            val plantType = object : TypeToken<MutableList<Plant>>() {}.type
            val loadedPlants: MutableList<Plant> = gson.fromJson(fileReader, plantType)
            plants.clear()
            plants.addAll(loadedPlants)
            fileReader.close()
        }
    }

    private fun savePlants() {
        val file = File(filesDir, plantFileName)
        val fileWriter = FileWriter(file)
        gson.toJson(plants, fileWriter)
        fileWriter.flush()
        fileWriter.close()
    }

    private fun saveHistory(action: String, plant: Plant) {
        val file = File(filesDir, "history.json")
        val history = if (file.exists()) {
            val fileReader = FileReader(file)
            val historyType = object : TypeToken<MutableList<History>>() {}.type
            val loadedHistory: MutableList<History> = gson.fromJson(fileReader, historyType)
            fileReader.close()
            loadedHistory
        } else {
            mutableListOf()
        }

        val historyItem = History(
            action = action,
            plantId = plant.id,
            modifiedField = "",
            oldValue = "",
            newValue = "",
            timestamp = System.currentTimeMillis().toString()
        )
        history.add(historyItem)

        val fileWriter = FileWriter(file)
        gson.toJson(history, fileWriter)
        fileWriter.flush()
        fileWriter.close()
    }

    private fun generateUniqueIdentification(baseId: String): String {
        var id = baseId
        var counter = 1
        while (plants.any { it.id.startsWith(id) }) {
            id = "$baseId$counter"
            counter++
        }
        return id
    }

    private fun addPlant() {
        val baseId = etIdentification.text.toString()
        val uniqueId = generateUniqueIdentification(baseId)
        val healthStatus = (spinnerHealthStatus.selectedItem as ColorItem).color.toString()
        val origin = etOrigin.text.toString()
        val description = etDescription.text.toString()
        val stage = spinnerStage.selectedItem.toString()
        val storage = spinnerStorage.selectedItem.toString()
        val active = 1
        val withdrawalDate = etWithdrawalDate.text.toString()
        val note = etNote.text.toString()

        val plant = Plant(uniqueId, healthStatus, "", origin, description, stage, storage, active, withdrawalDate, "", "", note)
        plants.add(plant)

        // Générer et enregistrer le code QR
        val qrCodeBitmap = generateQRCode(uniqueId)
        saveQRCode(qrCodeBitmap, uniqueId)

        savePlants()
        saveHistory("Ajout", plant)

        Toast.makeText(this, "Plante ajoutée", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, InventoryActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun importData() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/json"
        startActivityForResult(intent, IMPORT_JSON_REQUEST_CODE)
    }

    private fun importExcelData() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Select Excel File"), IMPORT_EXCEL_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMPORT_JSON_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.also { uri ->
                contentResolver.openInputStream(uri)?.let { inputStream ->
                    val reader = inputStream.bufferedReader()
                    val importedPlants: List<Plant> = gson.fromJson(reader, object : TypeToken<List<Plant>>() {}.type)
                    importedPlants.forEach { plant ->
                        val uniqueId = generateUniqueIdentification(plant.id)
                        val newPlant = plant.copy(id = uniqueId)
                        plants.add(newPlant)

                        // Générer et enregistrer le code QR pour chaque plante importée
                        val qrCodeBitmap = generateQRCode(uniqueId)
                        saveQRCode(qrCodeBitmap, uniqueId)

                        // Enregistrer l'ajout dans l'historique
                        saveHistory("Ajout", newPlant)
                    }
                    savePlants()
                    Toast.makeText(this, "Données importées avec succès", Toast.LENGTH_SHORT).show()

                    // Rediriger vers la page d'inventaire
                    val intent = Intent(this, InventoryActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        } else if (requestCode == IMPORT_EXCEL_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.also { uri ->
                contentResolver.openInputStream(uri)?.let { inputStream ->
                    readExcelFile(inputStream)
                    savePlants()
                    Toast.makeText(this, "Données Excel importées avec succès", Toast.LENGTH_SHORT).show()

                    // Rediriger vers la page d'inventaire
                    val intent = Intent(this, InventoryActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }




    private fun readExcelFile(inputStream: InputStream) {
        try {
            val workbook: Workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)
            for (row in sheet) {
                if (row.rowNum == 0) continue // Skip header row

                val id = generateUniqueIdentification(getCellValueAsString(row.getCell(0)))
                val healthStatus = convertHealthStatus(getCellValueAsString(row.getCell(1)))
                val date = getCellValueAsString(row.getCell(2))
                val origin = getCellValueAsString(row.getCell(3))
                val description = getCellValueAsString(row.getCell(4))
                val stage = getCellValueAsString(row.getCell(5))
                val storage = getCellValueAsString(row.getCell(6))
                val active = getCellValueAsString(row.getCell(7)).toInt()
                val withdrawalDate = getCellValueAsString(row.getCell(8))
                val note = getCellValueAsString(row.getCell(9))

                // Create Plant object with empty removalReason and decontaminationResponsible
                val plant = Plant(id, healthStatus, date, origin, description, stage, storage, active, withdrawalDate, "", "", note)

                // Générer et enregistrer le code QR pour chaque plante importée
                val qrCodeBitmap = generateQRCode(id)
                saveQRCode(qrCodeBitmap, id)

                plants.add(plant)
            }
            workbook.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erreur lors de la lecture du fichier Excel", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCellValueAsString(cell: org.apache.poi.ss.usermodel.Cell?): String {
        return if (cell == null) {
            ""
        } else {
            when (cell.cellType) {
                CellType.STRING -> cell.stringCellValue
                CellType.NUMERIC -> cell.numericCellValue.toString()
                CellType.BOOLEAN -> cell.booleanCellValue.toString()
                CellType.FORMULA -> cell.cellFormula
                else -> ""
            }
        }
    }

    private fun convertHealthStatus(status: String): String {
        return when (status) {
            "Rouge" -> Color.RED.toString()
            "Orange" -> Color.parseColor("#FFA500").toString()
            "Jaune" -> Color.YELLOW.toString()
            "Vert" -> Color.GREEN.toString()
            else -> Color.BLACK.toString() // default color
        }
    }

    private fun generateQRCode(plantId: String): Bitmap {
        val size = 512 // Taille de l'image QR
        val qrCodeWriter = QRCodeWriter()
        return try {
            val bitMatrix = qrCodeWriter.encode(plantId, BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        }
    }

    private fun saveQRCode(bitmap: Bitmap, plantId: String) {
        val qrCodeFile = File(filesDir, "$plantId.png")
        try {
            val outputStream = FileOutputStream(qrCodeFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val IMPORT_JSON_REQUEST_CODE = 1
        const val IMPORT_EXCEL_REQUEST_CODE = 2
    }
}

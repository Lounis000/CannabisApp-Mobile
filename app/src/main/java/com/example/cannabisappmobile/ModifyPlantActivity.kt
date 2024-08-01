package com.example.cannabisappmobile

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

class ModifyPlantActivity : AppCompatActivity() {

    private lateinit var etIdentification: EditText
    private lateinit var spinnerHealthStatus: Spinner
    private lateinit var etOrigin: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerStage: Spinner
    private lateinit var spinnerStorage: Spinner
    private lateinit var etWithdrawalDate: EditText
    private lateinit var etNote: EditText
    private lateinit var btnSaveChanges: Button

    private val gson = Gson()
    private val plantFileName = "plants.json"
    private val historyFileName = "history.json"
    private val plants = mutableListOf<Plant>()
    private val storageOptions = mutableListOf("Storage 1", "Storage 2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_plant)

        etIdentification = findViewById(R.id.etIdentification)
        spinnerHealthStatus = findViewById(R.id.spinnerHealthStatus)
        etOrigin = findViewById(R.id.etOrigin)
        etDescription = findViewById(R.id.etDescription)
        spinnerStage = findViewById(R.id.spinnerStage)
        spinnerStorage = findViewById(R.id.spinnerStorage)
        etWithdrawalDate = findViewById(R.id.etWithdrawalDate)
        etNote = findViewById(R.id.etNote)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)

        setupSpinners()
        setupDatePickers()
        loadPlants()

        val plantId = intent.getStringExtra("PLANT_ID")
        val plant = loadPlantById(plantId)
        if (plant != null) {
            populatePlantDetails(plant)
        }

        btnSaveChanges.setOnClickListener {
            savePlantChanges()
        }
    }

    private fun setupSpinners() {
        val healthItems = listOf(
            ColorItem(android.graphics.Color.RED, "Rouge"),
            ColorItem(android.graphics.Color.parseColor("#FFA500"), "Orange"),
            ColorItem(android.graphics.Color.YELLOW, "Jaune"),
            ColorItem(android.graphics.Color.GREEN, "Vert")
        )

        val healthStatusAdapter = ColorSpinnerAdapter(this, healthItems)
        spinnerHealthStatus.adapter = healthStatusAdapter

        val stageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Initiation", "Microdissection", "Magenta", "Double magenta", "Hydroponie"))
        stageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStage.adapter = stageAdapter

        val storageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, storageOptions)
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

    private fun saveHistory(action: String, plant: Plant, modifiedField: String, oldValue: String, newValue: String) {
        val file = File(filesDir, historyFileName)
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
            modifiedField = modifiedField,
            oldValue = oldValue,
            newValue = newValue,
            timestamp = System.currentTimeMillis().toString()
        )
        history.add(historyItem)

        val fileWriter = FileWriter(file)
        gson.toJson(history, fileWriter)
        fileWriter.flush()
        fileWriter.close()
    }

    private fun loadPlantById(plantId: String?): Plant? {
        return plants.find { it.id == plantId }
    }

    private fun populatePlantDetails(plant: Plant) {
        etIdentification.setText(plant.id)
        etIdentification.isEnabled = false
        etDescription.setText(plant.description)
        etOrigin.setText(plant.origin)
        etWithdrawalDate.setText(plant.withdrawalDate)
        etNote.setText(plant.note)
        spinnerHealthStatus.setSelection(getHealthStatusPosition(plant.healthStatus.toInt()))
        spinnerStage.setSelection(getStagePosition(plant.stage))
        // Ajouter la vérification et l'ajout du stockage
        addStorageIfNotExists(plant.storage)
        spinnerStorage.setSelection(getStoragePosition(plant.storage))
    }

    private fun getHealthStatusPosition(healthStatus: Int): Int {
        val healthColors = listOf(
            android.graphics.Color.RED,
            android.graphics.Color.parseColor("#FFA500"),
            android.graphics.Color.YELLOW,
            android.graphics.Color.GREEN
        )
        return healthColors.indexOf(healthStatus)
    }

    private fun getStagePosition(stage: String): Int {
        val stages = listOf("Initiation", "Microdissection", "Magenta", "Double magenta", "Hydroponie")
        return stages.indexOf(stage)
    }

    private fun getStoragePosition(storage: String): Int {
        return storageOptions.indexOf(storage)
    }

    private fun addStorageIfNotExists(storage: String) {
        if (storage !in storageOptions) {
            storageOptions.add(storage)
            (spinnerStorage.adapter as ArrayAdapter<String>).notifyDataSetChanged()
        }
    }

    private fun savePlantChanges() {
        val plantId = etIdentification.text.toString()
        val plant = plants.find { it.id == plantId }

        if (plant != null) {
            val newDescription = etDescription.text.toString()
            val newOrigin = etOrigin.text.toString()
            val newHealthStatus = (spinnerHealthStatus.selectedItem as ColorItem).color.toString()
            val newStage = spinnerStage.selectedItem.toString()
            val newStorage = spinnerStorage.selectedItem.toString()
            val newWithdrawalDate = etWithdrawalDate.text.toString()
            val newNote = etNote.text.toString()

            val oldDescription = plant.description
            val oldOrigin = plant.origin
            val oldHealthStatus = plant.healthStatus
            val oldStage = plant.stage
            val oldStorage = plant.storage
            val oldWithdrawalDate = plant.withdrawalDate ?: ""
            val oldNote = plant.note

            if (oldDescription != newDescription) saveHistory("Modification", plant, "description", oldDescription, newDescription)
            if (oldOrigin != newOrigin) saveHistory("Modification", plant, "origin", oldOrigin, newOrigin)
            if (oldHealthStatus != newHealthStatus) saveHistory("Modification", plant, "healthStatus", oldHealthStatus, newHealthStatus)
            if (oldStage != newStage) saveHistory("Modification", plant, "stage", oldStage, newStage)
            if (oldStorage != newStorage) saveHistory("Modification", plant, "storage", oldStorage, newStorage)
            if (oldWithdrawalDate != newWithdrawalDate) saveHistory("Modification", plant, "withdrawalDate", oldWithdrawalDate, newWithdrawalDate)
            if (oldNote != newNote) saveHistory("Modification", plant, "note", oldNote, newNote)

            plant.description = newDescription
            plant.origin = newOrigin
            plant.healthStatus = newHealthStatus
            plant.stage = newStage
            plant.storage = newStorage
            plant.withdrawalDate = newWithdrawalDate
            plant.note = newNote

            savePlants()
            // Ajouter la vérification et l'ajout du stockage
            addStorageIfNotExists(newStorage)

            Toast.makeText(this, "Plante mise à jour", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
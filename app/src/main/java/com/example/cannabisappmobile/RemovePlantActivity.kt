package com.example.cannabisappmobile

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class RemovePlantActivity : AppCompatActivity() {

    private lateinit var spinnerResponsible: Spinner
    private lateinit var spinnerReason: Spinner
    private lateinit var etNote: EditText
    private lateinit var btnRemovePlant: Button

    private val gson = Gson()
    private val plantFileName = "plants.json"
    private val historyFileName = "history.json"
    private var plant: Plant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_plant)

        spinnerResponsible = findViewById(R.id.spinnerResponsible)
        spinnerReason = findViewById(R.id.spinnerReason)
        etNote = findViewById(R.id.etNote)
        btnRemovePlant = findViewById(R.id.btnRemovePlant)

        val plantId = intent.getStringExtra("PLANT_ID")
        plant = loadPlantById(plantId)

        if (plant == null) {
            Toast.makeText(this, "Erreur: Plante introuvable", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupSpinners()

        btnRemovePlant.setOnClickListener {
            removePlant()
        }
    }

    private fun setupSpinners() {
        val responsiblePersons = arrayOf(
            "Kadija Houssein Youssouf",
            "Alexandre Tromas"
        )
        val responsibleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, responsiblePersons)
        responsibleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerResponsible.adapter = responsibleAdapter

        val reasons = arrayOf(
            "DESTRUCTION PAR AUTOCLAVE",
            "TRANSFERT CLIENT",
            "TRANSFERT AUTRE CENTRE",
            "AUTRE (INDIQUER LA RAISON DANS NOTE)",
            "TRANSFERT POUR ANALYSE",
            "ANALYSE",
            "CONTAMINATION",
            "LIMITATION DE LA LICENCE",
            "PERTE DE L'ÉCHANTILLION"
        )
        val reasonAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, reasons)
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerReason.adapter = reasonAdapter
    }

    private fun loadPlantById(plantId: String?): Plant? {
        val file = File(filesDir, plantFileName)
        if (file.exists()) {
            val fileReader = FileReader(file)
            val plantType = object : TypeToken<MutableList<Plant>>() {}.type
            val plants: List<Plant> = gson.fromJson(fileReader, plantType)
            fileReader.close()
            return plants.find { it.id == plantId }
        }
        return null
    }

    private fun savePlants(plants: List<Plant>) {
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

    private fun removePlant() {
        plant?.let {
            it.decontaminationResponsible = spinnerResponsible.selectedItem.toString()
            it.removalReason = spinnerReason.selectedItem.toString()
            it.note = etNote.text.toString()
            it.active = 0

            val file = File(filesDir, plantFileName)
            if (file.exists()) {
                val fileReader = FileReader(file)
                val plantType = object : TypeToken<MutableList<Plant>>() {}.type
                val plants: MutableList<Plant> = gson.fromJson(fileReader, plantType)
                fileReader.close()

                val plantIndex = plants.indexOfFirst { p -> p.id == it.id }
                if (plantIndex != -1) {
                    plants[plantIndex] = it
                    savePlants(plants)
                    saveHistory("Retrait", it, "active", "1", "0")
                    Toast.makeText(this, "Plante retirée avec succès", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erreur: Plante introuvable", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

package com.example.cannabisappmobile

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import android.graphics.Bitmap

class PlantDetailActivity : AppCompatActivity() {

    private lateinit var tvPlantID: TextView
    private lateinit var tvPlantName: TextView
    private lateinit var tvPlantOrigin: TextView
    private lateinit var tvPlantStage: TextView
    private lateinit var tvPlantStorage: TextView
    private lateinit var vPlantHealthStatus: View
    private lateinit var tvPlantDate: TextView
    private lateinit var tvPlantWithdrawalDate: TextView
    private lateinit var tvPlantResponsible: TextView
    private lateinit var tvPlantReason: TextView
    private lateinit var tvPlantNote: TextView
    private lateinit var ivQRCode: ImageView
    private lateinit var btnPrint: Button
    private lateinit var btnRemovePlant: Button
    private lateinit var btnModifyPlant: Button

    private val gson = Gson()
    private val plantFileName = "plants.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_detail)

        tvPlantID = findViewById(R.id.tvPlantID)
        tvPlantName = findViewById(R.id.tvPlantName)
        tvPlantOrigin = findViewById(R.id.tvPlantOrigin)
        tvPlantStage = findViewById(R.id.tvPlantStage)
        tvPlantStorage = findViewById(R.id.tvPlantStorage)
        vPlantHealthStatus = findViewById(R.id.vPlantHealthStatus)
        tvPlantDate = findViewById(R.id.tvPlantWithdrawalDate)
        tvPlantWithdrawalDate = findViewById(R.id.tvPlantWithdrawalDate)
        tvPlantResponsible = findViewById(R.id.tvPlantResponsible)
        tvPlantReason = findViewById(R.id.tvPlantReason)
        tvPlantNote = findViewById(R.id.tvPlantNote)
        ivQRCode = findViewById(R.id.ivQRCode)
        btnPrint = findViewById(R.id.btnPrint)
        btnRemovePlant = findViewById(R.id.btnRemovePlant)
        btnModifyPlant = findViewById(R.id.btnModifyPlant)

        val plantId = intent.getStringExtra("PLANT_ID")
        val plant = loadPlantById(plantId)

        if (plant != null) {
            tvPlantID.text = plant.id
            tvPlantName.text = plant.description
            tvPlantOrigin.text = "Provenance: ${plant.origin}"
            tvPlantStage.text = "Stade: ${plant.stage}"
            tvPlantStorage.text = "Stockage: ${plant.storage}"
            setHealthStatusColor(plant.healthStatus.toInt())
            tvPlantDate.text = "Date d'ajout: ${plant.date}"
            tvPlantWithdrawalDate.text = "Date de retrait: ${plant.withdrawalDate}"
            tvPlantResponsible.text = "Responsable: ${plant.decontaminationResponsible}"
            tvPlantReason.text = "Raison: ${plant.removalReason}"
            tvPlantNote.text = "Note: ${plant.note}"

            val qrCodeFile = File(filesDir, "${plant.id}.png")
            if (qrCodeFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(qrCodeFile.path)
                ivQRCode.setImageBitmap(bitmap)
            }
        }

        btnPrint.setOnClickListener {
            createAndSavePDF()
        }
        btnRemovePlant.setOnClickListener {
            val intent = Intent(this, RemovePlantActivity::class.java)
            intent.putExtra("PLANT_ID", tvPlantID.text.toString())
            startActivity(intent)
        }
        btnModifyPlant.setOnClickListener {
            val intent = Intent(this, ModifyPlantActivity::class.java)
            intent.putExtra("PLANT_ID", tvPlantID.text.toString())
            startActivity(intent)
        }
    }

    private fun setHealthStatusColor(healthStatus: Int) {
        val color = when (healthStatus) {
            Color.RED -> Color.RED
            Color.parseColor("#FFA500") -> Color.parseColor("#FFA500") // Couleur orange
            Color.YELLOW -> Color.YELLOW
            Color.GREEN -> Color.GREEN
            else -> Color.GREEN
        }
        vPlantHealthStatus.setBackgroundColor(color)
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

    private fun createAndSavePDF() {
        val plantId = tvPlantID.text.toString()
        val document = Document()

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val plantsDir = File(downloadsDir, "Plantes")
        if (!plantsDir.exists()) {
            plantsDir.mkdirs()
        }

        val file = File(plantsDir, "plante_$plantId.pdf")
        try {
            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()
            document.add(Paragraph("ID: ${tvPlantID.text}"))
            document.add(Paragraph("Nom: ${tvPlantName.text}"))
            document.add(Paragraph("Provenance: ${tvPlantOrigin.text}"))
            document.add(Paragraph("Stade: ${tvPlantStage.text}"))
            document.add(Paragraph("Stockage: ${tvPlantStorage.text}"))
            document.add(Paragraph("État de santé:"))
            document.add(Paragraph("Date d'ajout: ${tvPlantDate.text}"))
            document.add(Paragraph("Date de retrait: ${tvPlantWithdrawalDate.text}"))
            document.add(Paragraph("Responsable: ${tvPlantResponsible.text}"))
            document.add(Paragraph("Raison: ${tvPlantReason.text}"))
            document.add(Paragraph("Note: ${tvPlantNote.text}"))

            // Ajouter le QR Code
            val qrCodeBitmap = (ivQRCode.drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = com.itextpdf.text.Image.getInstance(stream.toByteArray())
            document.add(image)

            document.close()
            Toast.makeText(this, "PDF saved in Download/Plantes as plante_$plantId.pdf", Toast.LENGTH_LONG).show()
        } catch (e: DocumentException) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

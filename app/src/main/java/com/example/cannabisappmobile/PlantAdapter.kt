package com.example.cannabisappmobile

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class PlantAdapter(private val plants: List<Plant>) :
    RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plants[position]
        holder.tvPlantName.text = plant.description
        holder.tvPlantOrigin.text = "Provenance: ${plant.origin}"
        holder.tvPlantStage.text = "Stade: ${plant.stage}"
        holder.tvPlantId.text = plant.id

        // Set the health status color
        val healthColor = when (plant.healthStatus.toInt()) {
            Color.RED -> Color.RED
            Color.parseColor("#FFA500") -> Color.parseColor("#FFA500")
            Color.YELLOW -> Color.YELLOW
            Color.GREEN -> Color.GREEN
            else -> Color.GRAY
        }
        holder.vPlantHealthColor.setBackgroundColor(healthColor)

        // Load the QR code from the file
        val qrCodeFile = File(holder.itemView.context.filesDir, "${plant.id}.png")
        if (qrCodeFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(qrCodeFile.path)
            holder.ivQRCode.setImageBitmap(bitmap)
        } else {
            holder.ivQRCode.setImageResource(R.drawable.ic_qr_code_placeholder)
        }

        // Set click listener
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlantDetailActivity::class.java)
            intent.putExtra("PLANT_ID", plant.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return plants.size
    }

    class PlantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlantName: TextView = view.findViewById(R.id.tvPlantName)
        val tvPlantOrigin: TextView = view.findViewById(R.id.tvPlantOrigin)
        val tvPlantStage: TextView = view.findViewById(R.id.tvPlantStage)
        val tvPlantId: TextView = view.findViewById(R.id.tvPlantId)
        val ivQRCode: ImageView = view.findViewById(R.id.ivQRCode)
        val vPlantHealthColor: View = view.findViewById(R.id.vPlantHealthColor)
    }
}

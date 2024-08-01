package com.example.cannabisappmobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val historyList: List<History>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPlantId: TextView = itemView.findViewById(R.id.tvPlantId)
        val tvAction: TextView = itemView.findViewById(R.id.tvAction)
        val tvModifiedField: TextView = itemView.findViewById(R.id.tvModifiedField)
        val tvOldValue: TextView = itemView.findViewById(R.id.tvOldValue)
        val tvNewValue: TextView = itemView.findViewById(R.id.tvNewValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = historyList[position]
        holder.tvPlantId.text = "ID de la plante: ${currentItem.plantId}"
        holder.tvAction.text = "Action: ${currentItem.action}"
        holder.tvModifiedField.text = "Champ modifié: ${currentItem.modifiedField}"
        holder.tvOldValue.text = "Ancienne valeur: ${currentItem.oldValue}"
        holder.tvNewValue.text = "Nouvelle valeur: ${currentItem.newValue}"
    }

    override fun getItemCount() = historyList.size
}

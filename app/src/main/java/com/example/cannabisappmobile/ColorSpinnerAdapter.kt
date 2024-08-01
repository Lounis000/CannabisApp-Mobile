package com.example.cannabisappmobile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ColorSpinnerAdapter(
    private val context: Context,
    private val items: List<ColorItem>
) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_color, parent, false)
        val colorView = view.findViewById<View>(R.id.colorView)
        val colorText = view.findViewById<TextView>(R.id.colorText)

        colorView.setBackgroundColor(items[position].color)
        colorText.text = items[position].name

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }
}

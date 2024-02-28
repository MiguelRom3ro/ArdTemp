package com.itess.ardtemp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itess.ardtemp.R
import com.itess.ardtemp.Temperature

class TemperatureViewAdapter (private val temperatures: List<Temperature>) : RecyclerView.Adapter<TemperatureViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemperatureViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TemperatureViewHolder(layoutInflater.inflate(R.layout.temperatures_view, parent, false))
    }

    override fun getItemCount(): Int = temperatures.size

    override fun onBindViewHolder(holder: TemperatureViewHolder, position: Int) {
        val item = temperatures[position]

        holder.render(item)
    }
}
package com.calendar.calendar.view.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.calendar.calendar.R
import com.calendar.calendar.databinding.ItemHistoryBinding

class HistoryViewHolder(
    private val binding: ItemHistoryBinding,
    private val locationClick: (
        lat: String,
        lon: String
    ) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        hostory: String,
        latLon: Pair<String, String>,
        position: Int,
        type: String,
        context: Context
    ) {
        binding.apply {
            histiry.text = hostory
            if (position % 2 == 0) {
                itemMain.setBackgroundColor(context.getColor(R.color.row1))
            } else {
                itemMain.setBackgroundColor(context.getColor(R.color.row2))
            }

            if (type == "ADMIN") {
                btnlocation.visibility = View.VISIBLE
                btnlocation.setOnClickListener { locationClick.invoke(latLon.first, latLon.second)}
            }
        }

    }
}
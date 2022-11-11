package com.calendar.calendar.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.calendar.calendar.databinding.ItemHistoryBinding
import com.calendar.calendar.model.Prestamo
import com.calendar.calendar.model.User

class HistoryAdapter(
    private val context: Context,
    private val type: String,
    private val locationClick: (
        lat: String,
        lon: String
    ) -> Unit
) : ListAdapter<Prestamo, HistoryViewHolder>(HistoryAdapter.TaskDiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HistoryViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(context), parent, false), ::locationClick)

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(
            "Monto ${getItem(position).montoFinal} fecha ${getItem(position).dateRegisterPay} \n Cobrado por: ${getItem(position).employeName} ",
            Pair(getItem(position).latitude, getItem(position).longitude), position, type, context
        )
    }

    private fun locationClick(
        lat: String,
        lon: String
    ) {
        locationClick.invoke(lat, lon)
    }

    object TaskDiffCallBack : DiffUtil.ItemCallback<Prestamo>() {
        override fun areContentsTheSame(oldItem: Prestamo, newItem: Prestamo): Boolean =
            oldItem == newItem

        override fun areItemsTheSame(oldItem: Prestamo, newItem: Prestamo): Boolean =
            oldItem.curp == newItem.curp
    }
}

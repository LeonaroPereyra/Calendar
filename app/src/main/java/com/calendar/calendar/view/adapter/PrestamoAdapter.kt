package com.calendar.calendar.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.calendar.calendar.databinding.ItemPrestamoBinding
import com.calendar.calendar.model.Prestamo

class PrestamoAdapter(
    private val context: Context,
    private val prestamoSelected: (
        prestamo: Prestamo
    ) -> Unit,
    private val type: String,
    private val location: (
        lat: String,
        lon: String
    ) -> Unit
) : ListAdapter<Prestamo, PrestamoViewHolder>(TaskDiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoViewHolder =
        PrestamoViewHolder(
            ItemPrestamoBinding.inflate(LayoutInflater.from(context), parent, false),
            ::prestamoClicked, ::locationClicked
        )

    private fun prestamoClicked(prestamo: Prestamo) {
        prestamoSelected.invoke(prestamo)
    }

    private fun locationClicked( lat: String,
                                 lon: String) {
        location.invoke(lat,lon)
    }

    override fun onBindViewHolder(holder: PrestamoViewHolder, position: Int) =
        holder.bind(getItem(position), position, context, type)

    object TaskDiffCallBack : DiffUtil.ItemCallback<Prestamo>() {

        override fun areItemsTheSame(oldItem: Prestamo, newItem: Prestamo): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Prestamo, newItem: Prestamo): Boolean =
            oldItem.curp == newItem.curp

    }
}
package com.calendar.calendar.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.calendar.calendar.R
import com.calendar.calendar.databinding.ItemPrestamoBinding

import com.calendar.calendar.model.Prestamo

class PrestamoViewHolder(
    private val binding: ItemPrestamoBinding,
    private val prestamoSelected: (
        prestamo: Prestamo
    ) -> Unit,
    private val locationClick: (
        lat: String,
        lon: String
    ) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(prestamo: Prestamo?, position: Int, context: Context, type: String) {
        binding.apply {
            curp.setText(prestamo?.curp.toString())
            username.setText(prestamo?.name.toString())
            fechaFin.setText(prestamo?.fechaFin.toString())
            fechaInit.setText(prestamo?.fechaInicio.toString())
            pagado.setText(prestamo?.saldo).toString()
            montoFinal.setText(prestamo?.montoFinal.toString())
            status.setText(prestamo?.isActived.toString())
            cobro.setText(prestamo?.interesDiario.toString())
            employeName.setText(prestamo?.employeName.toString())
            val resto: Float =
                prestamo?.montoFinal?.toFloat()?.minus(prestamo?.saldo?.toFloat()!!) ?: 0f
            pagado.setText(prestamo?.saldo)
            faltante.setText(resto.toString())
            noAbono.setText(
                prestamo?.noAbonos.toString().plus(" / ").plus(prestamo?.totalAbonos.toString())
            )
            itemPrestamo.setOnClickListener {
                if (prestamo != null) {
                    prestamoSelected.invoke(prestamo)
                }
            }
            if (position % 2 == 0) {
                itemPrestamo.setBackgroundColor(context.getColor(R.color.row1))
            } else {
                itemPrestamo.setBackgroundColor(context.getColor(R.color.row2))
            }
            if(type == "ADMIN"){
                location.visibility = View.VISIBLE
                location.setOnClickListener { locationClick.invoke(prestamo?.latitude?:"", prestamo?.longitude?:"") }
            }
        }
    }

}
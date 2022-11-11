package com.calendar.calendar.viewModel

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.calendar.calendar.R
import com.calendar.calendar.databinding.ItemEnployeBinding
import com.calendar.calendar.model.Employee

class EmploeViewHolder(
    private val binding: ItemEnployeBinding,
    private val employeSelected: (employe: Employee) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(employe: Employee, context: Context) {
        employe.let {
            binding.apply {
                name.setText(it?.userName?.orEmpty())
                status.setText(it?.isActived?.orEmpty())
                type.setText(it?.type?.orEmpty())
                itemEmploye.setOnClickListener {
                    employe?.let { it1 -> employeSelected.invoke(it1) }
                }

                if (position % 2 == 0) {
                    itemEmploye.setBackgroundColor(context.getColor(R.color.row1))
                } else {
                    itemEmploye.setBackgroundColor(context.getColor(R.color.row2))
                }
            }
        }
    }
}
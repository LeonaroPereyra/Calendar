package com.calendar.calendar.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.calendar.calendar.databinding.ItemEnployeBinding
import com.calendar.calendar.model.Employee
import com.calendar.calendar.viewModel.EmploeViewHolder

class EmployeAdapter(
    private val context: Context,
    private val employeSelected: (
        employe: Employee
    ) -> Unit
) : ListAdapter<Employee, EmploeViewHolder>(TaskDiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmploeViewHolder =
        EmploeViewHolder(ItemEnployeBinding.inflate(LayoutInflater.from(context), parent, false),::employeSelected)

    override fun onBindViewHolder(holder: EmploeViewHolder, position: Int) =
        holder.bind(getItem(position), context)

    object TaskDiffCallBack : DiffUtil.ItemCallback<Employee>() {
        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean =
            oldItem == newItem

        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean =
            oldItem.userName == newItem.userName
    }

    private fun employeSelected(employe:Employee){
        employeSelected.invoke(employe)
    }
}
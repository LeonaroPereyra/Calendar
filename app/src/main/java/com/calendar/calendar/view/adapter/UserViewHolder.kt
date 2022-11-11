package com.calendar.calendar.view.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.view.View.VISIBLE
import androidx.recyclerview.widget.RecyclerView
import com.calendar.calendar.R
import com.calendar.calendar.databinding.ItemUserBinding
import com.calendar.calendar.model.User

class UserViewHolder(
    private val binding: ItemUserBinding,
    private val userSelected: (
        user: User
    ) -> Unit,
    private val callSelected: (
        numberPhone: String
    ) -> Unit,
    private val gallerySelected: (
        curp: String
    ) -> Unit,
    private val type: String
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: User?, context: Context) {
        user.let {
            binding.apply {
                name.setText(it?.name?.orEmpty())
                lastName.setText(it?.lastName.orEmpty())
                curp.setText(it?.curp.orEmpty())
                aka.setText(it?.aka.orEmpty())
                noId.setText(it?.employeeName.orEmpty())
                activo.setText(it?.active.orEmpty())
                btnCall.setOnClickListener {
                    user?.let { it1 ->
                        callSelected.invoke(
                            it1.aka ?: ""
                        )
                    }
                }
                if (type == "ADMIN") {
                    btnGallery.visibility = VISIBLE
                    btnGallery.setOnClickListener {
                        user?.curp?.let { it1 -> gallerySelected.invoke(it1) }
                    }
                }
                itemUser.setOnClickListener {
                    user?.let { it1 -> userSelected.invoke(it1) }
                }
                if (position % 2 == 0) {
                    itemUser.setBackgroundColor(context.getColor(R.color.row1))
                } else {
                    itemUser.setBackgroundColor(context.getColor(R.color.row2))
                }
            }

        }
    }
}
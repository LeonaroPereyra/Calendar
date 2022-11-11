package com.calendar.calendar.view.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.calendar.calendar.databinding.ItemGalleryBinding

class GalleryViewHolder(
    private val binding: ItemGalleryBinding,
    private val imageSelected: (
        urlImage: String
    ) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(urlString: Pair<String,String>, context: Context) {
        Glide.with(context).load(urlString.first).placeholder(ColorDrawable(Color.BLACK))
            .into(binding.imageGallery)
        binding.btnDelete.setOnClickListener{
            imageSelected.invoke(urlString.second)
        }
    }
}

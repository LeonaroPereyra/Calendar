package com.calendar.calendar.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.calendar.calendar.databinding.ItemGalleryBinding

class GalleryAdapter(
    private val context: Context,
    private val imageSelected: (
        urlImage: String
    ) -> Unit
) : ListAdapter<Pair<String, String>, GalleryViewHolder>(TaskDiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder =
        GalleryViewHolder(
            ItemGalleryBinding.inflate(LayoutInflater.from(context), parent, false),
            ::imageSelected
        )

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(getItem(position), context)
    }

    private fun imageSelected(urlImage: String) {
        imageSelected.invoke(urlImage)
    }

    object TaskDiffCallBack : DiffUtil.ItemCallback<Pair<String,String>>() {
        override fun areContentsTheSame(oldItem: Pair<String,String>, newItem: Pair<String,String>): Boolean =
            oldItem == newItem

        override fun areItemsTheSame(oldItem: Pair<String,String>, newItem: Pair<String,String>): Boolean =
            oldItem == newItem
    }
}
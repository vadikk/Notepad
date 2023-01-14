package com.example.notepad.ui.colorPicker.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.notepad.ui.colorPicker.ColorItem

class ColorAdapter(
    private val selectColor: (uid: Int) -> Unit
): ListAdapter<ColorItem, ColorItemVH>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorItemVH = ColorItemVH.create(parent)

    override fun onBindViewHolder(holder: ColorItemVH, position: Int) {
        val item = getItem(holder.adapterPosition)

        holder.bind(item) { selectColor(item.colorID) }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<ColorItem>() {
        override fun areItemsTheSame(oldItem: ColorItem, newItem: ColorItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ColorItem, newItem: ColorItem): Boolean {
            return oldItem == newItem
        }

    }
}
package com.example.notepad.ui.colorPicker.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.databinding.ColorItemBinding
import com.example.notepad.ui.colorPicker.ColorItem

class ColorItemVH(val binding: ColorItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(colorItem: ColorItem?, click: () -> Unit) {
        if (colorItem == null) return
        binding.selectColor.visibility = View.INVISIBLE


        with(binding) {
            if (colorItem.isChecked) selectColor.visibility = View.VISIBLE

            itemColor.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(itemView.context, colorItem.colorID))
            itemColor.setOnClickListener { click() }
        }
    }

    companion object {
        fun create(parent: ViewGroup): ColorItemVH {
            val binding =
                ColorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ColorItemVH(binding)
        }
    }
}
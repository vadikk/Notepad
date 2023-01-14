package com.example.notepad.ui.folderList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.databinding.FolderIdleLayoutBinding
import com.example.notepad.ui.folderList.FolderEntity

class FolderIdleVH(val binding: FolderIdleLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(entity: FolderEntity?, click: () -> Unit) {
        if (entity == null) return

        with(binding) {
            folderLayout.setOnClickListener { click() }
        }
    }

    companion object {
        fun create(parent: ViewGroup): FolderIdleVH {
            val binding =
                FolderIdleLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FolderIdleVH(binding)
        }
    }
}
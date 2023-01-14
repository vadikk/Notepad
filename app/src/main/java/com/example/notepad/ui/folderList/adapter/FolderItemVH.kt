package com.example.notepad.ui.folderList.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.R
import com.example.notepad.data.model.Folder
import com.example.notepad.databinding.ItemFolderLayoutBinding
import com.example.notepad.ui.folderList.FolderEntity

class FolderItemVH(val binding: ItemFolderLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(entity: FolderEntity?, click: () -> Unit) {
        if (entity == null) return
        val folder = (entity as FolderEntity.FolderItem).folder

        val folderBg = if (folder.isSelected) R.drawable.bg_folder_selected else R.drawable.bg_folder

        with(binding) {
            bgFolder.background = ContextCompat.getDrawable(itemView.context, folderBg)

            titleFolder.text = folder.title
            countNote.text = folder.countNote.toString()

            folderLayout.setOnClickListener { click() }
        }
    }

    fun binPayload(entity: FolderEntity?) {
        if (entity == null) return
        val folder = (entity as FolderEntity.FolderItem).folder

        with(binding) {
            titleFolder.text = folder.title
            countNote.text = folder.countNote.toString()
        }
    }


    companion object {
        fun create(parent: ViewGroup): FolderItemVH {
            val binding =
                ItemFolderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FolderItemVH(binding)
        }
    }
}
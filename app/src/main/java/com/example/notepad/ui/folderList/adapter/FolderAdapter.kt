package com.example.notepad.ui.folderList.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.ui.folderList.FolderEntity

class FolderAdapter(
    private val createFolder: () -> Unit,
    private val selectFolder: (uid: Int?) -> Unit
): ListAdapter<FolderEntity, RecyclerView.ViewHolder>(FolderDiffCallback()) {

    override fun getItem(position: Int) = currentList.getOrNull(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is FolderEntity.FolderItem -> FOLDER_TYPE_ITEM
        is FolderEntity.FolderIdle -> FOLDER_TYPE_IDLE
        else -> TYPE_UNKNOWN
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when(viewType) {
            FOLDER_TYPE_IDLE -> FolderIdleVH.create(parent)
            FOLDER_TYPE_ITEM -> FolderItemVH.create(parent)
            else -> throw IllegalArgumentException("Unknown viewType $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(holder.adapterPosition)

        when(holder) {
            is FolderIdleVH -> holder.bind(item) { createFolder() }
            is FolderItemVH -> holder.bind(item) {
                val uid = (item as? FolderEntity.FolderItem)?.folder?.uid
                selectFolder(uid)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) super.onBindViewHolder(holder, position, payloads)
        else {
            val item = getItem(holder.adapterPosition)

            if (holder is FolderItemVH) holder.binPayload(item)
        }
    }

    class FolderDiffCallback : DiffUtil.ItemCallback<FolderEntity>() {
        override fun areItemsTheSame(oldItem: FolderEntity, newItem: FolderEntity): Boolean {
            return if(oldItem is FolderEntity.FolderItem && newItem is FolderEntity.FolderItem)
                oldItem.folder.uid == newItem.folder.uid
            else if(oldItem is FolderEntity.FolderIdle && newItem is FolderEntity.FolderIdle)
                oldItem.hashCode() == newItem.hashCode()
            else false
        }

        override fun areContentsTheSame(oldItem: FolderEntity, newItem: FolderEntity): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: FolderEntity, newItem: FolderEntity): Any? {
            return true
        }

    }
}

const val TYPE_UNKNOWN = -1
const val FOLDER_TYPE_IDLE = 0
const val FOLDER_TYPE_ITEM = 1
package com.example.notepad.ui.mainScreen.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.example.notepad.data.model.Note
import com.example.notepad.ui.mainScreen.NoteModifyState

class NoteAdapter(
    private val layoutManager: StaggeredGridLayoutManager? = null,
    private val openDetail: (note: Note) -> Unit,
    private val selectNote: (uid: Int?, isChecked: Boolean) -> Unit
): ListAdapter<NoteModifyState, RecyclerView.ViewHolder>(NoteDiffCallback()) {

    private var canOpenDetail = true

    fun changeCanOpenDetailMode(canOpen: Boolean) {
        canOpenDetail = canOpen
    }

    override fun getItemViewType(position: Int): Int {
        return if (layoutManager?.spanCount == 1) ViewType.LIST.ordinal
        else ViewType.GRID.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return when (viewType) {
            ViewType.LIST.ordinal -> NoteVH.create(parent)
            else -> NoteVHGrid.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(holder.adapterPosition)

        if (holder is NoteVH) {
            holder.bind(
                item,
                { if (canOpenDetail) openDetail(item.note) },
                { selectNote(item.uid, it) }
            )
        } else if (holder is NoteVHGrid) {
            holder.bind(
                item,
                { if (canOpenDetail) openDetail(item.note) },
                { selectNote(item.uid, it) }
            )
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

            if (holder is NoteVH) holder.bindPayload(item)
            else if (holder is NoteVHGrid) holder.bindPayload(item)
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<NoteModifyState>() {
        override fun areItemsTheSame(oldItem: NoteModifyState, newItem: NoteModifyState): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: NoteModifyState, newItem: NoteModifyState): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: NoteModifyState, newItem: NoteModifyState): Any {
            return true
        }
    }
}

enum class ViewType {
    LIST,
    GRID
}
package com.example.notepad.ui.mainScreen.adapter

import android.content.res.ColorStateList
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.R
import com.example.notepad.data.model.Note
import com.example.notepad.data.utils.showCurrentDate
import com.example.notepad.databinding.ItemNoteVerticalBinding
import com.example.notepad.ui.mainScreen.NoteModifyState
import com.example.notepad.ui.mainScreen.NoteSelectState

class NoteVH(val binding: ItemNoteVerticalBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(noteState: NoteModifyState?, click: () -> Unit, selectNote: (isChecked: Boolean) -> Unit) {
        if (noteState == null) return

        val bgColor = if (noteState.note.bgColor == R.color.white) R.color.grayColor else noteState.note.bgColor

        with(binding) {
            itemLayout.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(itemView.context, bgColor))
            title.isVisible = noteState.note.title.isNotEmpty()
            description.isVisible = noteState.note.description.isNotEmpty()

            showBlurDescription(noteState)

            title.text = noteState.note.title
            description.text = noteState.note.description
            pinImage.isVisible = noteState.note.isPinned
            timeText.text = showCurrentDate(noteState.note.date.toLong())
            lockImage.isInvisible = noteState.note.password.isEmpty() == true
            select.isVisible = noteState.selectState != NoteSelectState.IDLE
            select.isChecked = noteState.selectState == NoteSelectState.SELECT

            itemLayout.setOnClickListener { click() }
            select.setOnClickListener {
                selectNote(select.isChecked)
            }
        }
    }

    fun bindPayload(noteState: NoteModifyState?) {
        if (noteState == null) return
        showBlurDescription(noteState)
        with(binding) {
            timeText.text = showCurrentDate(noteState.note.date.toLong())
            lockImage.isInvisible = noteState.note.password.isEmpty() == true
            pinImage.isVisible = noteState.note.isPinned
            select.isVisible = noteState.selectState != NoteSelectState.IDLE
            select.isChecked = noteState.selectState == NoteSelectState.SELECT
        }
    }

    private fun showBlurDescription(noteState: NoteModifyState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (noteState.note.description.isNotEmpty() && noteState.note.password.isNotEmpty()) {
                binding.description.setRenderEffect(
                    RenderEffect.createBlurEffect(
                        20.0f, 20.0f, Shader.TileMode.CLAMP
                    )
                )
            }else binding.description.setRenderEffect(null)
        }
    }

    companion object {
        fun create(parent: ViewGroup): NoteVH {
            val binding =
                ItemNoteVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NoteVH(binding)
        }
    }
}
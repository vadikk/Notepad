package com.example.notepad.ui.createNote

import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.createChooser
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notepad.R
import com.example.notepad.data.utils.TextChangeWatcher
import com.example.notepad.data.utils.collectWithLifecycleState
import com.example.notepad.data.utils.showCurrentDate
import com.example.notepad.data.utils.showKeyBoard
import com.example.notepad.databinding.FragmentCreateNoteBinding
import com.example.notepad.domain.usecase.PasswordType
import com.example.notepad.ui.colorPicker.ColorPickerDialog
import com.example.notepad.ui.folderChoose.FolderChooseFragment
import com.example.notepad.ui.password.PasswordFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CreateNoteFragment : Fragment() {

    private val createNoteVM: CreateNoteVM by viewModels()
    private var binding: FragmentCreateNoteBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateNoteBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        initResultListener()

        binding?.toolbar?.inflateMenu(R.menu.note_menu)
        showKeyBoard(context, binding?.description)

        viewLifecycleOwner.collectWithLifecycleState(createNoteVM.viewState) { note ->
            showUI(note)
        }

        viewLifecycleOwner.collectWithLifecycleState(createNoteVM.effect) {
            when (it) {
                is NoteEffect.CloseScreen -> {
                    findNavController().navigateUp()
                }
            }
        }

        binding?.titleText?.addTextChangedListener(
            TextChangeWatcher { createNoteVM.setEvent(NoteEvent.ChangeTitle(it.toString())) }
        )
        binding?.description?.addTextChangedListener(
            TextChangeWatcher { createNoteVM.setEvent(NoteEvent.ChangeDescription(it.toString())) }
        )
        binding?.applyImage?.setOnClickListener { createNoteVM.setEvent(NoteEvent.Save) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showUI(createNoteState: CreateNoteState?) {
        binding?.timeText?.text =
            if (createNoteState?.date?.isNotEmpty() == true) showCurrentDate(
                createNoteState.date.toLong()
            )
            else showCurrentDate(Calendar.getInstance().timeInMillis)
        binding?.titleText?.setText(createNoteState?.title.orEmpty())
        binding?.titleText?.setSelection(binding?.titleText?.text?.length ?: 0)
        binding?.description?.setText(createNoteState?.description.orEmpty())
        binding?.description?.setSelection(binding?.description?.text?.length ?: 0)
        binding?.parentContainer?.setBackgroundColor(
            ContextCompat.getColor(requireContext(), createNoteState?.color ?: R.color.white)
        )
        binding?.pinImage?.isVisible = createNoteState?.isPinned ?: false
        binding?.applyImage?.isVisible = createNoteState?.isApplyBtnEnabled ?: false
        checkPasswordType(binding?.toolbar?.menu?.findItem(R.id.password))
    }

    private fun initClickListener() {
        binding?.toolbar?.setNavigationOnClickListener { findNavController().navigateUp() }
        binding?.toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share -> {
                    shareNote()
                    true
                }
                R.id.move -> {
                    findNavController().navigate(R.id.action_createNoteFragment_to_folderChooseFragment)
                    true
                }
                R.id.delete -> {
                    createNoteVM.setEvent(NoteEvent.Delete)
                    true
                }
                R.id.bg -> {
                    showColorPickerScreen(createNoteVM.currentState.color)
                    true
                }
                R.id.password -> {
                    val passwordType = checkPasswordType(menuItem)

                    findNavController().navigate(
                        R.id.action_createNoteFragment_to_passwordFragment,
                        PasswordFragment.bundle(
                            createNoteVM.currentNote?.password.orEmpty(),
                            passwordType.value
                        )
                    )
                    true
                }
                else -> false
            }
        }
    }

    private fun initResultListener() {
        parentFragmentManager.setFragmentResultListener(
            ColorPickerDialog.SELECT_COLOR,
            viewLifecycleOwner
        ) { _, bundle ->
            val newColor = bundle.getInt(ColorPickerDialog.COLOR_ITEM)
            createNoteVM.setEvent(NoteEvent.ChangeColor(newColor))
        }

        parentFragmentManager.setFragmentResultListener(
            PasswordFragment.SET_PASSWORD,
            viewLifecycleOwner
        ) { _, bundle ->
            val refreshPassword = bundle.getString(PasswordFragment.PASSWORD, "")
            createNoteVM.setEvent(NoteEvent.ChangePassword(refreshPassword))
        }

        parentFragmentManager.setFragmentResultListener(
            FolderChooseFragment.FOLDER_CHOOSE,
            viewLifecycleOwner
        ) { _, bundle ->
            val folderUid = bundle.getString(FolderChooseFragment.FOLDER_UID, "")
            createNoteVM.setEvent(NoteEvent.Folder(folderUid))
        }
    }

    private fun showColorPickerScreen(color: Int) {
        val action = CreateNoteFragmentDirections.actionCreateNoteFragmentToColorPickerDialog(color)
        findNavController().navigate(action)
    }

    private fun shareNote() {
        val title = createNoteVM.currentState.title.trim()
        val description = createNoteVM.currentState.description.trim()
        val finalText =
            if (title.isNotEmpty() && description.isNotEmpty()) title + "\n" + description
            else title + description

        val intent = Intent(ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, finalText)
            type = "text/plain"
        }
        startActivity(createChooser(intent, "Share Note"))
    }

    private fun checkPasswordType(menuItem: MenuItem?): PasswordType {
        val passwordType =
            if (createNoteVM.currentNote == null ||
                createNoteVM.currentNote?.password?.isEmpty() == true
            ) PasswordType.APPLY
            else PasswordType.REMOVE

        menuItem?.title =
            if (passwordType == PasswordType.APPLY) "Set password"
            else "Remove password"

        return passwordType
    }

    companion object {
        private const val NOTE_UID = "NOTE_UID"
    }
}
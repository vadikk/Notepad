package com.example.notepad.ui.mainScreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notepad.R
import com.example.notepad.data.model.Note
import com.example.notepad.data.utils.collectWithLifecycleState
import com.example.notepad.data.utils.hideKeyBoard
import com.example.notepad.databinding.FragmentMainScreenBinding
import com.example.notepad.domain.usecase.PasswordType
import com.example.notepad.ui.mainScreen.adapter.NoteAdapter
import com.example.notepad.ui.mainScreen.adapter.NoteItemDecoration
import com.example.notepad.ui.password.PasswordFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainScreenFragment : Fragment() {

    private val mainScreenVM by viewModels<MainScreenVM>()
    private var adapter: NoteAdapter? = null
    private var layoutManager: StaggeredGridLayoutManager? = null
    private var binding: FragmentMainScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreenVM.init(TypeScreen.MAIN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.toolbar?.inflateMenu(R.menu.main_screen_menu)
        initClickListener()

        layoutManager =
            StaggeredGridLayoutManager(mainScreenVM.spanCount, StaggeredGridLayoutManager.VERTICAL)
        adapter = NoteAdapter(
            layoutManager,
            { openEditNote(it) },
            { uid, isChecked -> mainScreenVM.selectNote(uid, isChecked) }
        )

        binding?.noteRV?.also {
            it.layoutManager = layoutManager
            it.adapter = adapter
            it.addItemDecoration(NoteItemDecoration())
        }

        viewLifecycleOwner.collectWithLifecycleState(mainScreenVM.notesState) {
            adapter?.submitList(it)
        }
        viewLifecycleOwner.collectWithLifecycleState(mainScreenVM.selectCount) {
            binding?.selectNumber?.text = "${it.selectCount} selected"
            binding?.selectAll?.isChecked = it.isSelectAll
            binding?.delete?.isEnabled = it.selectCount > 0
            binding?.pin?.isEnabled = it.selectCount > 0
        }
        viewLifecycleOwner.collectWithLifecycleState(mainScreenVM.folderName) {
            binding?.folder?.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun openEditNote(note: Note?) {
        if (note == null || note.password.isEmpty()) {
            val action = MainScreenFragmentDirections.actionMainScreenFragmentToCreateNoteFragment(
                note?.uid.toString()
            )
            findNavController().navigate(action)
        } else {
            findNavController().navigate(
                R.id.action_mainScreenFragment_to_passwordFragment,
                PasswordFragment.bundle(
                    note.uid.toString(),
                    note.password.orEmpty(),
                    PasswordType.CONFIRM.value
                )
            )
        }
    }

    private fun editNote() {
        if (binding?.toolbar?.isVisible == true) {
            binding?.toolbar?.isInvisible = true
            binding?.editLayout?.isVisible = true
            mainScreenVM.selectAll(NoteSelectState.NOT_SELECT)
            binding?.fab?.isInvisible = true
            adapter?.changeCanOpenDetailMode(false)
        }
    }

    private fun initClickListener() {
        binding?.toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.editLM -> {
                    if (layoutManager?.spanCount == 1) {
                        mainScreenVM.spanCount = 2
                        layoutManager?.spanCount = mainScreenVM.spanCount
                        menuItem.title = "List view"
                    } else {
                        mainScreenVM.spanCount = 1
                        layoutManager?.spanCount = mainScreenVM.spanCount
                        menuItem.title = "Grid view"
                    }
                    true
                }
                R.id.edit -> {
                    editNote()
                    true
                }
                else -> false
            }
        }
        binding?.cancelEdit?.setOnClickListener { cancelEdit() }
        binding?.selectAll?.setOnClickListener {
            val selectState = if (binding?.selectAll?.isChecked == true) NoteSelectState.SELECT else NoteSelectState.NOT_SELECT
            mainScreenVM.selectAll(selectState)
        }

        val searchView = binding?.toolbar?.menu?.findItem(R.id.search)?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    hideKeyBoard(context, searchView)
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    mainScreenVM.searchContent(p0.orEmpty())
                    return true
                }
            })
        binding?.fab?.hide()
        binding?.fab?.postDelayed({ binding?.fab?.show() }, 250)
        binding?.fab?.setOnClickListener { openEditNote(null) }
        binding?.delete?.setOnClickListener { mainScreenVM.deleteNotes{ cancelEdit() } }
        binding?.pin?.setOnClickListener { mainScreenVM.pinNotes{ cancelEdit() } }
        binding?.folder?.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreenFragment_to_folderListFragment)
        }
    }

    private fun cancelEdit() {
        if (binding?.editLayout?.isVisible == true) {
            binding?.toolbar?.isVisible = true
            binding?.editLayout?.isInvisible = true
            mainScreenVM.selectAll(NoteSelectState.IDLE)
            binding?.fab?.isInvisible = false
            mainScreenVM.clearSelectNotes()
            adapter?.changeCanOpenDetailMode(true)
        }
    }

}